/*
 * twdiw_platform.c — TWDIW 平台抽象層實作
 *
 * 以本專案既有原語（x25519、hmac_sha256、totp）包裝出 twdiw_platform_t
 * 的四個函式指標，並將裝置祕密以檔案範圍靜態儲存承載。
 * 記憶體安全：固定緩衝、無動態配置、金鑰用畢清零。
 */
#include "twdiw_platform.h"
#include "lib/x25519.h"        /* x25519_scalarmult / X25519_KEY_LEN */
#include "lib/hmac_sha256.h"   /* hmac_sha256 / HMAC_SHA256_LEN */
#include "lib/totp.h"          /* totp_default_now（預設時間來源） */
#include <string.h>        /* memcpy、memset */

/* ---------- 裝置祕密的靜態儲存（單一裝置身分） ---------- */

/* X25519 裝置裸私鑰 */
static uint8_t g_device_priv[TWDIW_X25519_KEY_LEN];
/* hmacKey 與其長度 */
static uint8_t g_hmac_key[TWDIW_HMAC_KEY_MAX];
static size_t  g_hmac_key_len;
/* totpKey 與其長度 */
static uint8_t g_totp_key[TWDIW_HMAC_KEY_MAX];
static size_t  g_totp_key_len;
/* 金鑰是否已就緒（0=未注入，1=已注入） */
static int g_keys_ready = 0;

/* 可替換的時間來源（NULL 表示採用預設 totp_default_now） */
static uint64_t (*g_now_fn)(void) = NULL;

/* ---------- adapter：對應四個函式指標 ---------- */

/* 取得目前 Unix 秒數：優先用注入的時間來源，否則用預設 */
static uint64_t adapter_now(void)
{
    /* 有掛入自家 RTC 來源就用它 */
    if (g_now_fn != NULL) {
        return g_now_fn();
    }
    /* 否則採預設（time(NULL)） */
    return totp_default_now();
}

/* 判斷 32 位元組緩衝是否全為零（避免 small-subgroup 弱共享密鑰） */
static int is_all_zero_32(const uint8_t buf[32])
{
    uint8_t acc = 0;   /* 逐位元組累積 OR，避免提早返回造成的時間側通道 */
    size_t i;          /* 掃描索引 */

    for (i = 0; i < 32; i++) {
        acc = (uint8_t)(acc | buf[i]);
    }
    /* acc 為 0 代表全零 */
    return (acc == 0) ? 1 : 0;
}

/* 以裝置私鑰對 peer 公鑰執行 ECDH */
static int adapter_ecdh(const uint8_t peer_pub[32], uint8_t out_shared[32])
{
    /* 參數檢查 */
    if (peer_pub == NULL || out_shared == NULL) {
        return TWDIW_ERR_PARAM;
    }
    /* 尚未注入金鑰不得使用 */
    if (g_keys_ready == 0) {
        return TWDIW_ERR_STATE;
    }

    /* 純量乘法：out_shared = clamp(裝置私鑰) * peer_pub */
    x25519_scalarmult(out_shared, g_device_priv, peer_pub);

    /* 全零共享密鑰視為弱金鑰，清零輸出後回報錯誤 */
    if (is_all_zero_32(out_shared) != 0) {
        memset(out_shared, 0, TWDIW_SHARED_LEN);
        return TWDIW_ERR_WEAK_KEY;
    }
    return TWDIW_OK;
}

/* 以 hmacKey 計算 HMAC-SHA256（原始 32 位元組輸出） */
static int adapter_hmac_verify(const uint8_t *msg, size_t len, uint8_t out[32])
{
    /* 參數檢查：長度為 0 時允許 msg 為 NULL */
    if (out == NULL || (msg == NULL && len != 0)) {
        return TWDIW_ERR_PARAM;
    }
    /* 尚未注入金鑰不得使用 */
    if (g_keys_ready == 0) {
        return TWDIW_ERR_STATE;
    }

    /* 以 hmacKey 對訊息計算 HMAC-SHA256 */
    hmac_sha256(g_hmac_key, g_hmac_key_len, msg, len, out);
    return TWDIW_OK;
}

/* 以 totpKey 計算 HMAC-SHA256（原始 32 位元組輸出） */
static int adapter_hmac_totp(const uint8_t *msg, size_t len, uint8_t out[32])
{
    /* 參數檢查：長度為 0 時允許 msg 為 NULL */
    if (out == NULL || (msg == NULL && len != 0)) {
        return TWDIW_ERR_PARAM;
    }
    /* 尚未注入金鑰不得使用 */
    if (g_keys_ready == 0) {
        return TWDIW_ERR_STATE;
    }

    /* 以 totpKey 對訊息計算 HMAC-SHA256 */
    hmac_sha256(g_totp_key, g_totp_key_len, msg, len, out);
    return TWDIW_OK;
}

/* ---------- 對外靜態介面（函式指標指向上方 adapter） ---------- */

/* 平台介面常數：四個能力一次綁妥 */
static const twdiw_platform_t g_platform = {
    adapter_now,          /* now_epoch_sec */
    adapter_ecdh,         /* ecdh_x25519 */
    adapter_hmac_verify,  /* hmac_verify_key */
    adapter_hmac_totp     /* hmac_totp_key */
};

/* ---------- 生命週期管理 API ---------- */

/* 注入裝置專屬祕密 */
int twdiw_platform_set_keys(const uint8_t device_priv[TWDIW_X25519_KEY_LEN],
                            const uint8_t *hmac_key, size_t hmac_key_len,
                            const uint8_t *totp_key, size_t totp_key_len)
{
    /* 私鑰必填 */
    if (device_priv == NULL) {
        return TWDIW_ERR_PARAM;
    }
    /* HMAC 金鑰長度上限檢查 */
    if (hmac_key_len > TWDIW_HMAC_KEY_MAX || totp_key_len > TWDIW_HMAC_KEY_MAX) {
        return TWDIW_ERR_PARAM;
    }
    /* 長度非 0 時指標不得為空 */
    if ((hmac_key == NULL && hmac_key_len != 0) ||
        (totp_key == NULL && totp_key_len != 0)) {
        return TWDIW_ERR_PARAM;
    }

    /* 複製裝置私鑰 */
    memcpy(g_device_priv, device_priv, TWDIW_X25519_KEY_LEN);

    /* 複製 hmacKey（先清零整段緩衝再寫入實際長度） */
    memset(g_hmac_key, 0, sizeof(g_hmac_key));
    if (hmac_key_len != 0) {
        memcpy(g_hmac_key, hmac_key, hmac_key_len);
    }
    g_hmac_key_len = hmac_key_len;

    /* 複製 totpKey */
    memset(g_totp_key, 0, sizeof(g_totp_key));
    if (totp_key_len != 0) {
        memcpy(g_totp_key, totp_key, totp_key_len);
    }
    g_totp_key_len = totp_key_len;

    /* 標記就緒 */
    g_keys_ready = 1;
    return TWDIW_OK;
}

/* 設定 / 還原時間來源 */
void twdiw_platform_set_time_source(uint64_t (*now_fn)(void))
{
    /* NULL 代表還原為預設來源 */
    g_now_fn = now_fn;
}

/* 取得平台介面（永不為 NULL） */
const twdiw_platform_t *twdiw_platform_get(void)
{
    return &g_platform;
}

/* 清零所有裝置祕密並標記未就緒 */
void twdiw_platform_clear_keys(void)
{
    /* 逐段清零，避免記憶體殘留 */
    memset(g_device_priv, 0, sizeof(g_device_priv));
    memset(g_hmac_key, 0, sizeof(g_hmac_key));
    memset(g_totp_key, 0, sizeof(g_totp_key));
    g_hmac_key_len = 0;
    g_totp_key_len = 0;
    g_keys_ready = 0;
}
