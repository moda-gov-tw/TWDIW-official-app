/*
 * der_x25519.c — X25519 金鑰 DER↔raw 轉換工具
 *
 * 以固定前綴 + 32 位元組 key 的方式組拆 X.509 / PKCS#8 DER，
 * 不引入完整 ASN.1 剖析器；固定緩衝、明確邊界檢查、無動態配置。
 */
#include "der_x25519.h"
#include <string.h>   /* memcmp、memcpy */

/*
 * X.509 SubjectPublicKeyInfo 前綴（12 位元組）
 *   30 2A          SEQUENCE，長度 42
 *   30 05          SEQUENCE（AlgorithmIdentifier），長度 5
 *   06 03 2B 65 6E OID 1.3.101.110（id-X25519）
 *   03 21 00       BIT STRING，長度 33，未使用位元 0
 */
static const uint8_t DER_X25519_PUB_PREFIX[DER_X25519_PUB_PREFIX_LEN] = {
    0x30, 0x2A, 0x30, 0x05, 0x06, 0x03, 0x2B, 0x65, 0x6E, 0x03, 0x21, 0x00
};

/*
 * PKCS#8 PrivateKeyInfo 前綴（16 位元組）
 *   30 2E          SEQUENCE，長度 46
 *   02 01 00       INTEGER version = 0
 *   30 05 06 03 2B 65 6E  AlgorithmIdentifier（OID id-X25519）
 *   04 22          OCTET STRING，長度 34（外層 privateKey 封裝）
 *   04 20          OCTET STRING，長度 32（內層 CurvePrivateKey）
 */
static const uint8_t DER_X25519_PRIV_PREFIX[DER_X25519_PRIV_PREFIX_LEN] = {
    0x30, 0x2E, 0x02, 0x01, 0x00, 0x30, 0x05, 0x06,
    0x03, 0x2B, 0x65, 0x6E, 0x04, 0x22, 0x04, 0x20
};

/* 裸公鑰 → X.509 DER */
int der_x25519_public_from_raw(const uint8_t raw[DER_X25519_RAW_LEN],
                               uint8_t der[DER_X25519_PUB_LEN])
{
    /* 先寫入固定前綴，再接上 32 位元組 key */
    memcpy(der, DER_X25519_PUB_PREFIX, DER_X25519_PUB_PREFIX_LEN);
    memcpy(der + DER_X25519_PUB_PREFIX_LEN, raw, DER_X25519_RAW_LEN);
    return DER_X25519_OK;
}

/* X.509 DER → 裸公鑰 */
int der_x25519_public_to_raw(const uint8_t *der, size_t der_len,
                             uint8_t raw[DER_X25519_RAW_LEN])
{
    /* 長度必須恰為 44 位元組 */
    if (der_len != DER_X25519_PUB_LEN) {
        return DER_X25519_ERR_LEN;
    }
    /* 前綴須與 X25519 X.509 規範一致 */
    if (memcmp(der, DER_X25519_PUB_PREFIX, DER_X25519_PUB_PREFIX_LEN) != 0) {
        return DER_X25519_ERR_PREFIX;
    }
    /* 取出前綴之後的 32 位元組 key */
    memcpy(raw, der + DER_X25519_PUB_PREFIX_LEN, DER_X25519_RAW_LEN);
    return DER_X25519_OK;
}

/* PKCS#8 DER → 裸私鑰 */
int der_x25519_private_to_raw(const uint8_t *der, size_t der_len,
                              uint8_t raw[DER_X25519_RAW_LEN])
{
    /* 長度必須恰為 48 位元組 */
    if (der_len != DER_X25519_PRIV_LEN) {
        return DER_X25519_ERR_LEN;
    }
    /* 前綴須與 X25519 PKCS#8 規範一致 */
    if (memcmp(der, DER_X25519_PRIV_PREFIX, DER_X25519_PRIV_PREFIX_LEN) != 0) {
        return DER_X25519_ERR_PREFIX;
    }
    /* 取出前綴之後的 32 位元組 key */
    memcpy(raw, der + DER_X25519_PRIV_PREFIX_LEN, DER_X25519_RAW_LEN);
    return DER_X25519_OK;
}
