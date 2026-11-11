/*
 * base64.c — Base64 編解碼（RFC 4648 標準字母表，含 '=' 填充）
 */
#include "base64.h"

/* 標準 Base64 編碼字母表（含結尾 '\0'，僅使用索引 0~63） */
static const char BASE64_ALPHABET[] =
    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

/*
 * 將單一 Base64 字元轉回 0~63 數值
 * @param c 待查字元
 * @return 對應數值；非法字元回傳 -1
 */
static int base64_char_value(char c)
{
    /* 大寫字母 A~Z 對應 0~25 */
    if (c >= 'A' && c <= 'Z') {
        return c - 'A';
    }
    /* 小寫字母 a~z 對應 26~51 */
    if (c >= 'a' && c <= 'z') {
        return c - 'a' + 26;
    }
    /* 數字 0~9 對應 52~61 */
    if (c >= '0' && c <= '9') {
        return c - '0' + 52;
    }
    /* 符號 '+' 對應 62 */
    if (c == '+') {
        return 62;
    }
    /* 符號 '/' 對應 63 */
    if (c == '/') {
        return 63;
    }
    /* 其餘皆為非法字元 */
    return -1;
}

/* Base64 編碼 */
int base64_encode(const uint8_t *data, size_t len, char *out, size_t out_cap)
{
    size_t i;         /* 讀取原始資料的索引 */
    size_t o;         /* 寫入輸出字串的索引 */
    uint32_t triple;  /* 暫存 3 位元組（24 位元）的組合 */
    size_t remain;    /* 目前這一組尚有幾個位元組 */

    o = 0;
    /* 每次處理 3 位元組 */
    for (i = 0; i < len; i += 3) {
        /* 判斷本組實際可用位元組數（1~3） */
        remain = len - i;
        if (remain > 3) {
            remain = 3;
        }

        /* 將最多 3 位元組堆入 24 位元暫存，不足處補 0 */
        triple = (uint32_t)data[i] << 16;
        if (remain > 1) {
            triple |= (uint32_t)data[i + 1] << 8;
        }
        if (remain > 2) {
            triple |= (uint32_t)data[i + 2];
        }

        /* 容量檢查：本組固定輸出 4 字元 */
        if (o + 4 > out_cap) {
            return -1;
        }

        /* 依 6 位元一組輸出 4 個字元 */
        out[o++] = BASE64_ALPHABET[(triple >> 18) & 0x3F];
        out[o++] = BASE64_ALPHABET[(triple >> 12) & 0x3F];
        /* 第三個字元：資料不足 2 位元組時以 '=' 填充 */
        out[o++] = (remain > 1) ? BASE64_ALPHABET[(triple >> 6) & 0x3F] : '=';
        /* 第四個字元：資料不足 3 位元組時以 '=' 填充 */
        out[o++] = (remain > 2) ? BASE64_ALPHABET[triple & 0x3F] : '=';
    }

    /* 寫入結尾 '\0' */
    if (o + 1 > out_cap) {
        return -1;
    }
    out[o] = '\0';
    return (int)o;
}

/* Base64 解碼 */
int base64_decode(const char *b64, uint8_t *out, size_t out_cap, size_t *out_len)
{
    size_t o;         /* 寫入輸出的位元組索引 */
    uint32_t buffer;  /* 位元累積暫存 */
    int bits;         /* 目前累積的有效位元數 */
    int val;          /* 單一字元的數值 */
    const char *p;    /* 掃描輸入字串的指標 */

    o = 0;
    buffer = 0;
    bits = 0;

    /* 逐字元掃描直到字串結尾 */
    for (p = b64; *p != '\0'; p++) {
        /* 遇到填充字元 '=' 即視為資料結束 */
        if (*p == '=') {
            break;
        }
        /* 略過換行等空白字元，增加相容性 */
        if (*p == '\r' || *p == '\n' || *p == ' ' || *p == '\t') {
            continue;
        }

        /* 查表取得數值，非法字元即失敗 */
        val = base64_char_value(*p);
        if (val < 0) {
            return -1;
        }

        /* 把 6 位元併入暫存 */
        buffer = (buffer << 6) | (uint32_t)val;
        bits += 6;

        /* 每湊滿 8 位元即輸出一個位元組 */
        if (bits >= 8) {
            bits -= 8;
            if (o + 1 > out_cap) {
                return -1;   /* 輸出容量不足 */
            }
            out[o++] = (uint8_t)((buffer >> bits) & 0xFF);
        }
    }

    /* 回報實際解出的位元組數 */
    if (out_len != NULL) {
        *out_len = o;
    }
    return 0;
}
