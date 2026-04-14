# 數位發展部數位皮夾持有者 SDK

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)](https://kotlinlang.org)
[![API](https://img.shields.io/badge/API-29%2B-brightgreen.svg)](https://android-doc.github.io/about/versions/android-10.0.html)

---

### 目錄

- [專案簡介](#專案簡介)
- [主要功能](#主要功能)
- [技術架構](#技術架構)
  - [核心技術](#核心技術)
  - [主要相依套件](#主要相依套件)
- [系統需求](#系統需求)
  - [開發環境需求](#開發環境需求)
  - [執行環境需求](#執行環境需求)
- [快速開始](#快速開始)
  - [取得原始碼](#1-取得原始碼)
  - [編譯 SDK](#2-編譯-sdk)
  - [整合到您的專案](#3-整合到您的專案)
  - [設定權限](#4-設定權限)
  - [驗證編譯結果](#5-驗證編譯結果)
- [使用說明](#使用說明)
  - [初始化 SDK](#初始化-sdk)
  - [金鑰管理](#金鑰管理)
  - [數位簽章](#數位簽章)
  - [NFC 通訊](#nfc-通訊)
- [核心功能詳解](#核心功能詳解)
  - [DID 金鑰管理](#did-金鑰管理)
  - [JWT/JWS 簽章](#jwtjws-簽章)
  - [NFC 讀寫功能](#nfc-讀寫功能)
- [安全性](#安全性)
  - [硬體級安全機制](#硬體級安全機制)
  - [加密標準](#加密標準)
  - [安全最佳實踐](#安全最佳實踐)
- [開發指南](#開發指南)
  - [專案結構](#專案結構)
  - [程式碼風格規範](#程式碼風格規範)
- [疑難排解](#疑難排解)
- [支援與聯絡](#支援與聯絡)
- [變更日誌](#變更日誌)
- [授權條款](#授權條款)
- [致謝](#致謝)

---

### 專案簡介

本專案為數位發展部數位皮夾系統的 **持有者端 SDK（Holder SDK）**，提供行動裝置端的金鑰管理、數位簽章與 NFC 通訊功能。此 SDK 專為數位身分識別應用場景設計，採用硬體級安全機制保護使用者的私密金鑰，確保數位憑證的安全性與可信度。

本 SDK 部署於使用者的 Android 行動裝置上，負責：
- 在裝置安全區域內生成與管理 DID 金鑰
- 執行符合 JWT/JWS 標準的數位簽章
- 透過 NFC 進行安全的資料傳輸
- 與 Flutter 應用程式整合，提供完整的數位皮夾功能

### 主要功能

- **DID 金鑰管理**：支援 P-256 橢圓曲線金鑰的生成、取得、刪除與驗證
- **硬體級安全**：使用 Android KeyStore 和 StrongBox（如裝置支援）保護私鑰
- **數位簽章**：實作符合 JWT/JWS 標準的數位簽章功能
- **NFC 通訊**：完整的 NFC 讀寫功能，支援 ISO-DEP 協定
- **Flutter 整合**：透過 Method Channel 與 Flutter 應用程式無縫整合
- **身分驗證**：驗證金鑰持有者身分，確保金鑰所有權

### 技術架構

#### 核心技術

| 技術 | 版本 | 說明 |
|------|------|------|
| Kotlin | 1.8.20 | 程式語言 |
| Android SDK | API 29+ (Android 10+) | 最低支援版本 |
| Gradle | 8.2 | 建置工具 |
| Android KeyStore | - | 金鑰儲存系統 |
| StrongBox | - | 硬體安全模組（選用） |
| NFC | ISO-DEP | NFC 通訊協定 |

#### 主要相依套件

| 套件名稱 | 版本 | 用途 |
|----------|------|------|
| Kotlin Coroutines | 1.7.3 | 非同步處理 |
| Flutter Embedding | 1.0.0 | Flutter 整合 |
| Android Security | - | 金鑰管理與加密 |
| Android NFC | - | NFC 通訊 |

### 系統需求

#### 開發環境需求

- **作業系統**：Windows 10+、macOS 11+、Linux (Ubuntu 20.04+)
- **Android Studio**：Arctic Fox (2020.3.1) 或更新版本
- **JDK**：OpenJDK 11 或更高版本（建議使用 OpenJDK 17）
- **Gradle**：8.0 或更高版本（專案已包含 Gradle Wrapper）
- **Flutter SDK**：3.27.4 或更新版本
- **Android SDK**：
  - 最低 API Level: 29 (Android 10)
  - 目標 API Level: 33 (Android 13)
  - Build Tools: 30.0.3 或更高版本

#### 執行環境需求

- **Android 版本**：Android 10 (API 29) 或更高
- **硬體需求**：
  - NFC 功能（必要）
  - StrongBox 支援（選用，可提升安全性）
- **記憶體**：建議至少 2GB RAM
- **儲存空間**：至少 100MB 可用空間

### 快速開始

#### 1. 取得原始碼

```bash
# 使用 Git 複製專案
git clone https://github.com/moda-gov-tw/TWDIW-official-app.git
cd TWDIW-official-app/APP/moda-digitalwallet-holder-androidapp-token-sdk
```

#### 2. 編譯 SDK

##### 使用 Gradle Wrapper 編譯（建議）

```bash
# Linux/macOS
chmod +x gradlew
./gradlew clean assembleRelease

# Windows
gradlew.bat clean assembleRelease
```

##### 使用 Gradle 編譯

```bash
# 清理並編譯 Release 版本
gradle clean assembleRelease

# 編譯 Debug 版本
gradle assembleDebug
```

編譯成功後，會在 `app/build/outputs/aar/` 目錄下產生 AAR 檔案：

```
app/build/outputs/aar/
└── app-release.aar (約 27KB)
```

#### 3. 整合到您的專案

##### 步驟 3.1：複製 AAR 檔案

將產生的 `app-release.aar` 複製到您的 Android 專案的 `libs` 目錄：

```bash
cp app/build/outputs/aar/app-release.aar /path/to/your-project/app/libs/
```

##### 步驟 3.2：設定 Gradle 相依性

在您的應用程式模組的 `build.gradle` 檔案中新增相依套件：

```gradle
dependencies {
    // MODA Digital Wallet Holder SDK
    implementation files('libs/app-release.aar')

    // 必要的相依套件
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'

    // Flutter Embedding（如使用 Flutter 整合）
    // 注意：此版本對應 Flutter SDK 3.27.4
    compileOnly 'io.flutter:flutter_embedding_release:1.0.0-2a84ea55e4ef4e573b8277dd024fd097cd2242f8'
}
```

##### 步驟 3.3：設定 ProGuard 規則（選用）

如果您的專案啟用了程式碼混淆，請在 `proguard-rules.pro` 中新增：

```proguard
# MODA Digital Wallet Holder SDK
-keep class com.moda.pkcsnfckit.** { *; }
-keepclassmembers class com.moda.pkcsnfckit.** { *; }
```

#### 4. 設定權限

在您的應用程式的 `AndroidManifest.xml` 中新增必要的權限：

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <!-- NFC 功能權限（必要） -->
    <uses-feature
        android:name="android.hardware.nfc"
        android:required="true" />

    <application>
        <!-- 您的應用程式設定 -->
    </application>

</manifest>
```

#### 5. 驗證編譯結果

編譯您的應用程式以驗證 SDK 整合成功：

```bash
# 編譯您的應用程式
./gradlew assembleDebug

# 或直接在 Android Studio 中點選 Build > Make Project
```

### 使用說明

#### 初始化 SDK

```kotlin
import com.moda.pkcsnfckit.TokenSDKManager
import com.moda.pkcsnfckit.ModaDidKey
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.dart.DartExecutor
import android.content.Context

class YourActivity : AppCompatActivity() {

    private lateinit var flutterEngine: FlutterEngine
    private lateinit var tokenSDKManager: TokenSDKManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 初始化 Flutter Engine
        flutterEngine = FlutterEngine(applicationContext)
        flutterEngine.dartExecutor.executeDartEntrypoint(
            DartExecutor.DartEntrypoint.createDefault()
        )

        // 建立 SDK Manager
        tokenSDKManager = TokenSDKManager(flutterEngine, this)
    }
}
```

#### 金鑰管理

```kotlin
import com.moda.pkcsnfckit.ModaDidKey
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers

// 建立金鑰管理實例
val didKey = ModaDidKey(
    keyTag = "my-did-key",
    type = ModaDidKey.SourceType.PLATFORM,
    PIN = "",
    context = applicationContext
)

// 取得公鑰
lifecycleScope.launch(Dispatchers.IO) {
    val publicKey = didKey.getP256Key()
    // publicKey 為 JWK 格式的公鑰字串
}

// 刪除金鑰
lifecycleScope.launch(Dispatchers.IO) {
    val isDeleted = didKey.delete()
}

// 驗證使用者
lifecycleScope.launch(Dispatchers.IO) {
    val isValid = didKey.verifyUser(publicKeyJwk)
}
```

#### 數位簽章

```kotlin
import com.moda.pkcsnfckit.ModaDidKey
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers

lifecycleScope.launch(Dispatchers.IO) {
    // 準備簽章資料
    val header = """{"alg":"ES256","typ":"JWT"}"""
    val payload = """{"sub":"user123","iat":1234567890}"""

    // 執行簽章
    val jws = didKey.sign(header, payload)
    // jws 為完整的 JWS 格式字串：header.payload.signature
}
```

#### NFC 通訊

```kotlin
import com.moda.pkcsnfckit.ModaNFCReader

// 建立 NFC Reader
val nfcReader = ModaNFCReader(this)

// 準備要傳送的資料
val dataToSend = "your-data".toByteArray()

// 開始 NFC 傳輸
nfcReader.startSending(dataToSend, object : ModaNFCReader.ScanCallback {
    override fun onFinish(response: ByteArray) {
        // 傳輸成功，處理回應資料
        Log.d("NFC", "Received: ${String(response)}")
    }

    override fun onFailed() {
        // 傳輸失敗
        Log.e("NFC", "NFC transmission failed")
    }
})

// 取消 NFC 傳輸
nfcReader.cancelSending()
```

### 核心功能詳解

#### DID 金鑰管理

本 SDK 使用 Android KeyStore 系統安全地管理 DID 金鑰：

**金鑰生成**
- 自動生成 P-256 (secp256r1) 橢圓曲線金鑰對
- 私鑰永久儲存在 Android KeyStore 中，無法匯出
- 支援 StrongBox 硬體安全模組（如裝置支援）

**金鑰格式**
- 公鑰：JWK (JSON Web Key) 格式
- 支援的曲線：P-256 (secp256r1)
- 金鑰用途：數位簽章與驗證

**金鑰生命週期**
```kotlin
// 1. 自動生成金鑰（首次呼叫 getP256Key 時）
val publicKey = didKey.getP256Key()

// 2. 使用金鑰進行簽章
val signature = didKey.sign(header, payload)

// 3. 驗證金鑰持有者
val isValid = didKey.verifyUser(publicKeyJwk)

// 4. 刪除金鑰
val isDeleted = didKey.delete()
```

#### JWT/JWS 簽章

實作符合 RFC 7515 (JWS) 與 RFC 7519 (JWT) 標準的數位簽章：

**支援的演算法**
- ES256 (ECDSA with P-256 and SHA-256)

**簽章格式**
```
Base64Url(Header).Base64Url(Payload).Base64Url(Signature)
```

**JWK 格式範例**
```json
{
  "kty": "EC",
  "crv": "P-256",
  "x": "base64url-encoded-x-coordinate",
  "y": "base64url-encoded-y-coordinate"
}
```

#### NFC 讀寫功能

支援多種 NFC 技術與 ISO-DEP 協定：

**支援的 NFC 技術**
- ISO-DEP (ISO 14443-4)
- NFC-A (ISO 14443-3A)
- NFC-B (ISO 14443-3B)
- NFC-F (JIS X 6319-4)
- NFC-V (ISO 15693)
- NDEF
- NDEF Formatable
- Mifare Classic
- Mifare Ultralight

**通訊特性**
- 支援大型資料分片傳輸（每片最大 255 bytes）
- 自動重組接收資料
- 6 秒逾時保護
- 完整的錯誤處理機制

### 安全性

#### 硬體級安全機制

1. **Android KeyStore**
   - 私鑰儲存於系統安全區域
   - 私鑰無法匯出或讀取
   - 硬體支援的金鑰隔離

2. **StrongBox 支援**
   - 使用獨立的安全處理器
   - 通過 Common Criteria 認證
   - 防止側通道攻擊

#### 加密標準

| 項目 | 標準 | 說明 |
|------|------|------|
| 金鑰曲線 | P-256 (secp256r1) | NIST 標準橢圓曲線 |
| 簽章演算法 | ES256 (ECDSA) | SHA-256 搭配 ECDSA |
| 雜湊演算法 | SHA-256 | 256-bit 安全雜湊 |
| 金鑰長度 | 256-bit | 等同 AES-128 安全強度 |

#### 安全最佳實踐

1. **Release 版本混淆**
   - 啟用 ProGuard/R8 程式碼混淆
   - 移除所有 Log 輸出
   - 隱藏內部實作細節

2. **錯誤處理**
   - 不在日誌中記錄敏感資料
   - 提供通用錯誤訊息給使用者
   - 詳細錯誤僅供內部除錯

3. **金鑰管理建議**
   - 為每個使用者使用獨立的 keyTag
   - 定期輪替金鑰（建議每年一次）
   - 刪除金鑰前先備份重要資料

### 開發指南

#### 專案結構

```
moda-digitalwallet-holder-sdk/
├── app/                              # 主要模組
│   ├── src/
│   │   └── main/
│   │       ├── java/com/moda/pkcsnfckit/
│   │       │   ├── JWSCreator.kt     # JWT/JWS 處理
│   │       │   ├── ModaDidKey.kt     # DID 金鑰管理
│   │       │   ├── ModaNFCReader.kt  # NFC 讀寫功能
│   │       │   └── TokenSDKManager.kt # SDK 管理器
│   │       ├── res/
│   │       │   └── xml/
│   │       │       └── nfc_tech_filter.xml # NFC 技術過濾器
│   │       └── AndroidManifest.xml
│   ├── build.gradle                  # 模組建置設定
│   └── proguard-rules.pro           # ProGuard 規則
├── gradle/                           # Gradle Wrapper
├── build.gradle                      # 根專案建置設定
├── settings.gradle                   # Gradle 設定
├── gradle.properties                 # Gradle 屬性
├── README.md                         # 本檔案
├── CHANGELOG.md                      # 變更日誌
└── OPENSOURCE_PREPARATION.md         # 開源準備指南
```

#### 程式碼風格規範

本專案遵循 Kotlin 官方程式碼風格規範：

- **命名規則**
  - 類別：PascalCase（例如：`ModaDidKey`）
  - 函式：camelCase（例如：`getP256Key`）
  - 常數：UPPER_SNAKE_CASE（例如：`SELECT_APDU_TagInfo`）
  - 私有屬性：camelCase，前綴 `m`（例如：`mFlutterEngine`）

- **程式碼格式**
  - 縮排：4 個空格
  - 每行最多 120 字元
  - 使用有意義的變數與函式名稱

- **註解規範**
  - 公開 API 使用 KDoc 格式
  - 複雜邏輯加上說明註解
  - 註解使用繁體中文

### 疑難排解

#### 常見問題

**Q1：編譯時找不到 Flutter Embedding**

```
Could not find io.flutter:flutter_embedding_release:1.0.0
```

**解決方式：**
- 確認 Flutter SDK 已正確安裝
- 檢查 Gradle 設定中的 Maven 倉庫設定
- 使用 `compileOnly` 替代 `implementation`

**Q2：StrongBox 不可用錯誤**

```
StrongBoxUnavailableException
```

**解決方式：**
- 此為正常行為，SDK 會自動降級使用一般 KeyStore
- StrongBox 僅在特定高階裝置上可用
- 不影響功能，僅安全等級略有差異

**Q3：NFC 傳輸逾時**

```
NFC transmission timeout after 6000ms
```

**解決方式：**
- 確認裝置 NFC 功能已開啟
- 保持裝置與 NFC 標籤貼近
- 避免金屬物品干擾
- 檢查 NFC 標籤是否支援 ISO-DEP

**Q4：金鑰生成失敗**

```
KeyStore initialization failed
```

**解決方式：**
- 確認 Android 版本 ≥ 10 (API 29)
- 檢查 KeyStore 服務是否正常
- 嘗試重新啟動裝置

### 支援與聯絡

- **官方網站**：[https://wallet.gov.tw](https://wallet.gov.tw)
- **技術支援信箱**：service@wallet.gov.tw
- **問題回報**：[GitHub Issues](https://github.com/moda-gov-tw/TWDIW-official-app/issues)

### 變更日誌

詳細的版本更新記錄請參閱 [CHANGELOG.md](CHANGELOG.md)

**最新版本：v1.0.0** (2025-11-11)
- 初始發布版本
- DID 金鑰生成、取得、刪除與簽章功能
- NFC 讀寫支援
- Flutter 整合
- 完整的 JWT/JWS 簽章實作

### 授權條款

本專案採用 MIT 授權條款，詳情請參閱專案根目錄的 [LICENSE.txt](../../LICENSE.txt) 檔案。

### 致謝

本專案使用以下優秀的開源專案與技術：

- [Kotlin](https://kotlinlang.org/) - 現代化的 JVM 程式語言
- [Android](https://www.android.com/) - Google 行動作業系統
- [Flutter](https://flutter.dev/) - Google 跨平台應用程式框架
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) - 非同步程式設計支援

特別感謝所有為數位皮夾生態系統貢獻心力的開發者與合作夥伴。
