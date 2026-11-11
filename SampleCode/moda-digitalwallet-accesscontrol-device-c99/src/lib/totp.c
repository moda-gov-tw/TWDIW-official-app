/*
 * totp.c — TOTP 服務
 *
 * 嚴格複製 Java TOTPService 的計算流程，確保產碼與 reference baseline 一致。
 */
#include "totp.h"
#include "hmac_sha256.h"
#include "base64.h"
#include <string.h>   /* memset、strcmp */
#include <stdio.h>    /* snprintf */
#include <time.h>     /* time */

/* Java 常數：時間偏移量 30 秒 */
#define TOTP_TIME_OFFSET 30

/* Java 常數：TOTP 週期 60 秒 */
#define TOTP_PERIOD 60

/* 產生 TOTP 碼 */
int totp_generate(const char *key_b64, uint64_t epoch_sec, int offset,
                  char out[TOTP_CODE_BUFLEN])
{
    uint8_t key_bytes[64];             /* 解碼後金鑰 */
    size_t key_len;                    /* 金鑰長度 */
    int64_t time_step;                 /* 時間區間（對應 Java long） */
    uint8_t msg[8];                    /* 8-byte big-endian 時間區間 */
    uint8_t hash[HMAC_SHA256_LEN];     /* HMAC 輸出 */
    int idx;                           /* 動態截斷起始索引 */
    uint32_t binary;                   /* 截斷後的 31 位元整數 */
    uint32_t otp;                      /* 取模後的 6 位數 */
    uint64_t step_bits;                /* 用於大端序輸出的位元樣式 */
    int i;                             /* 迴圈索引 */
    int rc;                            /* 回傳值 */

    /* 步驟一：解碼金鑰 */
    rc = base64_decode(key_b64, key_bytes, sizeof(key_bytes), &key_len);
    if (rc != 0) {
        return TOTP_ERR_KEY;
    }

    /* 步驟二：計算 timeStep = (epoch + 30) / 60 + offset（整數除法，與 Java 一致） */
    time_step = (int64_t)((epoch_sec + (uint64_t)TOTP_TIME_OFFSET) / (uint64_t)TOTP_PERIOD);
    time_step += offset;

    /* 步驟三：把 timeStep 以 8-byte big-endian 寫入（等同 Java ByteBuffer.putLong） */
    step_bits = (uint64_t)time_step;   /* 以二補數位元樣式輸出 */
    for (i = 0; i < 8; i++) {
        msg[i] = (uint8_t)((step_bits >> (56 - i * 8)) & 0xFF);
    }

    /* 步驟四：HMAC-SHA256(key, msg) */
    hmac_sha256(key_bytes, key_len, msg, sizeof(msg), hash);

    /* 步驟五：動態截斷，idx 取自最後一個位元組的低 4 位 */
    idx = hash[HMAC_SHA256_LEN - 1] & 0x0f;
    binary = (((uint32_t)hash[idx] & 0x7f) << 24)
           | (((uint32_t)hash[idx + 1] & 0xff) << 16)
           | (((uint32_t)hash[idx + 2] & 0xff) << 8)
           | ((uint32_t)hash[idx + 3] & 0xff);

    /* 步驟六：取模得 6 位數並格式化（不足補零） */
    otp = binary % 1000000u;
    if (TOTP_CODE_BUFLEN < 7) {
        memset(key_bytes, 0, sizeof(key_bytes));
        return TOTP_ERR_BUFFER;
    }
    snprintf(out, TOTP_CODE_BUFLEN, "%06u", otp);

    /* 步驟七：清零金鑰與中間結果 */
    memset(key_bytes, 0, sizeof(key_bytes));
    memset(hash, 0, sizeof(hash));
    return TOTP_OK;
}

/* 取得目前 Unix 秒數（正式執行時的時間來源） */
uint64_t totp_default_now(void)
{
    /* time(NULL) 回傳自 Epoch 起的秒數 */
    return (uint64_t)time(NULL);
}
