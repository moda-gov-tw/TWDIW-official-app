/*
 * sha256.h — SHA-256 雜湊函式（公有領域參考實作）
 *
 * 依 FIPS 180-4 規範實作，供 HMAC-SHA256 與 ECC 的 KDF 使用。
 * 本檔案不依賴任何第三方函式庫，僅使用 C99 標準庫。
 * 識別字採英文，所有註解採繁體中文。
 */
#ifndef C99_SHA256_H
#define C99_SHA256_H

#include <stddef.h>   /* size_t 型別 */
#include <stdint.h>   /* 固定寬度整數型別 */

/* SHA-256 輸出摘要長度（位元組） */
#define SHA256_DIGEST_LEN 32

/* SHA-256 內部區塊長度（位元組） */
#define SHA256_BLOCK_LEN  64

/*
 * SHA-256 運算上下文（context）
 * 採固定長度緩衝區，全程無動態記憶體配置。
 */
typedef struct {
    uint32_t state[8];                 /* 八個雜湊狀態暫存器 H0~H7 */
    uint64_t bitlen;                   /* 已處理的總位元數 */
    uint8_t  buffer[SHA256_BLOCK_LEN]; /* 尚未湊滿一個區塊的暫存資料 */
    size_t   buffer_len;               /* 目前暫存資料的位元組數 */
} sha256_ctx;

/*
 * 初始化 SHA-256 上下文
 * @param ctx 欲初始化的上下文（不可為 NULL）
 */
void sha256_init(sha256_ctx *ctx);

/*
 * 餵入資料進行雜湊（可多次呼叫，支援串流）
 * @param ctx  已初始化的上下文
 * @param data 輸入資料指標
 * @param len  輸入資料長度（位元組）
 */
void sha256_update(sha256_ctx *ctx, const uint8_t *data, size_t len);

/*
 * 結束雜湊運算並輸出摘要
 * @param ctx    上下文（呼叫後即失效）
 * @param digest 輸出緩衝區，長度須為 SHA256_DIGEST_LEN
 */
void sha256_final(sha256_ctx *ctx, uint8_t digest[SHA256_DIGEST_LEN]);

/*
 * 一次性計算 SHA-256（便利函式）
 * @param data   輸入資料
 * @param len    輸入長度
 * @param digest 輸出摘要（32 位元組）
 */
void sha256(const uint8_t *data, size_t len, uint8_t digest[SHA256_DIGEST_LEN]);

#endif /* C99_SHA256_H */
