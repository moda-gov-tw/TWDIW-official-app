/*
 * x25519.h — X25519 橢圓曲線 Diffie-Hellman（RFC 7748）
 *
 * 提供 Curve25519 上的純量乘法，供 ECC 服務產生金鑰對與共享密鑰。
 * 演算法本體採用公有領域 TweetNaCl 的 crypto_scalarmult 改寫，
 * 已由官方測試向量驗證其正確性。
 * 識別字採英文，所有註解採繁體中文。
 */
#ifndef C99_X25519_H
#define C99_X25519_H

#include <stdint.h>   /* 固定寬度整數型別 */

/* X25519 金鑰與座標長度（位元組） */
#define X25519_KEY_LEN 32

/*
 * X25519 純量乘法：out = scalar * point
 * @param out    32 位元組輸出（共享密鑰或公鑰 u 座標）
 * @param scalar 32 位元組純量（私鑰）
 * @param point  32 位元組 u 座標（對方公鑰或基點）
 */
void x25519_scalarmult(uint8_t out[X25519_KEY_LEN],
                       const uint8_t scalar[X25519_KEY_LEN],
                       const uint8_t point[X25519_KEY_LEN]);

/*
 * X25519 基點乘法：out = scalar * 9（產生公鑰）
 * @param out    32 位元組輸出公鑰
 * @param scalar 32 位元組私鑰
 */
void x25519_base(uint8_t out[X25519_KEY_LEN],
                 const uint8_t scalar[X25519_KEY_LEN]);

#endif /* C99_X25519_H */
