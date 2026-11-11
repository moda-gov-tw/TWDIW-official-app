/*
 * twdiw_tester.c — 廠商互動式解密測試工具
 *
 * 流程:
 *   1. 讀取設定檔(預設 tester_config.txt)中的三把金鑰;每把可填 Base64 或 hex 其一,
 *      程式自動換算另一種形式並印出,兩種都填則檢查是否一致。
 *   2. 在 terminal 貼上數位憑證皮夾QR Code反掃測試頁面產生的 QR JSON({"t":..,"d":..,"h":..,"k":..})。
 *   3. 正向:分別以「hex 形式金鑰」(路徑 A)與「Base64 形式金鑰」(路徑 B)
 *      各自獨立解碼、注入平台、呼叫 twdiw_verify(),兩次皆須通過且明文逐位元組相同。
 *   4. 負向:自動產生 10 種竄改版本(翻轉 h / 密文 / tag / nonce / 公鑰、改長度欄位、
 *      截短封包、刪除 h、時間超出時窗),列出竄改後的 QR 與差異位置,
 *      並檢查每一筆都在「預期的那一關」被擋下。
 *   5. 結果同時寫入 report_YYYYMMDD_HHMMSS.txt;全部符合預期回傳 0,否則回傳 1。
 *
 * 用法:
 *   twdiw_tester [設定檔] [--report-full]
 *   --report-full:報告檔也寫入金鑰原文(預設報告檔只寫 SHA-256 指紋,畫面則完整顯示)
 *
 * 僅依賴 C 標準庫,固定靜態緩衝,無動態配置。
 */
#include <stdio.h>
#include <string.h>
#include <stdint.h>
#include <stddef.h>
#include <stdlib.h>
#include <stdarg.h>
#include <time.h>

#include "../src/lib/base64.h"
#include "../src/lib/der_x25519.h"
#include "../src/lib/x25519.h"
#include "../src/lib/sha256.h"
#include "../src/lib/chacha20_poly1305.h"
#include "../src/lib/qr_parse.h"
#include "../src/lib/ecc_service.h"
#include "../src/twdiw_platform.h"
#include "../src/twdiw_verify.h"

#define FILE_BUF_MAX (64 * 1024)
#define FIELD_MAX    4096
#define LINE_BUF_MAX 8192
#define QR_OUT_MAX   (QR_D_MAX + QR_H_MAX + 2 * (QR_TYPE_MAX + QR_KID_MAX) + 64)
#define PACKET_MAX   2048

/* 封包各區段起點:[長度 4][公鑰 DER 44 = 前綴 12 + 裸 32][nonce 12][密文][tag 16] */
#define PKT_OFF_PUB     4
#define PKT_OFF_PUBRAW  (PKT_OFF_PUB + DER_X25519_PUB_PREFIX_LEN)
#define PKT_OFF_NONCE   (PKT_OFF_PUB + DER_X25519_PUB_LEN)
#define PKT_OFF_CT      (PKT_OFF_NONCE + CHACHA20_NONCE_LEN)

#define TIME_SKEW_SEC   600   /* N10 使用的時間位移 */
#define DIFF_WINDOW     24    /* 差異顯示時,變動點前後各顯示幾個字元 */

/* ---------- 全域狀態 ---------- */

static char g_file_buf[FILE_BUF_MAX];
static char g_line[LINE_BUF_MAX];

static FILE *g_report = NULL;   /* 報告檔(開不了時為 NULL,只輸出畫面) */
static int g_report_full = 0;   /* 1 = 報告檔也寫入金鑰原文 */

/* 注入用時間來源 */
static uint64_t g_now_epoch = 0;
static uint64_t fixed_now(void) { return g_now_epoch; }

/* 設定檔的 now=(0 表示未指定,每輪取本機時間) */
static int g_cfg_now_set = 0;
static uint64_t g_cfg_now = 0;

/* 一把金鑰的兩種形式 */
typedef struct {
    const char *name;     /* 顯示名稱 */
    char b64[FIELD_MAX];  /* Base64 形式(私鑰可能是 PKCS#8 DER 或裸 32B) */
    char hex[FIELD_MAX];  /* hex 形式(一律為裸位元組) */
    int given_b64;        /* 設定檔是否提供 Base64 */
    int given_hex;        /* 設定檔是否提供 hex */
    const char *b64_kind; /* Base64 解出的型態說明 */
} key_forms_t;

static key_forms_t g_priv = { "裝置私鑰", {0}, {0}, 0, 0, "" };
static key_forms_t g_totp = { "totpKey",  {0}, {0}, 0, 0, "" };
static key_forms_t g_hmac = { "hmacKey",  {0}, {0}, 0, 0, "" };

/* 大型結構放靜態區,避免吃 stack */
static twdiw_verify_result_t g_res_a;
static twdiw_verify_result_t g_res_b;
static twdiw_verify_result_t g_res_n;
static qr_fields_t g_fields;
static uint8_t g_packet[PACKET_MAX];
static uint8_t g_tpkt[PACKET_MAX];
static char g_td[QR_D_MAX + 1];
static char g_th[QR_H_MAX + 1];
static char g_qr_out[QR_OUT_MAX];

/* ---------- 輸出(畫面 + 報告檔) ---------- */

/* 同時輸出到畫面與報告檔 */
static void out(const char *fmt, ...)
{
    va_list ap;
    va_start(ap, fmt);
    vprintf(fmt, ap);
    va_end(ap);
    if (g_report != NULL) {
        va_start(ap, fmt);
        vfprintf(g_report, fmt, ap);
        va_end(ap);
    }
}

/* 只輸出到畫面 */
static void out_screen(const char *fmt, ...)
{
    va_list ap;
    va_start(ap, fmt);
    vprintf(fmt, ap);
    va_end(ap);
}

/* 只輸出到報告檔 */
static void out_report(const char *fmt, ...)
{
    va_list ap;
    if (g_report == NULL) { return; }
    va_start(ap, fmt);
    vfprintf(g_report, fmt, ap);
    va_end(ap);
}

/* 金鑰原文:畫面完整顯示;報告檔除非 --report-full,否則以指紋取代 */
static void out_secret(const char *label, const char *value, const char *suffix,
                       const char *fingerprint)
{
    out_screen("%s%s  %s\n", label, value, suffix);
    if (g_report_full) {
        out_report("%s%s  %s\n", label, value, suffix);
    } else {
        out_report("%s(已遮蔽,指紋 %s)  %s\n", label, fingerprint, suffix);
    }
}

/* ---------- 字串 / 編碼工具 ---------- */

static void rstrip(char *s)
{
    size_t n = strlen(s);
    while (n > 0) {
        char c = s[n - 1];
        if (c == '\r' || c == '\n' || c == ' ' || c == '\t') { s[--n] = '\0'; }
        else { break; }
    }
}

static char *lstrip(char *s)
{
    while (*s == ' ' || *s == '\t') { s++; }
    return s;
}

static int hex_nibble(char c)
{
    if (c >= '0' && c <= '9') { return c - '0'; }
    if (c >= 'a' && c <= 'f') { return c - 'a' + 10; }
    if (c >= 'A' && c <= 'F') { return c - 'A' + 10; }
    return -1;
}

/* hex 字串 → 位元組;失敗回傳 (size_t)-1 */
static size_t hex_to_bytes(const char *hex, uint8_t *out_buf, size_t out_cap)
{
    size_t n = 0;
    const char *p = hex;
    while (p[0] != '\0' && p[1] != '\0') {
        int hi = hex_nibble(p[0]);
        int lo = hex_nibble(p[1]);
        if (hi < 0 || lo < 0) { return (size_t)-1; }
        if (n >= out_cap) { return (size_t)-1; }
        out_buf[n++] = (uint8_t)((hi << 4) | lo);
        p += 2;
    }
    if (p[0] != '\0') { return (size_t)-1; }
    return n;
}

/* 位元組 → 小寫 hex 字串 */
static void bytes_to_hex(const uint8_t *buf, size_t len, char *out_buf, size_t cap)
{
    static const char digits[] = "0123456789abcdef";
    size_t i;
    if (cap < len * 2 + 1) { out_buf[0] = '\0'; return; }
    for (i = 0; i < len; i++) {
        out_buf[i * 2]     = digits[buf[i] >> 4];
        out_buf[i * 2 + 1] = digits[buf[i] & 0x0f];
    }
    out_buf[len * 2] = '\0';
}

/* 金鑰指紋:SHA-256 前 8 位元組的 hex */
static void fingerprint(const uint8_t *buf, size_t len, char out_buf[17])
{
    uint8_t digest[SHA256_DIGEST_LEN];
    sha256(buf, len, digest);
    bytes_to_hex(digest, 8, out_buf, 17);
}

/* 原因碼 → 標籤 / 說明 */
static const char *reason_label(int r)
{
    switch (r) {
        case TWDIW_VERIFY_OK:          return "OK";
        case TWDIW_VERIFY_ERR_PARAM:   return "PARAM";
        case TWDIW_VERIFY_ERR_QR_JSON: return "QR_JSON";
        case TWDIW_VERIFY_ERR_PACKET:  return "PACKET";
        case TWDIW_VERIFY_ERR_ECDH:    return "ECDH";
        case TWDIW_VERIFY_ERR_DECRYPT: return "DECRYPT";
        case TWDIW_VERIFY_ERR_INNER:   return "INNER";
        case TWDIW_VERIFY_ERR_TOTP:    return "TOTP";
        case TWDIW_VERIFY_ERR_HMAC:    return "HMAC";
        default:                       return "UNKNOWN";
    }
}

static const char *reason_desc(int r)
{
    switch (r) {
        case TWDIW_VERIFY_OK:          return "通過";
        case TWDIW_VERIFY_ERR_PARAM:   return "參數錯誤";
        case TWDIW_VERIFY_ERR_QR_JSON: return "頂層 JSON 缺 d/h 或解析失敗";
        case TWDIW_VERIFY_ERR_PACKET:  return "d 封包 Base64/格式/邊界錯誤";
        case TWDIW_VERIFY_ERR_ECDH:    return "平台 ECDH 失敗";
        case TWDIW_VERIFY_ERR_DECRYPT: return "AEAD 標籤驗證失敗";
        case TWDIW_VERIFY_ERR_INNER:   return "內層無 totp 欄位";
        case TWDIW_VERIFY_ERR_TOTP:    return "TOTP 碼過期/不符";
        case TWDIW_VERIFY_ERR_HMAC:    return "HMAC(h)驗證不符";
        default:                       return "未知";
    }
}

/* ---------- 金鑰解碼 ---------- */

/*
 * 私鑰字串 → 裸 32 位元組。is_hex 指定字串格式。
 * 解出 48 位元組視為 PKCS#8 DER、32 位元組視為裸私鑰。
 * @return 0 成功;-1 編碼錯誤;-2 DER 前綴不符;-3 長度不符
 */
static int priv_from_string(const char *s, int is_hex, uint8_t raw[32], const char **kind)
{
    uint8_t buf[64];
    size_t n;
    int rc = 0;

    if (is_hex) {
        n = hex_to_bytes(s, buf, sizeof(buf));
        if (n == (size_t)-1) { return -1; }
    } else {
        if (base64_decode(s, buf, sizeof(buf), &n) != 0) { return -1; }
    }

    if (n == DER_X25519_PRIV_LEN) {
        if (der_x25519_private_to_raw(buf, n, raw) != DER_X25519_OK) { rc = -2; }
        if (kind != NULL) { *kind = "PKCS#8 DER 48 位元組"; }
    } else if (n == DER_X25519_RAW_LEN) {
        memcpy(raw, buf, DER_X25519_RAW_LEN);
        if (kind != NULL) { *kind = "裸私鑰 32 位元組"; }
    } else {
        rc = -3;
    }
    memset(buf, 0, sizeof(buf));
    return rc;
}

/*
 * totpKey / hmacKey 字串 → 位元組(長度 1..TWDIW_HMAC_KEY_MAX)。
 * @return 0 成功;-1 編碼錯誤;-3 長度不符
 */
static int secret_from_string(const char *s, int is_hex, uint8_t *out_buf, size_t *out_len)
{
    size_t n;
    if (is_hex) {
        n = hex_to_bytes(s, out_buf, TWDIW_HMAC_KEY_MAX);
        if (n == (size_t)-1) { return -1; }
    } else {
        if (base64_decode(s, out_buf, TWDIW_HMAC_KEY_MAX, &n) != 0) { return -1; }
    }
    if (n == 0) { return -3; }
    *out_len = n;
    return 0;
}

/*
 * 補齊一把金鑰的另一種形式,兩種都有時檢查一致。
 * is_priv=1 時走私鑰解碼(hex 形式一律存裸 32 位元組)。
 * @return 0 成功;-1 失敗(已印出原因)
 */
static int complete_key_forms(key_forms_t *k, int is_priv)
{
    uint8_t from_b64[TWDIW_HMAC_KEY_MAX];
    uint8_t from_hex[TWDIW_HMAC_KEY_MAX];
    size_t len_b64 = 0, len_hex = 0;
    const char *kind = "";
    int rc = 0;

    if (!k->given_b64 && !k->given_hex) {
        printf("[錯誤] 設定檔缺少 %s(請填 Base64 或 hex 其一)\n", k->name);
        return -1;
    }

    if (k->given_b64) {
        if (is_priv) {
            rc = priv_from_string(k->b64, 0, from_b64, &kind);
            len_b64 = DER_X25519_RAW_LEN;
        } else {
            rc = secret_from_string(k->b64, 0, from_b64, &len_b64);
            kind = "Base64";
        }
        if (rc != 0) {
            printf("[錯誤] %s 的 Base64 無法解碼或長度不符(rc=%d)%s\n", k->name, rc,
                   is_priv ? ",私鑰須為 PKCS#8 DER 48 位元組或裸 32 位元組" : "");
            goto fail;
        }
        k->b64_kind = kind;
    }
    if (k->given_hex) {
        if (is_priv) {
            rc = priv_from_string(k->hex, 1, from_hex, NULL);
            len_hex = DER_X25519_RAW_LEN;
        } else {
            rc = secret_from_string(k->hex, 1, from_hex, &len_hex);
        }
        if (rc != 0) {
            printf("[錯誤] %s 的 hex 無法解碼或長度不符(rc=%d)\n", k->name, rc);
            goto fail;
        }
    }

    if (k->given_b64 && k->given_hex) {
        /* 兩種都填:必須是同一串位元組 */
        if (len_b64 != len_hex || memcmp(from_b64, from_hex, len_b64) != 0) {
            printf("[錯誤] %s 的 Base64 與 hex 解出內容不一致,請確認是否貼錯\n", k->name);
            goto fail;
        }
        /* hex 一律改寫成裸位元組的正規形式 */
        bytes_to_hex(from_hex, len_hex, k->hex, sizeof(k->hex));
    } else if (k->given_b64) {
        bytes_to_hex(from_b64, len_b64, k->hex, sizeof(k->hex));
    } else {
        /* 只給 hex:換算出 Base64(私鑰以裸 32 位元組編碼) */
        if (base64_encode(from_hex, len_hex, k->b64, sizeof(k->b64)) < 0) {
            printf("[錯誤] %s 換算 Base64 失敗\n", k->name);
            goto fail;
        }
        k->b64_kind = is_priv ? "裸私鑰 32 位元組" : "Base64";
        /* hex 一律改寫成裸位元組的正規形式(私鑰若填 DER hex 也收斂為裸 32) */
        bytes_to_hex(from_hex, len_hex, k->hex, sizeof(k->hex));
    }

    memset(from_b64, 0, sizeof(from_b64));
    memset(from_hex, 0, sizeof(from_hex));
    return 0;

fail:
    memset(from_b64, 0, sizeof(from_b64));
    memset(from_hex, 0, sizeof(from_hex));
    return -1;
}

/*
 * 依指定形式(use_hex=1 用 hex、0 用 Base64)從字串重新解碼三把金鑰並注入平台。
 * 兩條路徑各自從字串解碼,不共用中間結果。
 */
static int inject_keys(int use_hex)
{
    uint8_t priv[DER_X25519_RAW_LEN];
    uint8_t totp[TWDIW_HMAC_KEY_MAX];
    uint8_t hmac[TWDIW_HMAC_KEY_MAX];
    size_t totp_len = 0, hmac_len = 0;
    int rc;

    rc = priv_from_string(use_hex ? g_priv.hex : g_priv.b64, use_hex, priv, NULL);
    if (rc == 0) { rc = secret_from_string(use_hex ? g_totp.hex : g_totp.b64, use_hex, totp, &totp_len); }
    if (rc == 0) { rc = secret_from_string(use_hex ? g_hmac.hex : g_hmac.b64, use_hex, hmac, &hmac_len); }
    if (rc == 0 && twdiw_platform_set_keys(priv, hmac, hmac_len, totp, totp_len) != TWDIW_OK) { rc = -1; }

    memset(priv, 0, sizeof(priv));
    memset(totp, 0, sizeof(totp));
    memset(hmac, 0, sizeof(hmac));
    return rc;
}

/* ---------- 設定檔 ---------- */

static void copy_value(char *dst, size_t cap, char *val)
{
    rstrip(val);
    snprintf(dst, cap, "%s", lstrip(val));
}

static int load_config(const char *path)
{
    FILE *fp;
    size_t nread;
    char *line;
    char now_str[64] = {0};

    fp = fopen(path, "rb");
    if (fp == NULL) {
        printf("[錯誤] 無法開啟設定檔:%s\n", path);
        printf("       請先複製 test 目錄下的 tester_config.txt.template 為 tester_config.txt 並填入金鑰\n");
        printf("       (未指定設定檔時,會依序尋找 ./tester_config.txt、test/tester_config.txt)\n");
        return -1;
    }
    nread = fread(g_file_buf, 1, FILE_BUF_MAX - 1, fp);
    g_file_buf[nread] = '\0';
    fclose(fp);

    line = strtok(g_file_buf, "\n");
    while (line != NULL) {
        char *p = lstrip(line);
        char *eq = strchr(p, '=');
        if (*p != '#' && eq != NULL) {
            char *key;
            char *val = eq + 1;
            *eq = '\0';
            key = p;
            rstrip(key);
            /* 舊欄位 priv / totp / hmac 視同 *_b64,可直接沿用 live.txt */
            if (strcmp(key, "priv_b64") == 0 || strcmp(key, "priv") == 0) {
                copy_value(g_priv.b64, sizeof(g_priv.b64), val);
                g_priv.given_b64 = (g_priv.b64[0] != '\0');
            } else if (strcmp(key, "priv_hex") == 0) {
                copy_value(g_priv.hex, sizeof(g_priv.hex), val);
                g_priv.given_hex = (g_priv.hex[0] != '\0');
            } else if (strcmp(key, "totp_b64") == 0 || strcmp(key, "totp") == 0) {
                copy_value(g_totp.b64, sizeof(g_totp.b64), val);
                g_totp.given_b64 = (g_totp.b64[0] != '\0');
            } else if (strcmp(key, "totp_hex") == 0) {
                copy_value(g_totp.hex, sizeof(g_totp.hex), val);
                g_totp.given_hex = (g_totp.hex[0] != '\0');
            } else if (strcmp(key, "hmac_b64") == 0 || strcmp(key, "hmac") == 0) {
                copy_value(g_hmac.b64, sizeof(g_hmac.b64), val);
                g_hmac.given_b64 = (g_hmac.b64[0] != '\0');
            } else if (strcmp(key, "hmac_hex") == 0) {
                copy_value(g_hmac.hex, sizeof(g_hmac.hex), val);
                g_hmac.given_hex = (g_hmac.hex[0] != '\0');
            } else if (strcmp(key, "now") == 0) {
                copy_value(now_str, sizeof(now_str), val);
            }
        }
        line = strtok(NULL, "\n");
    }
    memset(g_file_buf, 0, sizeof(g_file_buf));

    if (now_str[0] != '\0') {
        g_cfg_now = (uint64_t)strtoll(now_str, NULL, 10);
        g_cfg_now_set = 1;
    }

    if (complete_key_forms(&g_priv, 1) != 0) { return -1; }
    if (complete_key_forms(&g_totp, 0) != 0) { return -1; }
    if (complete_key_forms(&g_hmac, 0) != 0) { return -1; }
    return 0;
}

/* ---------- 金鑰展示 ---------- */

static void show_key(const key_forms_t *k)
{
    uint8_t raw[TWDIW_HMAC_KEY_MAX];
    size_t n;
    char fp[17];

    n = hex_to_bytes(k->hex, raw, sizeof(raw));
    if (n == (size_t)-1) { n = 0; }
    fingerprint(raw, n, fp);
    memset(raw, 0, sizeof(raw));

    out("[%s] %lu 位元組,指紋 SHA-256[0..8] = %s\n", k->name, (unsigned long)n, fp);
    out_secret("    hex    : ", k->hex, k->given_hex ? "← 設定檔" : "← 程式換算", fp);
    out_secret("    Base64 : ", k->b64, k->given_b64 ? "← 設定檔" : "← 程式換算", fp);
    out("    Base64 型態 : %s\n", k->b64_kind);
}

static void show_keys(void)
{
    uint8_t priv[DER_X25519_RAW_LEN];
    uint8_t pub[DER_X25519_RAW_LEN];
    uint8_t pub_der[DER_X25519_PUB_LEN];
    char pub_hex[DER_X25519_RAW_LEN * 2 + 1];
    char pub_b64[64];
    char pub_der_b64[96];

    out("---------------- 金鑰(hex 與 Base64 兩種形式) ----------------\n");
    show_key(&g_priv);
    show_key(&g_totp);
    show_key(&g_hmac);

    /* 由私鑰導出公鑰,供廠商與數位憑證皮夾QR Code反掃測試頁面後台登記的公鑰比對 */
    if (priv_from_string(g_priv.hex, 1, priv, NULL) == 0) {
        x25519_base(pub, priv);
        der_x25519_public_from_raw(pub, pub_der);
        bytes_to_hex(pub, sizeof(pub), pub_hex, sizeof(pub_hex));
        base64_encode(pub, sizeof(pub), pub_b64, sizeof(pub_b64));
        base64_encode(pub_der, sizeof(pub_der), pub_der_b64, sizeof(pub_der_b64));
        out("[裝置公鑰](由私鑰導出,請與數位憑證皮夾QR Code反掃測試頁面後台登記的公鑰比對)\n");
        out("    hex               : %s\n", pub_hex);
        out("    Base64(裸 32B)   : %s\n", pub_b64);
        out("    Base64(X.509 DER): %s\n", pub_der_b64);
    }
    memset(priv, 0, sizeof(priv));
    out("----------------------------------------------------------------\n\n");
}

/* ---------- QR 重組與差異顯示 ---------- */

/* 附加 JSON 跳脫後的字串;溢位回傳 -1 */
static int append_json_str(char *dst, size_t cap, size_t *pos, const char *s)
{
    while (*s != '\0') {
        char c = *s++;
        if (c == '"' || c == '\\') {
            if (*pos + 2 >= cap) { return -1; }
            dst[(*pos)++] = '\\';
        } else if (*pos + 1 >= cap) {
            return -1;
        }
        dst[(*pos)++] = c;
    }
    dst[*pos] = '\0';
    return 0;
}

static int append_raw(char *dst, size_t cap, size_t *pos, const char *s)
{
    size_t n = strlen(s);
    if (*pos + n + 1 > cap) { return -1; }
    memcpy(dst + *pos, s, n);
    *pos += n;
    dst[*pos] = '\0';
    return 0;
}

/* 以 t/d/h/k 重組 QR JSON;h 為 NULL 時省略 h 欄位 */
static int build_qr(const char *t, const char *d, const char *h, const char *k,
                    char *dst, size_t cap)
{
    size_t pos = 0;
    int rc = 0;
    dst[0] = '\0';
    rc |= append_raw(dst, cap, &pos, "{\"t\": \"");
    rc |= append_json_str(dst, cap, &pos, t);
    rc |= append_raw(dst, cap, &pos, "\", \"d\": \"");
    rc |= append_json_str(dst, cap, &pos, d);
    if (h != NULL) {
        rc |= append_raw(dst, cap, &pos, "\", \"h\": \"");
        rc |= append_json_str(dst, cap, &pos, h);
    }
    rc |= append_raw(dst, cap, &pos, "\", \"k\": \"");
    rc |= append_json_str(dst, cap, &pos, k);
    rc |= append_raw(dst, cap, &pos, "\"}");
    return (rc == 0) ? 0 : -1;
}

/* 顯示欄位字串的差異片段,以 ^ 標出變動位置 */
static void show_field_diff(const char *field, const char *before, const char *after)
{
    size_t la = strlen(before);
    size_t lb = strlen(after);
    size_t first, last, i, start, end;

    if (la != lb) {
        out("  %s 欄位長度:%lu → %lu 字元\n", field, (unsigned long)la, (unsigned long)lb);
        out("    after : %s\n", after);
        return;
    }
    for (first = 0; first < la && before[first] == after[first]; first++) { }
    if (first == la) {
        out("  %s 欄位:無差異\n", field);
        return;
    }
    last = first;
    for (i = first; i < la; i++) {
        if (before[i] != after[i]) { last = i; }
    }
    start = (first > DIFF_WINDOW) ? first - DIFF_WINDOW : 0;
    end = (last + DIFF_WINDOW + 1 < la) ? last + DIFF_WINDOW + 1 : la;

    out("  %s 欄位差異(第 %lu 個字元起):\n", field, (unsigned long)first + 1);
    out("    before: %s%.*s%s\n", start > 0 ? "..." : "",
        (int)(end - start), before + start, end < la ? "..." : "");
    out("    after : %s%.*s%s\n", start > 0 ? "..." : "",
        (int)(end - start), after + start, end < la ? "..." : "");
    out("            %s", start > 0 ? "   " : "");
    for (i = start; i < end; i++) {
        out("%c", (before[i] != after[i]) ? '^' : ' ');
    }
    out("\n");
}

/* ---------- 正向驗證 ---------- */

static int run_positive(const char *label, int use_hex, const char *qr,
                        uint64_t now, twdiw_verify_result_t *res)
{
    int accepted;

    if (inject_keys(use_hex) != 0) {
        out("[正向 %s] 使用 %s 金鑰:注入失敗\n", label, use_hex ? "hex" : "Base64");
        return 0;
    }
    g_now_epoch = now;
    twdiw_platform_set_time_source(fixed_now);
    accepted = twdiw_verify(qr, twdiw_platform_get(), res);
    twdiw_platform_clear_keys();

    if (accepted) {
        out("[正向 %s] 使用 %-6s 金鑰  ★ 通過\n", label, use_hex ? "hex" : "Base64");
        out("    明文   : %.*s\n", (int)res->plaintext_len, (const char *)res->plaintext);
        out("    TOTP   : %s    t=\"%s\"  k=\"%s\"\n", res->totp, res->t, res->k);
    } else {
        out("[正向 %s] 使用 %-6s 金鑰  ✘ 拒絕:%s(%s)\n", label, use_hex ? "hex" : "Base64",
            reason_label(res->reason), reason_desc(res->reason));
    }
    return accepted;
}

/* 正向卡在 TOTP 時,掃描 now±120 秒判斷是否僅為時間偏移 */
static void diagnose_totp(const char *qr, uint64_t base)
{
    static const int deltas[] = { -120, -90, -60, -30, 30, 60, 90, 120 };
    size_t i;

    out("  [診斷] 卡在 TOTP 時窗,掃描 now±120 秒…\n");
    if (inject_keys(1) != 0) { return; }
    twdiw_platform_set_time_source(fixed_now);
    for (i = 0; i < sizeof(deltas) / sizeof(deltas[0]); i++) {
        g_now_epoch = (uint64_t)((long long)base + deltas[i]);
        if (twdiw_verify(qr, twdiw_platform_get(), &g_res_n) == 1) {
            out("  ✔ 以 now=%llu(%+d 秒)即可通過 → 密碼流程正確,只是時間有偏移。\n",
                (unsigned long long)g_now_epoch, deltas[i]);
            out("    請確認本機時間,或把該 Unix 秒填入設定檔 now= 後重跑。\n");
            twdiw_platform_clear_keys();
            return;
        }
    }
    twdiw_platform_clear_keys();
    out("  ✘ 鄰近時窗皆不通過 → 可能 totpKey 填錯,或 QR 產生已超過約 2 分鐘。\n");
}

/* ---------- 負向驗證 ---------- */

enum {
    N_H_FLIP = 1, N_CT_FLIP, N_TAG_FLIP, N_NONCE_FLIP, N_PUB_FLIP,
    N_DER_FLIP, N_LEN_FIELD, N_TRUNCATE, N_DROP_H, N_TIME_SKEW
};

typedef struct {
    int kind;          /* 竄改種類 */
    const char *id;    /* 顯示編號 */
    const char *desc;  /* 說明 */
    int expect1;       /* 預期原因碼 */
    int expect2;       /* 可接受的第二原因碼(無則同 expect1) */
} neg_case_t;

static const neg_case_t g_neg_cases[] = {
    { N_H_FLIP,     "N1",  "h 欄位翻轉 1 bit",                 TWDIW_VERIFY_ERR_HMAC,    TWDIW_VERIFY_ERR_HMAC },
    { N_CT_FLIP,    "N2",  "d 密文區第 1 個 byte 翻轉 1 bit",   TWDIW_VERIFY_ERR_DECRYPT, TWDIW_VERIFY_ERR_DECRYPT },
    { N_TAG_FLIP,   "N3",  "d 的 tag 最後 1 個 byte 翻轉 1 bit", TWDIW_VERIFY_ERR_DECRYPT, TWDIW_VERIFY_ERR_DECRYPT },
    { N_NONCE_FLIP, "N4",  "d 的 nonce 第 1 個 byte 翻轉 1 bit", TWDIW_VERIFY_ERR_DECRYPT, TWDIW_VERIFY_ERR_DECRYPT },
    { N_PUB_FLIP,   "N5",  "d 的 ephemeral 公鑰翻轉 1 bit",     TWDIW_VERIFY_ERR_DECRYPT, TWDIW_VERIFY_ERR_ECDH },
    { N_DER_FLIP,   "N6",  "d 的公鑰 DER 前綴翻轉 1 bit",       TWDIW_VERIFY_ERR_PACKET,  TWDIW_VERIFY_ERR_PACKET },
    { N_LEN_FIELD,  "N7",  "d 的公鑰長度欄位 44 改為 45",       TWDIW_VERIFY_ERR_PACKET,  TWDIW_VERIFY_ERR_PACKET },
    { N_TRUNCATE,   "N8",  "d 封包截短為 20 位元組",            TWDIW_VERIFY_ERR_PACKET,  TWDIW_VERIFY_ERR_PACKET },
    { N_DROP_H,     "N9",  "刪除 h 欄位",                       TWDIW_VERIFY_ERR_QR_JSON, TWDIW_VERIFY_ERR_QR_JSON },
    { N_TIME_SKEW,  "N10", "QR 不變,驗證時間 +600 秒",          TWDIW_VERIFY_ERR_TOTP,    TWDIW_VERIFY_ERR_TOTP }
};

/* 翻轉封包某個 byte 的最低位元並記錄變動 */
static void flip_byte(uint8_t *pkt, size_t off, char *note, size_t cap)
{
    uint8_t before = pkt[off];
    pkt[off] = (uint8_t)(before ^ 0x01);
    snprintf(note, cap, "封包 byte#%lu:0x%02x → 0x%02x", (unsigned long)off, before, pkt[off]);
}

/*
 * 產生並執行一筆負向案例。
 * @return 1 = 依預期被拒絕;0 = 不符預期
 */
static int run_negative(const neg_case_t *c, size_t pkt_len, uint64_t now)
{
    const char *qr_in;
    const char *h_use = g_fields.h;
    const char *d_use = g_fields.d;
    size_t tlen = pkt_len;
    uint64_t verify_now = now;
    char note[128] = {0};
    int accepted, ok;

    out("\n[負向 %s] %s\n", c->id, c->desc);

    memcpy(g_tpkt, g_packet, pkt_len);
    switch (c->kind) {
        case N_H_FLIP: {
            uint8_t mac[64];
            size_t mlen;
            if (base64_decode(g_fields.h, mac, sizeof(mac), &mlen) != 0 || mlen == 0) {
                out("  [錯誤] 原始 h 無法解碼,略過\n");
                return 0;
            }
            snprintf(note, sizeof(note), "h byte#0:0x%02x → 0x%02x", mac[0], (uint8_t)(mac[0] ^ 0x01));
            mac[0] = (uint8_t)(mac[0] ^ 0x01);
            base64_encode(mac, mlen, g_th, sizeof(g_th));
            h_use = g_th;
            break;
        }
        case N_CT_FLIP:    flip_byte(g_tpkt, PKT_OFF_CT, note, sizeof(note)); break;
        case N_TAG_FLIP:   flip_byte(g_tpkt, pkt_len - 1, note, sizeof(note)); break;
        case N_NONCE_FLIP: flip_byte(g_tpkt, PKT_OFF_NONCE, note, sizeof(note)); break;
        /* 翻轉裸公鑰第 1 個 byte 的最低位元(最後 byte 的最高位元會被 X25519 忽略,不能用) */
        case N_PUB_FLIP:   flip_byte(g_tpkt, PKT_OFF_PUBRAW, note, sizeof(note)); break;
        case N_DER_FLIP:   flip_byte(g_tpkt, PKT_OFF_PUB, note, sizeof(note)); break;
        case N_LEN_FIELD:
            snprintf(note, sizeof(note), "封包 byte#3(長度欄位低位):0x%02x → 0x%02x",
                     g_tpkt[3], (uint8_t)(g_tpkt[3] + 1));
            g_tpkt[3] = (uint8_t)(g_tpkt[3] + 1);
            break;
        case N_TRUNCATE:
            tlen = 20;
            snprintf(note, sizeof(note), "封包長度:%lu → %lu 位元組",
                     (unsigned long)pkt_len, (unsigned long)tlen);
            break;
        case N_DROP_H:
            h_use = NULL;
            snprintf(note, sizeof(note), "移除 h 欄位");
            break;
        case N_TIME_SKEW:
            verify_now = now + TIME_SKEW_SEC;
            snprintf(note, sizeof(note), "驗證時間 now=%llu → %llu(+%d 秒)",
                     (unsigned long long)now, (unsigned long long)verify_now, TIME_SKEW_SEC);
            break;
        default:
            return 0;
    }

    /* 封包層的竄改:重新 Base64 編碼成新的 d */
    if (c->kind >= N_CT_FLIP && c->kind <= N_TRUNCATE) {
        if (base64_encode(g_tpkt, tlen, g_td, sizeof(g_td)) < 0) {
            out("  [錯誤] 竄改後封包編碼失敗,略過\n");
            return 0;
        }
        d_use = g_td;
    }

    if (build_qr(g_fields.t, d_use, h_use, g_fields.k, g_qr_out, sizeof(g_qr_out)) != 0) {
        out("  [錯誤] 竄改後 QR 過長,略過\n");
        return 0;
    }
    qr_in = g_qr_out;

    out("  變動      : %s\n", note);
    if (d_use != g_fields.d) { show_field_diff("d", g_fields.d, d_use); }
    if (h_use == g_th)       { show_field_diff("h", g_fields.h, h_use); }
    out("  竄改後 QR : %s\n", qr_in);

    if (inject_keys(1) != 0) {
        out("  [錯誤] 金鑰注入失敗\n");
        return 0;
    }
    g_now_epoch = verify_now;
    twdiw_platform_set_time_source(fixed_now);
    accepted = twdiw_verify(qr_in, twdiw_platform_get(), &g_res_n);
    twdiw_platform_clear_keys();

    if (c->expect1 == c->expect2) {
        out("  預期拒絕  : %s\n", reason_label(c->expect1));
    } else {
        out("  預期拒絕  : %s 或 %s\n", reason_label(c->expect1), reason_label(c->expect2));
    }

    if (accepted) {
        out("  實際結果  : 通過(ACCEPTED)  ✘ 不符預期:竄改資料竟被接受\n");
        return 0;
    }
    ok = (g_res_n.reason == c->expect1 || g_res_n.reason == c->expect2);
    out("  實際結果  : 拒絕 %s(%s)  %s\n", reason_label(g_res_n.reason),
        reason_desc(g_res_n.reason), ok ? "✔ 符合預期" : "✘ 不符預期:擋在錯誤的階段");
    return ok;
}

/* ---------- 單輪測試 ---------- */

/* @return 1 = 本輪全部符合預期 */
static int run_round(const char *qr, int round_no)
{
    uint64_t now = g_cfg_now_set ? g_cfg_now : (uint64_t)time(NULL);
    int ok_a, ok_b, same;
    size_t pkt_len;
    size_t i;
    int neg_ok = 0;
    int neg_total = (int)(sizeof(g_neg_cases) / sizeof(g_neg_cases[0]));

    out("\n================ 第 %d 輪 ================\n", round_no);
    out("輸入 QR    : %s\n", qr);
    out("now_epoch  : %llu %s\n\n", (unsigned long long)now, g_cfg_now_set ? "(設定檔指定)" : "(本機時間)");

    /* 正向:hex 與 Base64 兩種形式金鑰各跑一次 */
    ok_a = run_positive("A", 1, qr, now, &g_res_a);
    ok_b = run_positive("B", 0, qr, now, &g_res_b);

    if (!ok_a || !ok_b) {
        if (g_res_a.reason == TWDIW_VERIFY_ERR_TOTP || g_res_b.reason == TWDIW_VERIFY_ERR_TOTP) {
            diagnose_totp(qr, now);
        } else {
            out("  [說明] 請確認私鑰是否對應數位憑證皮夾QR Code反掃測試頁面登記的公鑰、QR 是否完整貼上、三把金鑰是否同一組。\n");
        }
        out("\n→ 正向未通過,略過負向測試(金鑰或時間不對時,負向結果沒有參考價值)。\n");
        out("本輪結果   : ✘ FAIL\n");
        return 0;
    }

    same = (g_res_a.plaintext_len == g_res_b.plaintext_len &&
            memcmp(g_res_a.plaintext, g_res_b.plaintext, g_res_a.plaintext_len) == 0);
    out("[正向比對] hex 與 Base64 兩種金鑰解出的明文 %s\n",
        same ? "逐位元組相同 ✔" : "不一致 ✘");
    if (!same) {
        out("本輪結果   : ✘ FAIL\n");
        return 0;
    }

    /* 負向:取出原始封包作為竄改基底 */
    if (qr_parse(qr, &g_fields) != QR_PARSE_OK ||
        base64_decode(g_fields.d, g_packet, sizeof(g_packet), &pkt_len) != 0 ||
        pkt_len < ECC_PACKET_HEADER_LEN + POLY1305_TAG_LEN + 1) {
        out("[錯誤] 無法解析原始封包,略過負向測試\n");
        out("本輪結果   : ✘ FAIL\n");
        return 0;
    }

    out("\n---------------- 負向測試(使用 hex 金鑰,共 %d 項) ----------------\n", neg_total);
    for (i = 0; i < sizeof(g_neg_cases) / sizeof(g_neg_cases[0]); i++) {
        neg_ok += run_negative(&g_neg_cases[i], pkt_len, now);
    }
    memset(g_packet, 0, sizeof(g_packet));
    memset(g_tpkt, 0, sizeof(g_tpkt));

    out("\n本輪結果   : 正向 2/2、負向 %d/%d 符合預期 → %s\n", neg_ok, neg_total,
        (neg_ok == neg_total) ? "★ PASS" : "✘ FAIL");
    return (neg_ok == neg_total) ? 1 : 0;
}

/* ---------- 輸入 ---------- */

/*
 * 讀一行輸入並取出 { ... } 區段。
 * @return 1 取得 QR;0 使用者要離開;-1 輸入過長或格式錯誤(可重試)
 */
static int read_qr(char **qr_out)
{
    char *s, *open_brace, *close_brace;
    size_t n;

    out_screen("\n請貼上數位憑證皮夾QR Code反掃測試頁面產生的 QR JSON(單行),直接 Enter 或輸入 q 離開 > ");
    fflush(stdout);
    if (fgets(g_line, sizeof(g_line), stdin) == NULL) { return 0; }

    n = strlen(g_line);
    if (n > 0 && g_line[n - 1] != '\n' && !feof(stdin)) {
        int ch;
        while ((ch = getchar()) != '\n' && ch != EOF) { }
        out_screen("[錯誤] 輸入超過 %d 字元\n", LINE_BUF_MAX - 1);
        return -1;
    }
    rstrip(g_line);
    s = lstrip(g_line);
    if (s[0] == '\0' || strcmp(s, "q") == 0 || strcmp(s, "Q") == 0) { return 0; }

    open_brace = strchr(s, '{');
    close_brace = strrchr(s, '}');
    if (open_brace == NULL || close_brace == NULL || close_brace < open_brace) {
        out_screen("[錯誤] 找不到 {...},請貼上完整的 QR JSON\n");
        return -1;
    }
    close_brace[1] = '\0';
    *qr_out = open_brace;
    return 1;
}

/* ---------- 主程式 ---------- */

int main(int argc, char *argv[])
{
    const char *cfg_path = "tester_config.txt";
    char report_name[64];
    time_t t_now;
    struct tm *lt;
    int i, rc;
    int rounds = 0;
    int all_ok = 1;

    int cfg_given = 0;

    for (i = 1; i < argc; i++) {
        if (strcmp(argv[i], "--report-full") == 0) { g_report_full = 1; }
        else { cfg_path = argv[i]; cfg_given = 1; }
    }

    /* 未指定設定檔且目前目錄沒有 tester_config.txt 時,改找 test/tester_config.txt */
    if (!cfg_given) {
        FILE *probe = fopen(cfg_path, "rb");
        if (probe != NULL) {
            fclose(probe);
        } else {
            cfg_path = "test/tester_config.txt";
        }
    }

    if (load_config(cfg_path) != 0) {
        return 2;
    }

    t_now = time(NULL);
    lt = localtime(&t_now);
    if (lt == NULL || strftime(report_name, sizeof(report_name), "report_%Y%m%d_%H%M%S.txt", lt) == 0) {
        snprintf(report_name, sizeof(report_name), "report.txt");
    }
    g_report = fopen(report_name, "w");

    out("==== TWDIW 離線 QR Code 解密測試工具 ====\n");
    out("設定檔     : %s\n", cfg_path);
    out("報告檔     : %s%s\n", g_report != NULL ? report_name : "(無法建立,只輸出畫面)",
        g_report_full ? "(含金鑰原文)" : "(金鑰以指紋遮蔽)");
    out("時間來源   : %s\n\n", g_cfg_now_set ? "設定檔 now=" : "本機時間(每輪貼上當下)");
    out_screen("!! 畫面會顯示金鑰原文,請僅使用測試金鑰,勿截圖外流 !!\n\n");

    show_keys();

    for (;;) {
        char *qr = NULL;
        rc = read_qr(&qr);
        if (rc == 0) { break; }
        if (rc < 0) { continue; }
        rounds++;
        if (!run_round(qr, rounds)) { all_ok = 0; }
        if (g_report != NULL) { fflush(g_report); }
    }

    out("\n==== 總計:%d 輪,%s ====\n", rounds,
        rounds == 0 ? "未執行任何測試" : (all_ok ? "全部符合預期 ★ PASS" : "有不符預期項目 ✘ FAIL"));

    /* 清除記憶體中的金鑰 */
    twdiw_platform_clear_keys();
    memset(&g_priv, 0, sizeof(g_priv));
    memset(&g_totp, 0, sizeof(g_totp));
    memset(&g_hmac, 0, sizeof(g_hmac));

    if (g_report != NULL) { fclose(g_report); }
    return (rounds > 0 && all_ok) ? 0 : 1;
}

/*
 * 建置(於 c99/test 目錄下,或直接執行 build.bat / build.sh):
 *   gcc -std=c99 -O2 \
 *     ../src/lib/sha256.c ../src/lib/hmac_sha256.c ../src/lib/base64.c \
 *     ../src/lib/chacha20_poly1305.c ../src/lib/x25519.c \
 *     ../src/lib/totp.c ../src/lib/der_x25519.c ../src/lib/ecc_service.c \
 *     ../src/lib/qr_parse.c ../src/twdiw_platform.c ../src/twdiw_verify.c \
 *     twdiw_tester.c -o twdiw_tester
 */
