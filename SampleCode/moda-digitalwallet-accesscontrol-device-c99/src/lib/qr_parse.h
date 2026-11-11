/*
 * qr_parse.h — QR 頂層 JSON 四欄結構化解析（t / d / h / k）
 *
 * 反掃端收到的 QR 內容是一個扁平 JSON 物件,對應 Java 產生端(t/d/h/k):
 *   t = 資料類型(例如 "SS")；    可選,缺省視為空字串
 *   d = ECC 加密封包的 Base64；  必填(驗證流程需解密)
 *   h = 對「解密後明文」算的 HMAC-SHA256 再 Base64；必填(完整性驗證)
 *   k = 金鑰識別碼(key id,例如 "default")；可選,缺省視為空字串
 *
 * 本模組只負責「把四欄安全地取出來 + 邊界檢核」,不做任何密碼學運算。
 * 純字串掃描、固定緩衝、無動態配置;取值時處理常見 JSON 跳脫序列。
 *
 * 識別字採英文,所有註解採繁體中文。
 */
#ifndef C99_QR_PARSE_H
#define C99_QR_PARSE_H

#include <stddef.h>   /* size_t 型別 */

/* 各欄位緩衝上限(不含結尾 '\0') */
#define QR_TYPE_MAX 32     /* t：類型字串上限 */
#define QR_D_MAX    2048   /* d：Base64 封包字串上限(足以涵蓋 QR 短訊息) */
#define QR_H_MAX    64     /* h：Base64 HMAC(32 位元組 → 44 字元含填充) */
#define QR_KID_MAX  64     /* k：金鑰識別碼上限 */

/* 解析回傳碼 */
#define QR_PARSE_OK          0    /* 成功 */
#define QR_PARSE_ERR_JSON   -1    /* 非合法物件 / 找不到冒號或引號 */
#define QR_PARSE_ERR_MISSING -2   /* 缺少必要欄位(d 或 h) */
#define QR_PARSE_ERR_TOOLONG -3   /* 某欄位超過對應緩衝上限 */

/*
 * 解析後的四欄。字串一律以 '\0' 結尾;
 * 可選欄位(t/k)缺省時為空字串,必填欄位(d/h)缺省則整體回報 MISSING。
 */
typedef struct {
    char t[QR_TYPE_MAX + 1];   /* 類型 */
    char d[QR_D_MAX + 1];      /* 加密封包 Base64 */
    char h[QR_H_MAX + 1];      /* HMAC Base64 */
    char k[QR_KID_MAX + 1];    /* 金鑰識別碼 */
} qr_fields_t;

/*
 * 解析 QR 頂層 JSON,取出 t/d/h/k 四欄。
 * @param json QR 內容字串(以 '\0' 結尾)
 * @param out  輸出四欄結構(不可為 NULL)
 * @return QR_PARSE_OK 或對應錯誤碼
 */
int qr_parse(const char *json, qr_fields_t *out);

#endif /* C99_QR_PARSE_H */
