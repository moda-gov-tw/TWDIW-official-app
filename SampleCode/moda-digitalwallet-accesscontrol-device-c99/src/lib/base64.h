/*
 * base64.h — Base64 編解碼（RFC 4648 標準字母表，含 '=' 填充）
 *
 * 對應 Java java.util.Base64 的 getEncoder()/getDecoder() 行為，
 * 以確保金鑰與 HMAC 字串與 reference baseline 逐位元一致。
 * 識別字採英文，所有註解採繁體中文。
 */
#ifndef C99_BASE64_H
#define C99_BASE64_H

#include <stddef.h>   /* size_t 型別 */
#include <stdint.h>   /* 固定寬度整數型別 */

/*
 * Base64 編碼
 * @param data    原始位元組
 * @param len     原始長度
 * @param out     輸出字串緩衝區（會寫入結尾 '\0'）
 * @param out_cap 輸出緩衝區容量
 * @return 成功回傳寫入的字元數（不含 '\0'），容量不足回傳 -1
 */
int base64_encode(const uint8_t *data, size_t len, char *out, size_t out_cap);

/*
 * Base64 解碼
 * @param b64     Base64 字串（以 '\0' 結尾）
 * @param out     輸出位元組緩衝區
 * @param out_cap 輸出緩衝區容量
 * @param out_len 實際解出的位元組數（輸出參數）
 * @return 成功回傳 0；格式錯誤或容量不足回傳 -1
 */
int base64_decode(const char *b64, uint8_t *out, size_t out_cap, size_t *out_len);

#endif /* C99_BASE64_H */
