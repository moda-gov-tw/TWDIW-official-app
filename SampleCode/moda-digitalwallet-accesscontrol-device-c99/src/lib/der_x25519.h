/*
 * der_x25519.h — X25519 金鑰 DER↔raw 轉換工具
 *
 * 本專案 C99 內部一律使用「裸 32 位元組」金鑰；但 Java 服務的
 * 金鑰交換與封包中，公鑰採 X.509 SubjectPublicKeyInfo（44 位元組）、
 * 私鑰採 PKCS#8 PrivateKeyInfo（48 位元組）的 DER 編碼。
 *
 * X25519 的 DER 編碼具有固定前綴（演算法 OID 1.3.101.110 = 2B 65 6E），
 * 金鑰內容為固定 32 位元組，因此可用「固定前綴 + 32 位元組 key」的方式
 * 直接組拆，無需完整 ASN.1 剖析器。前綴位元組已與 JDK 實際輸出比對驗證，
 * 並於參考向量中逐位元對答案。
 *
 *   公鑰 X.509 (44) = 12 位元組固定前綴 + 32 位元組 u 座標（小端）
 *     30 2A 30 05 06 03 2B 65 6E 03 21 00 || key[32]
 *   私鑰 PKCS#8 (48) = 16 位元組固定前綴 + 32 位元組純量
 *     30 2E 02 01 00 30 05 06 03 2B 65 6E 04 22 04 20 || key[32]
 *
 * 識別字採英文，所有註解採繁體中文。
 */
#ifndef C99_DER_X25519_H
#define C99_DER_X25519_H

#include <stddef.h>   /* size_t 型別 */
#include <stdint.h>   /* 固定寬度整數型別 */

/* 裸金鑰長度（位元組） */
#define DER_X25519_RAW_LEN         32

/* 公鑰 X.509 DER 全長與固定前綴長度 */
#define DER_X25519_PUB_LEN         44
#define DER_X25519_PUB_PREFIX_LEN  12

/* 私鑰 PKCS#8 DER 全長與固定前綴長度 */
#define DER_X25519_PRIV_LEN        48
#define DER_X25519_PRIV_PREFIX_LEN 16

/* 轉換回傳碼 */
#define DER_X25519_OK          0   /* 成功 */
#define DER_X25519_ERR_LEN    -1   /* 輸入長度不符 */
#define DER_X25519_ERR_PREFIX -2   /* DER 前綴與 X25519 規範不符 */

/*
 * 裸公鑰 → X.509 DER
 * @param raw 32 位元組裸公鑰（u 座標，小端）
 * @param der 輸出 44 位元組 X.509 DER 緩衝區
 * @return DER_X25519_OK
 */
int der_x25519_public_from_raw(const uint8_t raw[DER_X25519_RAW_LEN],
                               uint8_t der[DER_X25519_PUB_LEN]);

/*
 * X.509 DER → 裸公鑰（驗證前綴）
 * @param der     X.509 DER 位元組
 * @param der_len DER 長度（須為 44）
 * @param raw     輸出 32 位元組裸公鑰
 * @return DER_X25519_OK 或錯誤碼
 */
int der_x25519_public_to_raw(const uint8_t *der, size_t der_len,
                             uint8_t raw[DER_X25519_RAW_LEN]);

/*
 * PKCS#8 DER → 裸私鑰（驗證前綴）
 * @param der     PKCS#8 DER 位元組
 * @param der_len DER 長度（須為 48）
 * @param raw     輸出 32 位元組裸私鑰
 * @return DER_X25519_OK 或錯誤碼
 */
int der_x25519_private_to_raw(const uint8_t *der, size_t der_len,
                              uint8_t raw[DER_X25519_RAW_LEN]);

#endif /* C99_DER_X25519_H */
