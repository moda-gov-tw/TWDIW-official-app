/*
 * chacha20_poly1305.h — ChaCha20-Poly1305 AEAD（RFC 8439）
 *
 * 提供符合 RFC 8439 的認證加密與解密，供 ECC 服務的封包封裝使用。
 * 本專案 KDF 直接以 SHA-256(sharedSecret) 全 32 位元組作為金鑰，無 HKDF。
 * 識別字採英文，所有註解採繁體中文。
 */
#ifndef C99_CHACHA20_POLY1305_H
#define C99_CHACHA20_POLY1305_H

#include <stddef.h>   /* size_t 型別 */
#include <stdint.h>   /* 固定寬度整數型別 */

/* ChaCha20 金鑰長度（位元組） */
#define CHACHA20_KEY_LEN   32

/* AEAD 使用的 nonce 長度（位元組） */
#define CHACHA20_NONCE_LEN 12

/* Poly1305 認證標籤長度（位元組） */
#define POLY1305_TAG_LEN   16

/*
 * ChaCha20-Poly1305 認證解密（open）
 * @param key        32 位元組金鑰
 * @param nonce      12 位元組 nonce
 * @param aad        額外驗證資料（可為 NULL）
 * @param aad_len    額外驗證資料長度
 * @param ciphertext 密文
 * @param ct_len     密文長度
 * @param tag        16 位元組認證標籤
 * @param plaintext  輸出明文緩衝區（長度須 >= ct_len）
 * @return 驗證成功且解密完成回傳 0；標籤驗證失敗回傳 -1
 */
int chacha20_poly1305_decrypt(const uint8_t key[CHACHA20_KEY_LEN],
                              const uint8_t nonce[CHACHA20_NONCE_LEN],
                              const uint8_t *aad, size_t aad_len,
                              const uint8_t *ciphertext, size_t ct_len,
                              const uint8_t tag[POLY1305_TAG_LEN],
                              uint8_t *plaintext);

/*
 * ChaCha20 串流加密（RFC 8439 §2.4），供固定答案比對測試與內部使用
 * @param key      32 位元組金鑰
 * @param counter  起始區塊計數器
 * @param nonce    12 位元組 nonce
 * @param in       輸入資料
 * @param len      資料長度
 * @param out      輸出緩衝區（長度須 >= len，可與 in 相同）
 */
void chacha20_xor(const uint8_t key[CHACHA20_KEY_LEN], uint32_t counter,
                  const uint8_t nonce[CHACHA20_NONCE_LEN],
                  const uint8_t *in, size_t len, uint8_t *out);

#endif /* C99_CHACHA20_POLY1305_H */
