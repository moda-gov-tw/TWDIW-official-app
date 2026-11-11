/*
 * twdiw_verify.h — 可攜驗證核心(單一 orchestration,完全不相依硬體)
 *
 * 這是「單一驗證核心」:輸入一段反掃得到的 QR JSON(t/d/h/k),
 * 依序完成 解密 → 內層 JSON → TOTP 驗證 → HMAC 驗證,回傳「通過 / 拒絕(帶原因碼)」。
 *
 * 硬體相依全部收斂到傳入的 twdiw_platform_t 四個函式指標:
 *   - 重建共享密鑰走 platform->ecdh_x25519(裝置私鑰留在平台層 / Secure Element)
 *   - 內層 TOTP 以 platform->now_epoch_sec + platform->hmac_totp_key 重算比對
 *   - h 欄位以 platform->hmac_verify_key 對「解密後明文」重算,Base64 後與 h 比對
 * 核心本身永遠不接觸任何金鑰的裸位元組。
 *
 * 驗證順序與 Java 驗證端一致:先解密,再驗 TOTP,最後驗 HMAC。
 * 內層明文為 JSON,TOTP 碼固定放在 "totp" 欄位(其餘欄位為業務動態欄位)。
 *
 * 記憶體安全:固定緩衝、無動態配置、明文用畢清零。
 * 識別字採英文,所有註解採繁體中文。
 */
#ifndef C99_TWDIW_VERIFY_H
#define C99_TWDIW_VERIFY_H

#include <stddef.h>   /* size_t 型別 */
#include <stdint.h>   /* 固定寬度整數型別 */

#include "twdiw_platform.h"       /* twdiw_platform_t */
#include "lib/qr_parse.h"         /* QR_TYPE_MAX / QR_KID_MAX */
#include "lib/ecc_service.h"      /* ECC_MAX_PLAINTEXT */
#include "lib/totp.h"             /* TOTP_CODE_BUFLEN */

/* 明文緩衝上限,沿用 ECC 服務的固定上限 */
#define TWDIW_VERIFY_PLAINTEXT_MAX ECC_MAX_PLAINTEXT

/* 驗證原因碼(0 為通過;負值為各階段拒絕原因) */
#define TWDIW_VERIFY_OK           0    /* 全部通過 */
#define TWDIW_VERIFY_ERR_PARAM   -1    /* 參數錯誤(空指標等) */
#define TWDIW_VERIFY_ERR_QR_JSON -2    /* QR 頂層 JSON 解析失敗 / 缺 d 或 h */
#define TWDIW_VERIFY_ERR_PACKET  -3    /* d 封包 Base64 / 格式 / 邊界錯誤 */
#define TWDIW_VERIFY_ERR_ECDH    -4    /* 平台 ECDH 失敗(狀態未就緒 / 弱金鑰) */
#define TWDIW_VERIFY_ERR_DECRYPT -5    /* AEAD 標籤驗證失敗(資料遭竄改) */
#define TWDIW_VERIFY_ERR_INNER   -6    /* 內層 JSON 無 "totp" 欄位 */
#define TWDIW_VERIFY_ERR_TOTP    -7    /* TOTP 驗證不通過(過期 / 不符) */
#define TWDIW_VERIFY_ERR_HMAC    -8    /* HMAC(h)驗證不通過 */

/*
 * 驗證結果。無論通過或拒絕,已成功解出的欄位會盡量填入以利診斷。
 */
typedef struct {
    int reason;                                  /* TWDIW_VERIFY_* 原因碼 */
    char t[QR_TYPE_MAX + 1];                     /* QR 的 t 欄位(類型) */
    char k[QR_KID_MAX + 1];                      /* QR 的 k 欄位(金鑰識別碼) */
    char totp[TOTP_CODE_BUFLEN];                 /* 內層取出的 TOTP 碼(若已解出) */
    uint8_t plaintext[TWDIW_VERIFY_PLAINTEXT_MAX]; /* 解密後明文(若已解出) */
    size_t plaintext_len;                        /* 明文長度 */
} twdiw_verify_result_t;

/*
 * 執行端到端驗證。
 * @param qr_json QR 內容字串(頂層 JSON,含 t/d/h/k)
 * @param plat    平台介面(需已透過 twdiw_platform_set_keys 注入金鑰)
 * @param result  輸出結果(不可為 NULL);result->reason 記錄通過或拒絕原因
 * @return 1=通過(reason==TWDIW_VERIFY_OK);0=拒絕(reason 為對應負值)
 */
int twdiw_verify(const char *qr_json,
                 const twdiw_platform_t *plat,
                 twdiw_verify_result_t *result);

#endif /* C99_TWDIW_VERIFY_H */
