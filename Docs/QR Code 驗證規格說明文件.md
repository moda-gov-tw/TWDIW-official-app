# QR Code 驗證規格說明文件

## 1. 概述

本文件描述在取得 QR Code 的資料後，要如何進行驗證。

[測試網站](https://demo.wallet.gov.tw/reverseqrcode/)

[程式範例請參考](../SampleCode/VerifyQRCodeController.java#L61)

## 2. 執行此解密作業，所需要的資料

### 2.1 QRCode掃描後，取得的資料

```json
{
  "t": "超商取貨",
  "d": "AAAALDAqMAUGAytlbgMhAGQmJAWzbjfvm2PV7TOhC4KEcNyfAwwaRpNozmn5qzUcKLqk0Nn9M/BZDLpfv2YuKLHlk2mxkQBeZf2mwKVe0B2V38BUsrraKfGYYx3rCBnJpKWoixSngGdCLM41CkrrT8dXavzkcaDTvkpAJ5tl",
  "h": "tdguyeOlJnjcsQedgoeP1kFZzKvuBl4oTwCJAlIx7js=",
  "k": "default"
}
```

| 參數名稱 | 必填 | 說明 | 格式範例 |
|---------|------|------|----------|
| t | 是 | 交易類型，可於驗證端後台自行設定，如"超商取貨" | `"超商取貨"` |
| d | 是 | 加密後的密文(encryptedData) | `"AAAALDAqMAUGAytlbgMhAGQmJAWzbjfvm2PV7TOhC4KEcNyfAwwaRpNozmn5qzUcKLqk0Nn9M/BZDLpfv2YuKLHlk2mxkQBeZf2mwKVe0B2V38BUsrraKfGYYx3rCBnJpKWoixSngGdCLM41CkrrT8dXavzkcaDTvkpAJ5tl"` |
| h | 是 | d 欄位解密後內容的 HMAC 訊息驗證碼 | `"tdguyeOlJnjcsQedgoeP1kFZzKvuBl4oTwCJAlIx7js="` |
| k | 是 | 金鑰代碼 | `"default"` |

### 2.2 執行此解密作業，所需要的欄位

> ⚠️ **重要訊息：** privateKey、totpKey與hmacKey的值，因本專案是範例參考，實際情況請自行保存在POS機或安全的位置。

| 參數名稱 | 必填 | 說明 | 格式範例 |
|---------|------|------|----------|
| encryptedData | 是 | Base64 編碼的加密資料(來自QR Code的d欄位) | `"AAAALDAqMAUGAytlbgMhAGQmJAWzbjfvm2PV7TOhC4KEcNyfAwwaRpNozmn5qzUcKLqk0Nn9M/BZDLpfv2YuKLHlk2mxkQBeZf2mwKVe0B2V38BUsrraKfGYYx3rCBnJpKWoixSngGdCLM41CkrrT8dXavzkcaDTvkpAJ5tl"` |
| hmacValue | 是 | d 欄位解密後內容的 HMAC 訊息驗證碼(來自QR Code的h欄位) | `"tdguyeOlJnjcsQedgoeP1kFZzKvuBl4oTwCJAlIx7js="` |
| keyIdentifier | 是 | 金鑰代碼(來自QR Code的k欄位) | `"default"` |
| privateKey | 是 | Base64 編碼的私鑰(這個值請自行保存) | `"MIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0w..."` |
| totpKey | 是 | TOTP 金鑰，用於時間同步驗證(這個值請自行保存) | `"JBSWY3DPEHPK3PXP"` |
| hmacKey | 是 | HMAC 金鑰，用於訊息驗證碼驗證(這個值請自行保存) | `"your-hmac-secret-key"` |


## 3. 驗證流程

### 3.1 前置處理

1. 驗證請求參數
   - 檢查 encryptedData 是否為空值
   - 檢查 hmacValue 是否為空值
   - 檢查 keyIdentifier 是否為空值
   - 檢查 privateKey 是否為空值
   - 檢查 totpKey 是否為空值
   - 檢查 hmacKey 是否為空值

### 3.2 解密流程
1. 使用 ECC 解密
   - 使用提供的私鑰解密 encryptedData
   - 解密失敗時回傳適當的錯誤訊息
   - 請參考 [ECC_Service_Specification.md 規格文件](../ECC_Service_Specification.md)

2. 解析解密後的內容
   - 解析 JSON 格式的解密內容
   - 驗證必要欄位是否存在
   - 檢查資料格式是否正確

### 3.3 驗證檢查
1. 時間戳記驗證
   - 檢查解密內容中的時間戳記
   - 驗證時間戳記是否在有效期限內(QRCode有效60秒、時間偏移量30秒)
   - 過期的時間戳記視為無效
   - 請參考 [TOTP_Service_Specification.md 規格文件](../TOTP_Service_Specification.md)
  

2. 內容完整性驗證
   - 檢查解密內容的完整性
   - 驗證所有必要欄位
   - 檢查資料格式是否符合預期
   - 驗證 HMAC 訊息驗證碼是否正確
   - 請參考 [HMAC_Service_Specification.md 規格文件](../HMAC_Service_Specification.md)


## 4. 安全性考量

### 4.1 加密要求
- 使用 Curve25519 橢圓曲線
- 使用 ECDH 金鑰交換
- 使用 ChaCha20-Poly1305 認證加密（對應 SampleCode/ECCService.java 與 ECC 規格）
- 使用 12 bytes 隨機 Nonce（ChaCha20-Poly1305 要求）

### 4.2 金鑰管理
- 金鑰對必須安全產生
- 私鑰必須安全保管（建議儲存在 HSM、POS 端安全模組或等同安全等級的機制中）
- 定期更換金鑰對（需由實作方依作業流程訂定週期）
- 使用安全的金鑰傳輸機制（請依實際環境評估，例如 TLS、離線載具等）
