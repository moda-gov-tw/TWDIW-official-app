# TOTP 服務技術規格文件

## 1. 概述

TOTP（Time-based One-Time Password）服務提供了一個基於時間的一次性密碼驗證機制。本服務實作了 RFC 6238 標準，使用 HMAC-SHA256 演算法，結合時間戳記和金鑰來產生一次性密碼。

[程式碼請參考](../SampleCode/TOTPService.java)

### 1.1 主要功能
- 產生 TOTP 一次性密碼
- 驗證 TOTP 一次性密碼
- 支援自訂時間間隔
- 支援自訂密碼長度

### 1.2 技術特點
- 使用 HMAC-SHA256 演算法
- 支援 Base64 編碼的金鑰
- 提供 6 位數密碼輸出
- 預設 60 秒時間間隔，允許前後 30 秒的時間偏移

## 2. 技術規格

### 2.1 演算法說明
- 雜湊演算法：HMAC-SHA256
- 金鑰格式：Base64 編碼
- 密碼長度：6 位數
- 時間間隔：60 秒
- 時間偏移：允許前後 30 秒的時間偏移

### 2.2 TOTP 產生流程
1. 取得當前時間戳記（Unix 時間，秒）
2. 計算時間計數器（時間戳記 / 時間間隔）
3. 將時間計數器轉換為 8 位元組的位元組陣列
4. 使用 HMAC-SHA256 計算雜湊值
5. 使用動態截斷演算法取得 4 位元組的整數
6. 取模運算得到 6 位數密碼

### 2.3 資料格式

#### 2.3.1 金鑰格式
```
Base64 編碼的字串，例如：wiyvBquTOCAMX3Rk80zJ0q4OqU4X/8xTr8B0ESXsYIo=
```

#### 2.3.2 密碼格式
```
6 位數字的字串，例如：123456
```

## 3. API 規格

### 3.1 產生 TOTP

#### 3.1.1 方法簽章
```java
public String generateTOTP(String secretKey)
```

#### 3.1.2 參數說明
| 參數名稱 | 型別 | 必填 | 說明 |
|---------|------|------|------|
| secretKey | String | 是 | Base64 編碼的密鑰 |

#### 3.1.3 回傳值
- 型別：String
- 格式：6 位數字的字串

#### 3.1.4 範例
```java
String secretKey = "wiyvBquTOCAMX3Rk80zJ0q4OqU4X/8xTr8B0ESXsYIo=";
String totp = totpService.generateTOTP(secretKey);
```

### 3.2 驗證 TOTP

#### 3.2.1 方法簽章
```java
public boolean verifyTOTP(String secretKey, String totp)
```

#### 3.2.2 參數說明
| 參數名稱 | 型別 | 必填 | 說明 |
|---------|------|------|------|
| secretKey | String | 是 | Base64 編碼的密鑰 |
| totp | String | 是 | 要驗證的 TOTP 密碼 |

#### 3.2.3 回傳值
- 型別：boolean
- 說明：true 表示驗證成功，false 表示驗證失敗

## 4. 實作注意事項

### 4.1 安全性考量
1. 金鑰管理
   - 金鑰必須安全儲存
   - 建議使用加密儲存
   - 不同使用者使用不同的金鑰

2. 時間同步
   - 確保伺服器時間準確
   - 使用 NTP 服務同步時間
   - 考慮時間偏移的容錯範圍

### 4.2 效能考量
1. 金鑰處理
   - 使用 Base64 編碼/解碼
   - 避免重複的金鑰轉換
   - 考慮金鑰快取機制

2. 時間計算
   - 使用系統時間戳記
   - 避免頻繁的時間計算
   - 考慮時間同步的影響

### 4.3 錯誤處理
1. 參數驗證
   - 檢查金鑰格式
   - 驗證 TOTP 格式
   - 處理無效的輸入

2. 例外處理
   - 處理金鑰解碼錯誤
   - 處理時間計算錯誤
   - 處理驗證失敗情況

## 5. 測試建議

### 5.1 單元測試
1. 基本功能測試
   - 測試 TOTP 產生
   - 測試 TOTP 驗證
   - 測試時間間隔

2. 邊界條件測試
   - 測試無效的金鑰
   - 測試過期的 TOTP
   - 測試時間偏移

### 5.2 整合測試
1. 系統整合測試
   - 測試與其他服務的整合
   - 測試端對端流程
   - 測試效能和負載

## 6. 部署建議

### 6.1 配置建議
1. 時間配置
   - 設定 NTP 伺服器
   - 配置時間同步間隔
   - 設定時間偏移容錯範圍

2. 效能配置
   - 調整 JVM 參數
   - 配置適當的執行緒池
   - 設定適當的超時時間

## 7. 參考資源

### 7.1 技術文件
- [RFC 6238 - TOTP 規格](https://tools.ietf.org/html/rfc6238)
- [RFC 4226 - HOTP 規格](https://tools.ietf.org/html/rfc4226)
- [Base64 編碼說明](https://tools.ietf.org/html/rfc4648)

### 7.2 相關工具
- [TOTP 產生器](https://totp.danhersam.com/)
- [Base64 編碼/解碼工具](https://www.base64encode.org/)
- [時間戳記轉換工具](https://www.epochconverter.com/) 
