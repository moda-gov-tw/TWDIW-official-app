# 數位錢包持有者 iOS SDK

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![iOS](https://img.shields.io/badge/iOS-15.0%2B-blue.svg)](https://www.apple.com/ios/)
[![Xcode](https://img.shields.io/badge/Xcode-15.0%2B-blue.svg)](https://developer.apple.com/xcode/)
[![Swift](https://img.shields.io/badge/Swift-5.0-orange.svg)](https://swift.org/)
[![Flutter](https://img.shields.io/badge/Flutter-3.16.5-02569B.svg)](https://flutter.dev/)

---

### 目錄

- [數位錢包持有者 iOS SDK](#數位錢包持有者-ios-sdk)
    - [目錄](#目錄)
  - [專案簡介](#專案簡介)
  - [主要功能](#主要功能)
  - [技術架構](#技術架構)
    - [核心技術](#核心技術)
    - [主要相依套件](#主要相依套件)
  - [系統需求](#系統需求)
    - [開發環境需求](#開發環境需求)
    - [執行環境需求](#執行環境需求)
  - [快速開始](#快速開始)
    - [1. 取得原始碼](#1-取得原始碼)
    - [2. 準備 Flutter Framework](#2-準備-flutter-framework)
    - [3. 開啟專案](#3-開啟專案)
    - [4. 編譯專案](#4-編譯專案)
      - [使用 xcodebuild 編譯](#使用-xcodebuild-編譯)
    - [5. 驗證建置結果](#5-驗證建置結果)
  - [整合指南](#整合指南)
    - [加入 Framework 到專案](#加入-framework-到專案)
    - [使用範例](#使用範例)
      - [初始化 DID 金鑰物件](#初始化-did-金鑰物件)
      - [取得公鑰（JWK 格式）](#取得公鑰jwk-格式)
      - [簽署資料（產生 JWS）](#簽署資料產生-jws)
      - [驗證使用者（公開金鑰）](#驗證使用者公開金鑰)
      - [刪除金鑰](#刪除金鑰)
  - [API 文件](#api-文件)
    - [主要方法](#主要方法)
    - [錯誤處理](#錯誤處理)
      - [一般錯誤](#一般錯誤)
      - [常見錯誤](#常見錯誤)
  - [安全性](#安全性)
    - [金鑰管理](#金鑰管理)
    - [加密演算法](#加密演算法)
  - [開發指南](#開發指南)
    - [專案結構](#專案結構)
    - [程式碼風格規範](#程式碼風格規範)
      - [命名規範](#命名規範)
      - [錯誤處理](#錯誤處理)
  - [疑難排解](#疑難排解)
    - [常見問題](#常見問題)
      - [1. 編譯錯誤：找不到 Flutter.framework](#1-編譯錯誤找不到-flutterframework)
      - [2. 執行時錯誤：Secure Enclave 不可用](#2-執行時錯誤secure-enclave-不可用)
      - [3. Mac Catalyst 編譯錯誤](#3-mac-catalyst-編譯錯誤)
  - [支援與聯絡](#支援與聯絡)
  - [變更日誌](#變更日誌)
  - [致謝](#致謝)
  - [授權條款](#授權條款)

---

## 專案簡介

本專案為 **數位錢包持有者 iOS SDK**，提供安全的去中心化身分識別（DID）金鑰管理功能。此 SDK 使用 iOS Secure Enclave 進行金鑰儲存，並提供金鑰產生、刪除及數位簽章等核心功能，適用於需要高安全性身分驗證的 iOS 應用程式。

本 SDK 採用 Flutter 混合架構開發，透過 Method Channel 提供原生 iOS 平台的安全金鑰操作能力，並確保私鑰永不離開 Secure Enclave。

---

## 主要功能

- **DID 金鑰管理**：透過 iOS Secure Enclave 進行金鑰的產生、取得與刪除
- **數位簽章**：支援 ES256（ECDSA with P-256）簽章演算法
- **JWK 格式支援**：公鑰以 JSON Web Key（JWK）格式輸出
- **JWS 產生**：提供完整的 JSON Web Signature（JWS）產生功能
- **Flutter 整合**：透過 Method Channel 提供跨平台相容性

---

## 技術架構

### 核心技術

| 技術 | 版本 | 說明 |
|------|------|------|
| iOS | 15.0+ | 最低支援版本 |
| Xcode | 15.0+ | 開發工具 |
| Swift | 5.0 | 程式語言 |
| Flutter SDK | 3.16.5 | 跨平台框架 |
| Secure Enclave | - | 硬體安全模組 |

### 主要相依套件

| 套件名稱 | 用途 |
|----------|------|
| Flutter.framework | Flutter 引擎與執行環境 |
| Security.framework | iOS 安全框架（Secure Enclave、Keychain） |
| CryptoKit | 密碼學運算 |

---

## 系統需求

### 開發環境需求

- **macOS**: 13.0 (Ventura) 或更高版本
- **Xcode**: 15.0 或更高版本
- **Flutter SDK**: 3.16.5
- **CocoaPods**: 1.11.0 或更高版本（選用）
- **支援裝置**: 具備 Secure Enclave 的 iOS 裝置（iPhone 5s 及以後機型）

### 執行環境需求

- **最低 iOS 版本**: iOS 15.0
- **建議 iOS 版本**: iOS 16.0 或更高
- **硬體需求**: 具備 Secure Enclave 的裝置

---

## 快速開始

### 1. 取得原始碼

```bash
git clone https://github.com/moda-gov-tw/TWDIW-official-app.git
cd TWDIW-official-app/APP/moda-digitalwallet-holder-iosapp-token-sdk
```

### 2. 準備 Flutter Framework

請依照 `FLUTTER_SETUP.md` 文件中的指示取得並設定 Flutter.framework 相依套件：

```bash
# 參考 FLUTTER_SETUP.md 下載 Flutter SDK 3.16.5
# 並將 Flutter.framework 放置於正確位置
```

### 3. 開啟專案

使用 Xcode 開啟專案檔案：

```bash
open moda-digitalwallet-holder-sdk.xcodeproj
```

### 4. 編譯專案

#### 使用 xcodebuild 編譯

```bash
# 編譯 Release 版本
xcodebuild \
  -project moda-digitalwallet-holder-sdk.xcodeproj \
  -target moda-digitalwallet-holder-sdk \
  -configuration Release \
  -sdk iphoneos \
  BUILD_LIBRARY_FOR_DISTRIBUTION=YES \
  SKIP_INSTALL=NO \
  clean build
```

編譯成功後，framework 會產生於以下位置：

```
build/Release-iphoneos/moda_digitalwallet_holder_sdk.framework
```

### 5. 驗證建置結果

```bash
# 檢查 framework 結構
ls -lh build/Release-iphoneos/moda_digitalwallet_holder_sdk.framework/

# 驗證二進位檔案類型
file build/Release-iphoneos/moda_digitalwallet_holder_sdk.framework/moda_digitalwallet_holder_sdk

# 預期輸出：Mach-O 64-bit dynamically linked shared library arm64
```

---

## 整合指南

### 加入 Framework 到專案

1. **複製 Framework**

   將編譯好的 framework 複製到您的專案目錄：

   ```bash
   cp -R build/Release-iphoneos/moda_digitalwallet_holder_sdk.framework YourProject/Frameworks/
   ```

2. **連結 Framework**

   在 Xcode 中：
   - 選擇專案 target
   - 前往 `General` > `Frameworks, Libraries, and Embedded Content`
   - 點擊 `+` 並選擇 `moda_digitalwallet_holder_sdk.framework`
   - 設定為 `Embed & Sign`

3. **加入 Flutter Framework**

   同樣將 `Flutter.framework` 加入專案並設定為 `Embed & Sign`

### 使用範例

#### 初始化 DID 金鑰物件

```swift
import moda_digitalwallet_holder_sdk

class YourViewController: UIViewController {
    var modaDidKey:ModaDidKey?

    override func viewDidLoad() {
        super.viewDidLoad()
        // 初始化 DID 金鑰物件，並設定標籤與 PIN
        modaDidKey = ModaDidKey(keyTag: "user_did_key_001", type: .platform, pin: "123456".data(using: .utf8)!)
    }
}
```

#### 取得公鑰（JWK 格式）

```swift
let (publicKey,generateError) = modaDidKey?.getP256Key()
print("公鑰 JWK: \(publicKey), 錯誤訊息: \(generateError)")
```

#### 簽署資料（產生 JWS）

```swift
let headerString = """
{
 "alg": "ES256",
 "typ": "JWT"
}
"""
        
let payloadString = """
{
 "sub": "user123",
        "iat": 1234567890
}
"""

let (signature, signError) = modaDidKey?.sign(header: headerString, payload: payloadString)
print("JWS 簽章: \(signature), 錯誤訊息: \(signError)")
```

#### 驗證使用者（公開金鑰）

```swift
let (isValid, verifyError) = modaDidKey?.verifyUser(publicKey:didPublicKey)
print("驗證結果: \(isValid ? "成功" : "失敗"), 錯誤訊息: \(verifyError)")
```

#### 刪除金鑰

```swift
let (isDeleted, deleteError) = modaDidKey?.delete()
print("金鑰刪除: \(isDeleted), 錯誤訊息: \(deleteError)")
```

---

## API 文件

### 主要方法

| 方法 | 參數 | 回傳值 | 說明 |
|------|------|--------|------|
| `init` | `tag`: String<br>`type`: String<br>`pin`: Data | `Bool` | 初始化 DID 金鑰，並設定標籤與 PIN 碼 |
| `getP256Key` | - | `String` | 取得 P-256 公鑰（JWK 格式） |
| `sign` | `header`: String (JSON)<br>`payload`: String (JSON) | `String` | 簽署資料並回傳 JWS |
| `verifyUser` | `publicKey`: String (JWK) | `Bool` | 使用公開金鑰驗證使用者 |
| `delete` | - | `Bool` | 刪除 DID 金鑰 |

### 錯誤處理

SDK 錯誤處理，錯誤類型包含：

#### 一般錯誤

| 錯誤類型 | 錯誤代碼 | 說明 |
|---------|:--------:|-----|
| `SOFTWARE_KEY_GENERATION_FAILED`          | 800 | 無法生成新的加密金鑰 |
| `SOFTWARE_KEY_NOT_FOUND`                  | 801 | 在 Secure Enclave 中找不到指定的金鑰 |
| `SOFTWARE_SIGNING_FAILED`                 | 802 | 使用私鑰進行簽署時發生錯誤 |
| `SOFTWARE_VERIFICATION_FAILED`            | 803 | 無法驗證簽署的數據 |
| `SOFTWARE_PUBLIC_KEY_EXTRACTION_FAILED`   | 804 | 無法從 Secure Enclave 中提取公鑰 |
| `SOFTWARE_ENCODING_FAILED`                | 805 | 編碼失敗 |
| `SOFTWARE_INVALID_KEY`                    | 806 | 無效的金鑰 |
| `SOFTWARE_UNSUPPORTED_KEY_TYPE_OR_CURVE`  | 807 | 不支援的金鑰類型或曲線 |
| `SOFTWARE_INVALID_COORDINATE_DATA`        | 808 | 無效的座標數據 |
| `SOFTWARE_DER_TO_RAW_CONVERSION_FAILED`   | 809 | 無法將 DER 簽章轉換為 raw 格式 |
| `SOFTWARE_INVALID_SIGNATURE_DATA`         | 810 | 無效的簽章資料格式|
| `SOFTWARE_INVALID_JWK_FORMAT`             | 811 | 無效的JWK格式 |

#### 常見錯誤

以下錯誤列表為 Keychain / Secure Enclave 的常見錯誤類型：

| 錯誤類型 | 錯誤代碼 | 說明 |
|---------|:--------:|-----|
| `errSecSuccess`       | 0      | 操作成功 |
| `errSecItemNotFound`  | -25300 | 找不到項目 |
| `errSecParam`         | -50    | Query dictionary 錯誤 |

---

## 安全性

### 金鑰管理

- **Secure Enclave 儲存**：所有私鑰均儲存於 iOS Secure Enclave 硬體安全模組中
- **金鑰不可匯出**：私鑰設定為不可匯出（non-extractable），無法從 Secure Enclave 取出
- **隔離執行**：金鑰操作在隔離的安全環境中執行
- **自動刪除**：應用程式移除時，金鑰會自動從 Keychain 刪除

### 加密演算法

| 演算法類型 | 規格 | 說明 |
|-----------|------|------|
| 非對稱加密 | P-256 (secp256r1) | NIST 標準橢圓曲線 |
| 簽章演算法 | ES256 (ECDSA with SHA-256) | JSON Web Algorithms 標準 |
| 金鑰長度 | 256-bit | 符合業界安全標準 |

---

## 開發指南

### 專案結構

```
moda-digitalwallet-holder-sdk/
├── TokenSDKManager.swift          # SDK 主要管理器
├── ModaDidKey.swift                # DID 金鑰介面定義
├── Implementations/
│   └── PlatformDIDKey.swift       # 平台原生 DID 金鑰實作
├── Utilities/
│   ├── JWSCreator.swift           # JWS 產生工具
│   ├── JWKComparer.swift          # JWK 比對工具
│   ├── PrintPro.swift             # 除錯輸出工具
│   └── FrameworkLoader.swift      # Framework 載入器
├── Errors/
│   ├── ErrorManager.swift         # 錯誤管理器
│   └── GeneralErrorMessages.swift # 一般錯誤訊息
├── Extensions/
│   ├── Data_Extension.swift       # Data 型別擴充
│   └── String_Extension.swift     # String 型別擴充
├── Flutter.framework/             # Flutter 引擎框架
└── moda_digitalwallet_holder_sdk.swift  # 主要模組檔案
```

### 程式碼風格規範

- 遵循 [Swift API Design Guidelines](https://swift.org/documentation/api-design-guidelines/)
- 使用有意義的變數與方法命名（駝峰式命名法）
- 所有公開 API 需提供完整的文件註解
- 錯誤處理使用 Swift 的 `throws` 與 `Result` 型別
- 非同步操作使用 `async/await` 語法

#### 命名規範

```swift
// ✅ 良好的命名
func generateDIDKey(withTag tag: String, pin: String) throws -> Bool
let publicKeyJWK: String
private var secureEnclaveKey: SecKey?

// ❌ 不良的命名
func genKey(t: String, p: String) -> Bool
let pk: String
var key: SecKey?
```

#### 錯誤處理

```swift
// ✅ 使用明確的錯誤類型
enum DIDKeyError: Error {
    case keyGenerationFailed
    case invalidParameters(String)
    case authenticationRequired
}

// ✅ 錯誤傳播
func signData(_ data: Data) throws -> String {
    guard let key = self.privateKey else {
        throw DIDKeyError.keyGenerationFailed
    }
    // ...
}
```

---

## 疑難排解

### 常見問題

#### 1. 編譯錯誤：找不到 Flutter.framework

**問題**：
```
Module 'Flutter' not found
```

**解決方案**：
- 確認 `Flutter.framework` 已正確放置於 `moda-digitalwallet-holder-sdk/` 目錄
- 參考 `FLUTTER_SETUP.md` 重新設定 Flutter framework
- 確認 Framework Search Paths 包含正確路徑

#### 2. 執行時錯誤：Secure Enclave 不可用

**問題**：
```
com.digitalwallet.security.secureEnclaveNotAvailable
```

**解決方案**：
- 確認在實體裝置上執行（模擬器不支援 Secure Enclave）
- 確認裝置支援 Secure Enclave（iPhone 5s 及之後機型）
- 檢查裝置是否設定螢幕鎖定密碼


#### 3. Mac Catalyst 編譯錯誤

**問題**：
```
building for 'macCatalyst', but linking in dylib built for 'iOS'
```

**解決方案**：
- 本 SDK 僅支援 iOS 平台，不支援 Mac Catalyst
- 確認專案設定中 `SUPPORTS_MACCATALYST = NO`
- 使用 `-sdk iphoneos` 參數編譯

---

## 支援與聯絡

- **官方網站**：[https://wallet.gov.tw](https://wallet.gov.tw)
- **技術支援信箱**：service@wallet.gov.tw
- **問題回報**：[GitHub Issues](https://github.com/moda-gov-tw/TWDIW-official-app/issues)
- **貢獻指南**：請參閱專案根目錄的 [CONTRIBUTING.md](../../CONTRIBUTING.md)

---

## 變更日誌

詳細的變更歷史記錄請參閱 [CHANGELOG.md](CHANGELOG.md)

---

## 致謝

本專案使用以下優秀的開源專案與技術：

- [Flutter](https://flutter.dev/) - Google 開發的跨平台 UI 框架
- [Apple CryptoKit](https://developer.apple.com/documentation/cryptokit) - Apple 的密碼學框架
- [Swift](https://swift.org/) - Apple 的程式語言
- [iOS Secure Enclave](https://support.apple.com/zh-tw/guide/security/sec59b0b31ff/web) - Apple 的硬體安全模組

感謝所有對本專案做出貢獻的開發者與社群成員。

---

## 授權條款

本專案為 [TWDIW Official App](https://github.com/moda-gov-tw/TWDIW-official-app) 的一部分，採用 MIT License 授權 - 詳細內容請參閱專案根目錄的 [LICENSE](../../LICENSE) 檔案。

---

**© 2025 Digital Wallet Holder SDK Contributors**
