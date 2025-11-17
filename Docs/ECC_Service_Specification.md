# ECC 加密規格說明文件

## 1. 概述

本文件描述使用 Curve25519 橢圓曲線進行非對稱加密的實作規格。此加密方案結合了 ECDH（橢圓曲線迪菲-赫爾曼金鑰交換）和 ChaCha20-Poly1305 加密，提供安全的資料傳輸機制。

[程式碼請參考](../SampleCode/ECCService.java)

## 2. 技術規格

### 2.1 使用的演算法
- 橢圓曲線：Curve25519（256 位元）
- 金鑰交換：ECDH
- 對稱加密：ChaCha20-Poly1305
- 金鑰衍生：SHA-256
- 編碼方式：Base64

### 2.2 金鑰格式

#### 2.2.1 公鑰格式
- 使用 X25519 格式
- 編碼：Base64

#### 2.2.2 私鑰格式
- 使用 PKCS#8 格式
- 編碼：Base64

## 3. 加密流程

### 3.1 金鑰對產生
1. 使用 X25519 產生金鑰對
2. 將公鑰和私鑰分別進行 Base64 編碼
3. 回傳格式：`[Base64(公鑰), Base64(私鑰)]`

### 3.2 加密過程
1. 產生臨時金鑰對（使用 X25519）
2. 使用 ECDH 與接收者公鑰產生共享金鑰
3. 使用 SHA-256 作為 KDF 衍生對稱金鑰
4. 產生 12 bytes 隨機 Nonce
5. 使用 ChaCha20-Poly1305 加密原始資料
6. 組合資料：`[ephemeral 公鑰長度(4 bytes) + ephemeral 公鑰 + nonce(12 bytes) + 加密資料]`
7. 將組合後的資料進行 Base64 編碼

### 3.3 解密過程
1. Base64 解碼加密資料
2. 分離資料：
   - 前 4 bytes：ephemeral 公鑰長度
   - 接下來 N bytes：ephemeral 公鑰（N 為前 4 bytes 指定的長度）
   - 接下來 12 bytes：Nonce
   - 剩餘部分：加密資料
3. 使用 ECDH 與私鑰重建共享金鑰
4. 使用 SHA-256 作為 KDF 衍生對稱金鑰
5. 使用 ChaCha20-Poly1305 解密資料

## 4. 資料格式

### 4.1 金鑰對格式
```json
{
    "publicKey": "Base64編碼的公鑰字串",
    "privateKey": "Base64編碼的私鑰字串"
}
```

### 4.2 加密資料格式
```
Base64(ephemeral 公鑰長度(4 bytes) + ephemeral 公鑰 + nonce(12 bytes) + ChaCha20-Poly1305加密資料)
```

## 5. 實作注意事項

### 5.1 安全性考量
- 每次加密都使用新的臨時金鑰對
- 使用隨機 Nonce 確保相同明文產生不同密文
- 使用 ChaCha20-Poly1305 提供認證加密
- 使用 Curve25519 提供 128 位元的安全性
- 使用 SHA-256 作為 KDF 提供 256 位元的安全性

### 5.2 效能考量
- 使用 X25519 提供高效的橢圓曲線運算
- 使用 Base64 編碼確保資料可以安全傳輸
- 使用 ChaCha20-Poly1305 提供高效的加密效能

### 5.3 錯誤處理
- 金鑰格式錯誤時應拋出 InvalidKeySpecException
- 加密/解密失敗時應拋出相應的加密異常
- MAC 驗證失敗時應拋出 SecurityException

## 6. 範例

### 6.1 金鑰對產生
```java
String[] keyPair = generateKeyPair();
String publicKey = keyPair[0];  // Base64 編碼的公鑰
String privateKey = keyPair[1]; // Base64 編碼的私鑰
```

### 6.2 加密
```java
String plaintext = "要加密的文字";
String encrypted = encrypt(plaintext, publicKey);
```

### 6.3 解密
```java
String decrypted = decrypt(encrypted, privateKey);
```

## 7. 相容性說明

### 7.1 支援的程式語言
- Java 11 或更高版本
- Python
- Node.js
- Go
- C#

### 7.2 必要的函式庫
- Java 標準加密函式庫
- Base64 編碼/解碼功能
- ChaCha20-Poly1305 加密支援

## 8. 測試建議

建議實作時進行以下測試：
1. 金鑰對產生測試
2. 基本加密/解密測試
3. 特殊字元測試
4. 長文字測試
5. 錯誤處理測試
6. MAC 驗證失敗測試

## 9. 參考資源

- [Curve25519 規格](https://cr.yp.to/ecdh/curve25519-20060209.pdf)
- [ECDH 標準](https://tools.ietf.org/html/rfc7748)
- [ChaCha20-Poly1305 標準](https://tools.ietf.org/html/rfc8439) 
