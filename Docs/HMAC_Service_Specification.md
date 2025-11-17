# HMAC 服務技術規格文件

## 1. 概述

HMAC（Hash-based Message Authentication Code）服務提供了一個安全的方式來驗證訊息的真實性和完整性。本服務使用 HMAC-SHA256 演算法，結合了加密雜湊函數（SHA-256）和金鑰，以產生訊息驗證碼。

[程式碼請參考](../SampleCode/HMACService.java)

### 1.1 主要功能
- 產生 HMAC 金鑰
- 計算 HMAC 值
- 支援 UTF-8 編碼的資料

### 1.2 技術特點
- 使用 HMAC-SHA256 演算法
- 支援 Base64 編碼的金鑰
- 提供 Base64 編碼的輸出
- 使用 256 位元（32 位元組）的金鑰長度

## 2. 技術規格

### 2.1 演算法說明
- 雜湊演算法：HMAC-SHA256
- 金鑰長度：256 位元（32 位元組）
- 輸出格式：Base64 編碼
- 字元編碼：UTF-8

### 2.2 HMAC 計算流程
1. 金鑰處理：使用 Base64 解碼金鑰
2. 資料處理：將輸入資料轉換為 UTF-8 編碼的位元組陣列
3. HMAC 計算：使用 HMAC-SHA256 演算法計算雜湊值
4. 編碼轉換：將結果轉換為 Base64 格式

## 3. 規格說明

### 3.1 產生 HMAC 金鑰

#### 3.1.1 方法簽章
```java
public Map<String, String> generateHMACKey()
```

#### 3.1.2 回傳值
- 型別：Map<String, String>
- 格式：包含 "hmacKey" 鍵值對的 Map，值為 Base64 編碼的金鑰

#### 3.1.3 範例
```java
Map<String, String> result = hmacService.generateHMACKey();
String hmacKey = result.get("hmacKey");
```

### 3.2 計算 HMAC 值

#### 3.2.1 方法簽章
```java
public String calculateHMAC(String data, String key)
```

#### 3.2.2 參數說明
| 參數名稱 | 型別 | 必填 | 說明 |
|---------|------|------|------|
| data | String | 是 | 要計算 HMAC 的原始資料 |
| key | String | 是 | Base64 編碼的 HMAC 金鑰 |

#### 3.2.3 回傳值
- 型別：String
- 格式：Base64 編碼的 HMAC 值

#### 3.2.4 範例
```java
String data = "testData";
String key = "wiyvBquTOCAMX3Rk80zJ0q4OqU4X/8xTr8B0ESXsYIo=";
String hmac = hmacService.calculateHMAC(data, key);
```

## 4. 實作注意事項

### 4.1 安全性考量
1. 金鑰管理
   - 金鑰必須安全儲存
   - 建議定期更換金鑰
   - 不同環境使用不同的金鑰

2. 資料處理
   - 確保資料使用 UTF-8 編碼
   - 避免資料被截斷或修改
   - 注意資料長度限制

### 4.2 效能考量
1. 金鑰處理
   - 使用 Base64 編碼/解碼
   - 避免重複的金鑰轉換
   - 考慮金鑰快取機制

2. 資料處理
   - 使用 UTF-8 編碼
   - 避免過大的資料量
   - 考慮資料分塊處理

### 4.3 錯誤處理
1. 參數驗證
   - 檢查金鑰格式
   - 驗證資料格式
   - 處理 null 值情況

2. 例外處理
   - 處理編碼相關例外
   - 處理金鑰格式錯誤
   - 處理計算過程錯誤

## 5. 測試建議

### 5.1 單元測試
1. 基本功能測試
   - 測試金鑰產生
   - 測試 HMAC 計算
   - 測試編碼轉換

2. 邊界條件測試
   - 測試空資料
   - 測試特殊字元
   - 測試長字串

### 5.2 整合測試
1. API 整合測試
   - 測試與其他服務的整合
   - 測試端對端流程
   - 測試效能和負載

## 6. 部署建議

### 6.1 配置建議
1. 金鑰配置
   - 使用環境變數或配置檔案
   - 不同環境使用不同金鑰
   - 定期更換金鑰

## 7. 參考資源

### 7.1 技術文件
- [HMAC 演算法說明](https://en.wikipedia.org/wiki/HMAC)
- [SHA-256 規格](https://nvlpubs.nist.gov/nistpubs/FIPS/NIST.FIPS.180-4.pdf)
- [Base64 編碼說明](https://tools.ietf.org/html/rfc4648)

### 7.2 相關工具
- [HMAC 計算器](https://www.freeformatter.com/hmac-generator.html)
- [Base64 編碼/解碼工具](https://www.base64encode.org/)
- [SHA-256 雜湊計算器](https://emn178.github.io/online-tools/sha256.html) 
