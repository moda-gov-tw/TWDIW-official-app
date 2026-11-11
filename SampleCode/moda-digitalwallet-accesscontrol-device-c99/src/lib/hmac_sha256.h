/*
 * hmac_sha256.h — HMAC-SHA256 訊息驗證碼（RFC 2104）
 *
 * 以本專案的 sha256 模組為基礎實作，供 HMAC 服務與 TOTP 共用。
 * 識別字採英文，所有註解採繁體中文。
 */
#ifndef C99_HMAC_SHA256_H
#define C99_HMAC_SHA256_H

#include <stddef.h>   /* size_t 型別 */
#include <stdint.h>   /* 固定寬度整數型別 */
#include "sha256.h"   /* 沿用 SHA256_DIGEST_LEN 定義 */

/* HMAC-SHA256 輸出長度（位元組），等同底層雜湊摘要長度 */
#define HMAC_SHA256_LEN SHA256_DIGEST_LEN

/*
 * 計算 HMAC-SHA256
 * @param key     金鑰位元組
 * @param key_len 金鑰長度
 * @param data    訊息位元組
 * @param data_len 訊息長度
 * @param out     輸出緩衝區，長度須為 HMAC_SHA256_LEN
 */
void hmac_sha256(const uint8_t *key, size_t key_len,
                 const uint8_t *data, size_t data_len,
                 uint8_t out[HMAC_SHA256_LEN]);

#endif /* C99_HMAC_SHA256_H */
