/*
 * hmac_sha256.c — HMAC-SHA256 訊息驗證碼（RFC 2104）
 *
 * HMAC(K, m) = H( (K' ^ opad) || H( (K' ^ ipad) || m ) )
 * 其中 K' 為調整到區塊長度後的金鑰，區塊長度為 64 位元組。
 */
#include "hmac_sha256.h"
#include <string.h>   /* memcpy、memset */

/* 計算 HMAC-SHA256，全程使用固定長度緩衝區、無動態配置 */
void hmac_sha256(const uint8_t *key, size_t key_len,
                 const uint8_t *data, size_t data_len,
                 uint8_t out[HMAC_SHA256_LEN])
{
    uint8_t k_prime[SHA256_BLOCK_LEN];     /* 調整後的金鑰 K'（64 位元組） */
    uint8_t ipad[SHA256_BLOCK_LEN];        /* 內層填充區塊 */
    uint8_t opad[SHA256_BLOCK_LEN];        /* 外層填充區塊 */
    uint8_t inner_hash[HMAC_SHA256_LEN];   /* 內層雜湊結果 */
    sha256_ctx ctx;                        /* SHA-256 運算上下文 */
    size_t i;                              /* 迴圈索引 */

    /* 步驟一：把金鑰正規化為 64 位元組的 K' */
    memset(k_prime, 0, sizeof(k_prime));
    if (key_len > SHA256_BLOCK_LEN) {
        /* 金鑰過長時，先以 SHA-256 壓成 32 位元組再置於前段 */
        sha256(key, key_len, k_prime);
    } else {
        /* 金鑰不長於區塊長度時直接複製，其餘位元組維持補零 */
        memcpy(k_prime, key, key_len);
    }

    /* 步驟二：建立 ipad 與 opad 填充區塊 */
    for (i = 0; i < SHA256_BLOCK_LEN; i++) {
        ipad[i] = (uint8_t)(k_prime[i] ^ 0x36);  /* 內層填充常數 0x36 */
        opad[i] = (uint8_t)(k_prime[i] ^ 0x5c);  /* 外層填充常數 0x5c */
    }

    /* 步驟三：計算內層雜湊 H( (K' ^ ipad) || m ) */
    sha256_init(&ctx);
    sha256_update(&ctx, ipad, SHA256_BLOCK_LEN);
    sha256_update(&ctx, data, data_len);
    sha256_final(&ctx, inner_hash);

    /* 步驟四：計算外層雜湊 H( (K' ^ opad) || inner_hash ) */
    sha256_init(&ctx);
    sha256_update(&ctx, opad, SHA256_BLOCK_LEN);
    sha256_update(&ctx, inner_hash, HMAC_SHA256_LEN);
    sha256_final(&ctx, out);

    /* 步驟五：清零所有含金鑰衍生資料的緩衝區，避免記憶體殘留 */
    memset(k_prime, 0, sizeof(k_prime));
    memset(ipad, 0, sizeof(ipad));
    memset(opad, 0, sizeof(opad));
    memset(inner_hash, 0, sizeof(inner_hash));
}
