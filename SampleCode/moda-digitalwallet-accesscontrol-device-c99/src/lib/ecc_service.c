/*
 * ecc_service.c — ECC 解密服務
 *
 * 以本專案 x25519、sha256、chacha20_poly1305、base64、der_x25519 原語
 * 組合出與 Java ECCService 一致的封包格式與解密行為。
 */
#include "ecc_service.h"
#include "x25519.h"
#include "sha256.h"
#include "base64.h"
#include <string.h>   /* memcpy、memset */

/*
 * 讀取 4 位元組大端長度（對應 Java bytesToInt）
 */
static uint32_t get_u32_be(const uint8_t *src)
{
    return ((uint32_t)src[0] << 24) |
           ((uint32_t)src[1] << 16) |
           ((uint32_t)src[2] << 8) |
           ((uint32_t)src[3]);
}

/* ECC 解密（裝置端符合性版：ECDH 改走平台函式指標） */
int ecc_decrypt_via_platform(const char *in_b64,
                             const twdiw_platform_t *plat,
                             uint8_t *out, size_t out_cap,
                             size_t *out_len_actual)
{
    uint8_t packet[ECC_MAX_PACKET];              /* 解碼後封包位元組 */
    uint8_t ephemeral_pub[DER_X25519_RAW_LEN];   /* 從封包取回的臨時公鑰（裸） */
    uint8_t shared[X25519_KEY_LEN];              /* ECDH 共享密鑰 */
    uint8_t key[SHA256_DIGEST_LEN];              /* KDF 衍生對稱金鑰 */
    size_t packet_len;                           /* 封包長度 */
    uint32_t pub_len;                            /* 封包宣告的公鑰長度 */
    size_t offset;                               /* 讀取位移 */
    size_t ct_len;                               /* 密文（不含 tag）長度 */
    const uint8_t *nonce;                        /* 指向封包中的 nonce */
    const uint8_t *ciphertext;                   /* 指向封包中的密文 */
    const uint8_t *tag;                          /* 指向封包中的 tag */
    int rc;                                       /* 回傳值 */

    /* 參數檢查：平台介面必填 */
    if (plat == NULL || plat->ecdh_x25519 == NULL) {
        return ECC_ERR_ECDH;
    }

    /* 步驟一：Base64 解碼取得原始封包位元組 */
    rc = base64_decode(in_b64, packet, sizeof(packet), &packet_len);
    if (rc != 0) {
        return ECC_ERR_BASE64;
    }

    /* 步驟二：長度合理性檢查（至少須容納標頭與 16 位元組 tag） */
    if (packet_len < ECC_PACKET_HEADER_LEN + POLY1305_TAG_LEN) {
        return ECC_ERR_FORMAT;
    }

    /* 步驟三：讀取 4 位元組大端公鑰長度，本實作僅支援固定 44 位元組 X.509 DER */
    pub_len = get_u32_be(packet);
    if (pub_len != (uint32_t)DER_X25519_PUB_LEN) {
        return ECC_ERR_FORMAT;
    }

    /* 步驟四：從封包取出 ephemeral 公鑰（X.509 DER → 裸 32 位元組） */
    offset = 4;
    rc = der_x25519_public_to_raw(packet + offset, DER_X25519_PUB_LEN, ephemeral_pub);
    if (rc != DER_X25519_OK) {
        return ECC_ERR_FORMAT;
    }
    offset += DER_X25519_PUB_LEN;

    /* 步驟五：定位 nonce、密文、tag */
    nonce = packet + offset;
    offset += CHACHA20_NONCE_LEN;
    ct_len = packet_len - offset - POLY1305_TAG_LEN;
    ciphertext = packet + offset;
    tag = packet + offset + ct_len;

    /* 邊界檢查：明文緩衝與固定上限 */
    if (ct_len > out_cap || ct_len > ECC_MAX_PLAINTEXT) {
        return ECC_ERR_BUFFER;
    }

    /*
     * 步驟六：重建共享密鑰 —— 關鍵差異：不吃裸私鑰,改呼叫平台 ECDH。
     * 裝置私鑰留在平台層 / Secure Element,核心不接觸金鑰。
     */
    rc = plat->ecdh_x25519(ephemeral_pub, shared);
    if (rc != TWDIW_OK) {
        memset(shared, 0, sizeof(shared));
        return ECC_ERR_ECDH;
    }

    /* 步驟七：KDF，key = SHA-256(sharedSecret) */
    sha256(shared, X25519_KEY_LEN, key);

    /* 步驟八：ChaCha20-Poly1305 解密並驗證標籤 */
    rc = chacha20_poly1305_decrypt(key, nonce, NULL, 0,
                                   ciphertext, ct_len, tag, out);

    /* 清零敏感中間值 */
    memset(shared, 0, sizeof(shared));
    memset(key, 0, sizeof(key));
    memset(packet, 0, sizeof(packet));

    if (rc != 0) {
        return ECC_ERR_AUTH;   /* 標籤驗證失敗 */
    }

    if (out_len_actual != NULL) {
        *out_len_actual = ct_len;
    }
    return (int)ct_len;   /* 明文位元組數 */
}
