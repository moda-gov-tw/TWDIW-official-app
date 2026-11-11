/*
 * totp.h — TOTP 服務
 *
 * 對應 Java 端 TOTPService，重點互通細節：
 *   timeStep = (epochSec + 30) / 60 + offset        （整數除法，偏移量 TIME_OFFSET=30、週期 TOTP_PERIOD=60）
 *   msg      = 8-byte big-endian(timeStep)
 *   hash     = HmacSHA256(Base64.decode(key), msg)
 *   idx      = hash[31] & 0x0f                        （動態截斷）
 *   binary   = ((hash[idx] & 0x7f)<<24)|((hash[idx+1]&0xff)<<16)|((hash[idx+2]&0xff)<<8)|(hash[idx+3]&0xff)
 *   code     = binary % 1000000，格式化為 6 位數（%06d）
 *   驗證掃描 offset ∈ {-1, 0, +1}
 *
 * 「取得目前時間」抽成可替換函式，測試以固定 epoch 注入。
 * 識別字採英文，所有註解採繁體中文。
 */
#ifndef C99_TOTP_H
#define C99_TOTP_H

#include <stddef.h>   /* size_t 型別 */
#include <stdint.h>   /* 固定寬度整數型別 */

/* TOTP 服務錯誤碼 */
#define TOTP_OK          0   /* 成功 */
#define TOTP_ERR_KEY    -1   /* 金鑰 Base64 解碼失敗 */
#define TOTP_ERR_BUFFER -2   /* 輸出緩衝區容量不足 */

/* TOTP 碼字串長度（6 位數字 + 結尾 '\0'） */
#define TOTP_CODE_BUFLEN 7

/*
 * 產生 TOTP 碼（時間由參數注入，對應 Java generateTOTP(key, offset)）
 * @param key_b64   Base64 格式金鑰
 * @param epoch_sec 注入的 Unix 秒數（等同 Java Instant.now().getEpochSecond()）
 * @param offset    時間窗口偏移（-1 / 0 / +1）
 * @param out       輸出緩衝區，長度須為 TOTP_CODE_BUFLEN
 * @return TOTP_OK 或對應錯誤碼
 */
int totp_generate(const char *key_b64, uint64_t epoch_sec, int offset,
                  char out[TOTP_CODE_BUFLEN]);

/*
 * 取得目前 Unix 秒數（可替換的時間來源，正式執行時使用）
 * 測試不呼叫此函式，改以固定 epoch 注入 totp_generate。
 * @return 目前 Unix 秒數
 */
uint64_t totp_default_now(void);

#endif /* C99_TOTP_H */
