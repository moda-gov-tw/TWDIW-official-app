/*
 * qr_parse.c — QR 頂層 JSON 四欄結構化解析實作
 *
 * 以純字串掃描取出扁平物件中的字串欄位值,處理常見跳脫序列,並對每個欄位
 * 做長度上限檢核。刻意採用極簡解析,確保行為可預期。
 */
#include "qr_parse.h"
#include <string.h>   /* memset、strstr、strchr、strlen */

/*
 * 從物件文字取出指定鍵的字串值。
 * @param obj  物件文字
 * @param key  鍵名(不含引號)
 * @param out  輸出緩衝
 * @param cap  輸出緩衝容量(含結尾 '\0' 的空間)
 * @return 0 成功、-1 找不到鍵/格式錯誤、-2 值超過緩衝上限
 */
static int qr_get_string(const char *obj, const char *key, char *out, size_t cap)
{
    char needle[16];      /* 形如 "key" 的搜尋樣式(鍵名皆為單字元,足夠) */
    const char *p;        /* 掃描指標 */
    size_t o;             /* 輸出索引 */
    size_t klen;          /* 鍵名長度 */

    /* 組出帶引號的鍵名(t/d/h/k 皆單字元,needle 綽綽有餘) */
    klen = strlen(key);
    if (klen + 3 > sizeof(needle)) {
        return -1;
    }
    needle[0] = '"';
    memcpy(needle + 1, key, klen);
    needle[1 + klen] = '"';
    needle[2 + klen] = '\0';

    p = strstr(obj, needle);
    if (p == NULL) {
        return -1;
    }

    /* 跳過鍵名,找冒號 */
    p += strlen(needle);
    p = strchr(p, ':');
    if (p == NULL) {
        return -1;
    }
    p++;
    /* 跳過空白 */
    while (*p == ' ' || *p == '\t' || *p == '\r' || *p == '\n') {
        p++;
    }
    /* 必須是字串起始引號 */
    if (*p != '"') {
        return -1;
    }
    p++;

    /* 逐字元讀取直到未跳脫的引號 */
    o = 0;
    while (*p != '\0' && *p != '"') {
        char c = *p;
        if (c == '\\') {
            /* 處理常見跳脫序列 */
            p++;
            if (*p == '\0') {
                break;
            }
            switch (*p) {
                case 'n':  c = '\n'; break;
                case 't':  c = '\t'; break;
                case 'r':  c = '\r'; break;
                case '"':  c = '"';  break;
                case '\\': c = '\\'; break;
                case '/':  c = '/';  break;
                default:   c = *p;   break;
            }
        }
        /* 邊界檢核：預留結尾 '\0' */
        if (o + 1 >= cap) {
            return -2;
        }
        out[o++] = c;
        p++;
    }
    out[o] = '\0';
    return 0;
}

/* 解析 QR 頂層 JSON,取出 t/d/h/k 四欄 */
int qr_parse(const char *json, qr_fields_t *out)
{
    int rc;   /* 各欄位取值回傳 */

    /* 參數檢查 */
    if (json == NULL || out == NULL) {
        return QR_PARSE_ERR_JSON;
    }

    /* 先全清零,可選欄位缺省即為空字串 */
    memset(out, 0, sizeof(*out));

    /* d：必填 */
    rc = qr_get_string(json, "d", out->d, sizeof(out->d));
    if (rc == -2) {
        return QR_PARSE_ERR_TOOLONG;
    }
    if (rc != 0) {
        return QR_PARSE_ERR_MISSING;
    }

    /* h：必填 */
    rc = qr_get_string(json, "h", out->h, sizeof(out->h));
    if (rc == -2) {
        return QR_PARSE_ERR_TOOLONG;
    }
    if (rc != 0) {
        return QR_PARSE_ERR_MISSING;
    }

    /* t：可選,缺省維持空字串;僅在超長時報錯 */
    rc = qr_get_string(json, "t", out->t, sizeof(out->t));
    if (rc == -2) {
        return QR_PARSE_ERR_TOOLONG;
    }

    /* k：可選,缺省維持空字串;僅在超長時報錯 */
    rc = qr_get_string(json, "k", out->k, sizeof(out->k));
    if (rc == -2) {
        return QR_PARSE_ERR_TOOLONG;
    }

    return QR_PARSE_OK;
}
