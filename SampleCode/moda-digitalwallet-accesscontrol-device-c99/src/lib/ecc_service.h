/*
 * ecc_service.h — ECC 解密服務
 *
 * 對應 Java 端 ECCService 的封包格式，解密流程：
 *   1. 封包 = 4 位元組大端長度 + ephemeral 公鑰(X.509 DER, 44)
 *              + 12 位元組 nonce + (密文 || tag)，整體以 Base64 傳遞
 *   2. 以封包中的 ephemeral 公鑰與裝置私鑰做 X25519 ECDH → sharedSecret
 *   3. KDF：key = SHA-256(sharedSecret) 全 32 位元組（無 HKDF、無 salt）
 *   4. ChaCha20-Poly1305(key, nonce) 驗證標籤並解出明文
 *
 * 記憶體安全：固定緩衝、明確邊界檢查、無動態配置，金鑰與共享密鑰用後清零。
 * 識別字採英文，所有註解採繁體中文。
 */
#ifndef C99_ECC_SERVICE_H
#define C99_ECC_SERVICE_H

#include <stddef.h>   /* size_t 型別 */
#include <stdint.h>   /* 固定寬度整數型別 */

#include "der_x25519.h"          /* DER 長度常數 */
#include "chacha20_poly1305.h"   /* nonce / tag 長度常數 */
#include "../twdiw_platform.h"   /* twdiw_platform_t：ECDH 走平台函式指標 */

/* ECC 服務錯誤碼（皆為負值；解密成功回傳非負的明文長度） */
#define ECC_OK             0    /* 一般成功 */
#define ECC_ERR_BUFFER    -1    /* 輸出緩衝區容量不足 */
#define ECC_ERR_FORMAT    -2    /* 封包格式錯誤（長度欄位、DER 前綴等） */
#define ECC_ERR_BASE64    -3    /* 輸入 Base64 解碼失敗 */
#define ECC_ERR_AUTH      -4    /* Poly1305 標籤驗證失敗（資料遭竄改或金鑰錯） */
#define ECC_ERR_TOOLARGE  -5    /* 明文或封包超過本實作固定緩衝上限 */
#define ECC_ERR_ECDH      -6    /* 平台 ECDH 失敗(狀態未就緒 / 弱金鑰等) */

/* 本參考實作的固定緩衝上限（足以涵蓋 QR 驗證用途的短訊息） */
#define ECC_MAX_PLAINTEXT  1024
/* 封包標頭固定長度：4(長度) + 44(X.509 公鑰) + 12(nonce) = 60 */
#define ECC_PACKET_HEADER_LEN (4 + DER_X25519_PUB_LEN + CHACHA20_NONCE_LEN)
/* 封包最大長度：標頭 + 最大明文 + 16 位元組 tag */
#define ECC_MAX_PACKET (ECC_PACKET_HEADER_LEN + ECC_MAX_PLAINTEXT + POLY1305_TAG_LEN)

/*
 * ECC 解密（裝置端符合性版：ECDH 改走平台函式指標）
 *
 * 「重建共享密鑰」這一步不吃裸私鑰,而是呼叫 platform->ecdh_x25519(ephemeral_pub, shared)。
 * 裝置私鑰因此可永久留在平台層 / Secure Element,核心永不接觸金鑰裸位元組。
 * 裝置端驗證流程(twdiw_verify)一律走本函式。
 *
 * @param in_b64          Base64 封包字串
 * @param plat            平台介面(需已注入金鑰,否則 ECDH 回 TWDIW_ERR_STATE)
 * @param out             輸出明文緩衝區
 * @param out_cap         輸出緩衝區容量
 * @param out_len_actual  實際解出的明文長度(可為 NULL)
 * @return >=0 為明文位元組數;<0 為錯誤碼(ECC_ERR_AUTH 表示標籤驗證失敗、
 *         ECC_ERR_ECDH 表示平台 ECDH 失敗)
 */
int ecc_decrypt_via_platform(const char *in_b64,
                             const twdiw_platform_t *plat,
                             uint8_t *out, size_t out_cap,
                             size_t *out_len_actual);

#endif /* C99_ECC_SERVICE_H */
