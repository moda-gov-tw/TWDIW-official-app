/*
 * twdiw_platform.h — TWDIW 平台抽象層（裝置端 adapter）
 *
 * 將韌體「裝置專屬祕密（X25519 私鑰、hmacKey、totpKey）」與「RTC 時間來源」
 * 綁定成一組函式指標，供上層協定以統一的 twdiw_platform_t 介面呼叫，而不必
 * 直接接觸金鑰。內部以本專案既有原語實作：
 *   - now_epoch_sec  → 可替換時間來源（預設 totp_default_now，即 time(NULL)）
 *   - ecdh_x25519    → x25519_scalarmult(裝置私鑰, peer 公鑰)
 *   - hmac_verify_key→ hmac_sha256(hmacKey, msg)
 *   - hmac_totp_key  → hmac_sha256(totpKey, msg)
 *
 * 設計說明：
 *   1. C 沒有閉包，函式指標無法夾帶祕密，故裝置祕密以「檔案範圍靜態儲存」
 *      保存（單一裝置身分）。請以安全儲存 / 保護記憶體承載，勿明文常駐。
 *   2. ecdh_x25519 會檢查共享密鑰是否為全零（small-subgroup 攻擊），
 *      全零回傳 TWDIW_ERR_WEAK_KEY。
 *   3. 用畢請呼叫 twdiw_platform_clear_keys() 將祕密清零。
 *
 * 識別字採英文，所有註解採繁體中文。
 */
#ifndef C99_TWDIW_PLATFORM_H
#define C99_TWDIW_PLATFORM_H

#include <stddef.h>   /* size_t 型別 */
#include <stdint.h>   /* 固定寬度整數型別 */

/* 各長度常數（位元組） */
#define TWDIW_X25519_KEY_LEN 32   /* X25519 裸私鑰 / 公鑰 / 共享密鑰長度 */
#define TWDIW_SHARED_LEN     32   /* ECDH 共享密鑰輸出長度 */
#define TWDIW_HMAC_OUT_LEN   32   /* HMAC-SHA256 原始輸出長度 */
#define TWDIW_HMAC_KEY_MAX   64   /* hmacKey / totpKey 可承載的最大長度 */

/* 回傳碼（皆為非正值；成功為 0） */
#define TWDIW_OK             0    /* 成功 */
#define TWDIW_ERR_PARAM     -1    /* 參數錯誤（空指標、長度超限等） */
#define TWDIW_ERR_STATE     -2    /* 尚未注入金鑰即呼叫（狀態錯誤） */
#define TWDIW_ERR_WEAK_KEY  -3    /* ECDH 共享密鑰為全零（弱金鑰 / 小子群） */

/*
 * 平台介面：由裝置韌體提供的四個能力。
 * 上層協定僅透過本結構呼叫，不直接接觸裝置祕密。
 */
typedef struct {
    /* 取得目前 Unix 時間（秒），由平台的 RTC 提供 */
    uint64_t (*now_epoch_sec)(void);
    /* 以裝置私鑰對 peer 公鑰執行 ECDH */
    int (*ecdh_x25519)(const uint8_t peer_pub[32], uint8_t out_shared[32]);
    /* 以 hmacKey 計算 HMAC-SHA256 */
    int (*hmac_verify_key)(const uint8_t *msg, size_t len, uint8_t out[32]);
    /* 以 totpKey 計算 HMAC-SHA256 */
    int (*hmac_totp_key)(const uint8_t *msg, size_t len, uint8_t out[32]);
} twdiw_platform_t;

/*
 * 注入裝置專屬祕密（金鑰）。
 * 呼叫成功後，透過 twdiw_platform_get() 取得的介面即可運作。
 *
 * @param device_priv  32 位元組 X25519 裸私鑰（不可為 NULL）
 * @param hmac_key     hmacKey 位元組（長度為 0 時可為 NULL）
 * @param hmac_key_len hmacKey 長度（0..TWDIW_HMAC_KEY_MAX）
 * @param totp_key     totpKey 位元組（長度為 0 時可為 NULL）
 * @param totp_key_len totpKey 長度（0..TWDIW_HMAC_KEY_MAX）
 * @return TWDIW_OK 或 TWDIW_ERR_PARAM
 */
int twdiw_platform_set_keys(const uint8_t device_priv[TWDIW_X25519_KEY_LEN],
                            const uint8_t *hmac_key, size_t hmac_key_len,
                            const uint8_t *totp_key, size_t totp_key_len);

/*
 * 設定時間來源（可選）。
 * 傳入 NULL 表示還原為預設來源（totp_default_now，即 time(NULL)）。
 * 裸機 / 無標準 time() 的環境，請在此掛入自家 RTC 讀取函式。
 *
 * @param now_fn 回傳目前 Unix 秒數的函式指標，或 NULL 還原預設
 */
void twdiw_platform_set_time_source(uint64_t (*now_fn)(void));

/*
 * 取得已綁定裝置祕密的平台介面（指向靜態常數，勿釋放）。
 * 即使尚未注入金鑰亦回傳有效指標；但在 set_keys 之前呼叫 ecdh / hmac
 * 介面會得到 TWDIW_ERR_STATE。
 *
 * @return 平台介面指標（永不為 NULL）
 */
const twdiw_platform_t *twdiw_platform_get(void);

/*
 * 將所有裝置祕密清零並標記為未就緒。
 * 建議在關機、登出或金鑰輪替前呼叫。
 */
void twdiw_platform_clear_keys(void);

#endif /* C99_TWDIW_PLATFORM_H */
