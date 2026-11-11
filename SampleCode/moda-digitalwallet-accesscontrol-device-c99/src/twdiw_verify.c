/*
 * twdiw_verify.c — 可攜驗證核心實作
 *
 * 串接既有服務,全程僅透過 twdiw_platform_t 取用硬體能力(ECDH / RTC / HMAC 金鑰),
 * 核心不接觸任何金鑰裸位元組。TOTP 與 HMAC 皆以平台函式指標重算後比對。
 */
#include "twdiw_verify.h"

#include "lib/base64.h"        /* base64_encode */
#include "lib/hmac_sha256.h"   /* HMAC_SHA256_LEN */
#include <string.h>        /* memset、memcpy、strcmp、strstr、strchr、strlen */
#include <stdio.h>         /* snprintf */

/* TOTP 常數,與 totp.c / Java TOTPService 一致 */
#define TWDIW_TOTP_TIME_OFFSET 30   /* 時間位移量(秒) */
#define TWDIW_TOTP_PERIOD      60   /* 時窗週期(秒) */

/*
 * 從(已 '\0' 結尾的)內層明文中取出字串欄位值,處理常見跳脫序列。
 * 作法與 qr_parse 的取值一致,但此處為內層 JSON 專用的區域函式。
 * @return 0 成功、-1 找不到 / 格式錯誤、-2 值超過緩衝上限
 */
static int inner_get_string(const char *obj, const char *key,
                            char *out, size_t cap)
{
    char needle[32];      /* 形如 "key" 的搜尋樣式 */
    const char *p;        /* 掃描指標 */
    size_t o;             /* 輸出索引 */
    size_t klen;          /* 鍵名長度 */

    klen = strlen(key);
    if (klen + 3 > sizeof(needle)) {
        return -1;
    }
    needle[0] = '"';
    memcpy(needle + 1, key, klen);
    needle[1 + klen] = '"';
    needle[2 + klen] = '\0';

    p = strstr(obj, needle);
    if (p == NULL) {
        return -1;
    }
    p += strlen(needle);
    p = strchr(p, ':');
    if (p == NULL) {
        return -1;
    }
    p++;
    while (*p == ' ' || *p == '\t' || *p == '\r' || *p == '\n') {
        p++;
    }
    if (*p != '"') {
        return -1;
    }
    p++;

    o = 0;
    while (*p != '\0' && *p != '"') {
        char c = *p;
        if (c == '\\') {
            p++;
            if (*p == '\0') {
                break;
            }
            switch (*p) {
                case 'n':  c = '\n'; break;
                case 't':  c = '\t'; break;
                case 'r':  c = '\r'; break;
                case '"':  c = '"';  break;
                case '\\': c = '\\'; break;
                case '/':  c = '/';  break;
                default:   c = *p;   break;
            }
        }
        if (o + 1 >= cap) {
            return -2;
        }
        out[o++] = c;
        p++;
    }
    out[o] = '\0';
    return 0;
}

/*
 * 常數時間字串比對('\0' 結尾)。
 *
 * 比對耗時僅取決於字串長度,不因「相符前綴多長」而提早結束,避免攻擊者
 * 以 timing side-channel 逐位元組逼近正確的 HMAC / TOTP 值(選擇密文攻擊)。
 * 長度本身屬公開的格式資訊(HMAC Base64 固定 44 字元、TOTP 固定 6 位數),
 * 非祕密,故長度不等時可直接回傳不相符。
 *
 * @return 1 表示兩字串完全相同、0 表示不同
 */
static int constant_time_streq(const char *a, const char *b)
{
    size_t la;        /* a 的長度 */
    size_t lb;        /* b 的長度 */
    size_t i;         /* 迴圈索引 */
    uint8_t diff;     /* 累積差異位元(全程不 early-return) */

    la = strlen(a);
    lb = strlen(b);
    if (la != lb) {
        return 0;
    }

    diff = 0;
    for (i = 0; i < la; i++) {
        /* 逐位元組 XOR 後以 OR 累積,任一位元不同 diff 即非 0 */
        diff = (uint8_t)(diff | (uint8_t)((uint8_t)a[i] ^ (uint8_t)b[i]));
    }
    return (diff == 0) ? 1 : 0;
}

/*
 * 以平台的 totpKey 產生某一時窗的 TOTP 碼(6 位數)。
 * 邏輯與 totp.c 完全相同,唯一差異是 HMAC 改由 platform->hmac_totp_key 計算,
 * 金鑰因此留在平台層。
 * @return 0 成功、-1 平台 HMAC 失敗
 */
static int totp_code_via_platform(const twdiw_platform_t *plat,
                                  uint64_t epoch_sec, int offset,
                                  char out[TOTP_CODE_BUFLEN])
{
    int64_t time_step;                 /* 時間區間 */
    uint64_t step_bits;                /* 大端輸出用位元樣式 */
    uint8_t msg[8];                    /* 8-byte big-endian 時間區間 */
    uint8_t hash[TWDIW_HMAC_OUT_LEN];  /* HMAC 輸出 */
    int idx;                           /* 動態截斷索引 */
    uint32_t binary;                   /* 截斷後 31 位元整數 */
    uint32_t otp;                      /* 取模後 6 位數 */
    int i;                             /* 迴圈索引 */
    int rc;                            /* 平台呼叫回傳 */

    /* timeStep = (epoch + 30) / 60 + offset(整數除法,與 Java 一致) */
    time_step = (int64_t)((epoch_sec + (uint64_t)TWDIW_TOTP_TIME_OFFSET)
                          / (uint64_t)TWDIW_TOTP_PERIOD);
    time_step += offset;

    /* 以 8-byte big-endian 寫入(等同 Java ByteBuffer.putLong) */
    step_bits = (uint64_t)time_step;
    for (i = 0; i < 8; i++) {
        msg[i] = (uint8_t)((step_bits >> (56 - i * 8)) & 0xFF);
    }

    /* HMAC-SHA256(totpKey, msg) —— 金鑰綁在平台層 */
    rc = plat->hmac_totp_key(msg, sizeof(msg), hash);
    if (rc != TWDIW_OK) {
        return -1;
    }

    /* 動態截斷,idx 取自最後一個位元組的低 4 位 */
    idx = hash[TWDIW_HMAC_OUT_LEN - 1] & 0x0f;
    binary = (((uint32_t)hash[idx] & 0x7f) << 24)
           | (((uint32_t)hash[idx + 1] & 0xff) << 16)
           | (((uint32_t)hash[idx + 2] & 0xff) << 8)
           | ((uint32_t)hash[idx + 3] & 0xff);

    otp = binary % 1000000u;
    snprintf(out, TOTP_CODE_BUFLEN, "%06u", otp);

    memset(hash, 0, sizeof(hash));
    return 0;
}

/*
 * 驗證內層 TOTP 碼:掃描 offset ∈ {-1,0,+1},任一時窗相符即通過。
 * @return 1 通過、0 不符、-1 平台 HMAC 失敗
 */
static int totp_verify_via_platform(const twdiw_platform_t *plat,
                                    uint64_t epoch_sec, const char *code)
{
    char candidate[TOTP_CODE_BUFLEN];  /* 逐時窗重算的碼 */
    int i;                             /* 偏移索引 */
    int rc;                            /* 產碼回傳 */

    for (i = -1; i <= 1; i++) {
        rc = totp_code_via_platform(plat, epoch_sec, i, candidate);
        if (rc != 0) {
            return -1;
        }
        /* 常數時間比對,避免以 timing 逐位元組猜測正確 TOTP 碼 */
        if (constant_time_streq(candidate, code) == 1) {
            return 1;
        }
    }
    return 0;
}

/* 設定結果原因碼並回傳「是否通過」布林 */
static int finish(twdiw_verify_result_t *result, int reason)
{
    result->reason = reason;
    return (reason == TWDIW_VERIFY_OK) ? 1 : 0;
}

/* 端到端驗證 */
int twdiw_verify(const char *qr_json,
                 const twdiw_platform_t *plat,
                 twdiw_verify_result_t *result)
{
    qr_fields_t fields;                            /* 解析出的 t/d/h/k */
    char inner[TWDIW_VERIFY_PLAINTEXT_MAX + 1];    /* 明文的 '\0' 結尾副本 */
    uint8_t mac[HMAC_SHA256_LEN];                  /* h 重算的原始 HMAC */
    char mac_b64[64];                              /* HMAC 的 Base64 */
    uint64_t epoch;                                /* 平台時間 */
    int rc;                                        /* 各步驟回傳 */
    int tv;                                        /* TOTP 驗證結果 */

    /* 參數檢查 */
    if (result == NULL) {
        return 0;
    }
    memset(result, 0, sizeof(*result));
    if (qr_json == NULL || plat == NULL ||
        plat->ecdh_x25519 == NULL || plat->hmac_verify_key == NULL ||
        plat->hmac_totp_key == NULL || plat->now_epoch_sec == NULL) {
        return finish(result, TWDIW_VERIFY_ERR_PARAM);
    }

    /* 步驟一:解析 QR 頂層四欄 */
    rc = qr_parse(qr_json, &fields);
    if (rc != QR_PARSE_OK) {
        return finish(result, TWDIW_VERIFY_ERR_QR_JSON);
    }
    /* 保留 t/k 供診斷(長度已由 qr_parse 上限保證) */
    memcpy(result->t, fields.t, sizeof(result->t));
    memcpy(result->k, fields.k, sizeof(result->k));

    /* 步驟二:解密 d —— ECDH 走平台指標,私鑰不進核心 */
    rc = ecc_decrypt_via_platform(fields.d, plat,
                                  result->plaintext,
                                  TWDIW_VERIFY_PLAINTEXT_MAX,
                                  &result->plaintext_len);
    if (rc == ECC_ERR_ECDH) {
        return finish(result, TWDIW_VERIFY_ERR_ECDH);
    }
    if (rc == ECC_ERR_AUTH) {
        return finish(result, TWDIW_VERIFY_ERR_DECRYPT);
    }
    if (rc < 0) {
        /* 其餘皆為封包格式 / Base64 / 邊界問題 */
        return finish(result, TWDIW_VERIFY_ERR_PACKET);
    }

    /* 步驟三:把明文複製成 '\0' 結尾字串以解析內層 JSON */
    memcpy(inner, result->plaintext, result->plaintext_len);
    inner[result->plaintext_len] = '\0';

    /* 取出內層 "totp" 欄位 */
    rc = inner_get_string(inner, "totp", result->totp, sizeof(result->totp));
    if (rc != 0) {
        memset(inner, 0, sizeof(inner));
        return finish(result, TWDIW_VERIFY_ERR_INNER);
    }

    /* 步驟四:TOTP 驗證(平台時間 + totpKey,掃描 ±1 時窗) */
    epoch = plat->now_epoch_sec();
    tv = totp_verify_via_platform(plat, epoch, result->totp);
    if (tv != 1) {
        memset(inner, 0, sizeof(inner));
        return finish(result, TWDIW_VERIFY_ERR_TOTP);
    }

    /* 步驟五:HMAC(h)驗證 —— 對「解密後明文」以 hmacKey 重算,Base64 後比對 */
    rc = plat->hmac_verify_key(result->plaintext, result->plaintext_len, mac);
    if (rc != TWDIW_OK) {
        memset(inner, 0, sizeof(inner));
        return finish(result, TWDIW_VERIFY_ERR_HMAC);
    }
    if (base64_encode(mac, HMAC_SHA256_LEN, mac_b64, sizeof(mac_b64)) < 0) {
        memset(inner, 0, sizeof(inner));
        return finish(result, TWDIW_VERIFY_ERR_HMAC);
    }
    /* 常數時間比對:h 來自不可信 QR,避免以 timing 逐位元組逼近正確 HMAC */
    if (constant_time_streq(mac_b64, fields.h) != 1) {
        memset(inner, 0, sizeof(inner));
        return finish(result, TWDIW_VERIFY_ERR_HMAC);
    }

    /* 全部通過。清零區域明文副本(result->plaintext 由呼叫端負責生命週期) */
    memset(inner, 0, sizeof(inner));
    return finish(result, TWDIW_VERIFY_OK);
}
