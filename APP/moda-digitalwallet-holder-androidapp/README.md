# 數位發展部數位憑證皮夾 Android App

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Android](https://img.shields.io/badge/Android-9.0%2B-green.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.23-blue.svg)](https://kotlinlang.org/)
[![Gradle](https://img.shields.io/badge/Gradle-8.10.2-02303A.svg)](https://gradle.org)

---

## 目錄

- [專案簡介](#專案簡介)
- [主要功能](#主要功能)
- [技術架構](#技術架構)
  - [核心技術](#核心技術)
  - [主要相依套件](#主要相依套件)
  - [Flutter SDK 模組](#flutter-sdk-模組)
- [系統需求](#系統需求)
  - [開發環境需求](#開發環境需求)
  - [執行環境需求](#執行環境需求)
- [快速開始](#快速開始)
  - [取得原始碼](#1-取得原始碼)
  - [設定 Android SDK](#2-設定-android-sdk)
  - [編譯專案](#3-編譯專案)
  - [執行應用程式](#4-執行應用程式)
- [專案結構](#專案結構)
- [開發指南](#開發指南)
  - [程式碼風格規範](#程式碼風格規範)
  - [除錯與測試](#除錯與測試)
- [常見問題](#常見問題)
- [開源授權](#開源授權)
  - [本專案授權](#本專案授權)
  - [第三方相依套件授權](#第三方相依套件授權)
  - [開源風險評估](#開源風險評估)
- [貢獻指南](#貢獻指南)
- [支援與聯絡](#支援與聯絡)
- [致謝](#致謝)

---

## 專案簡介

本專案為數位發展部數位憑證皮夾之 **Android 持有端應用程式**，提供使用者安全且便利的數位憑證管理解決方案。本應用程式整合核心 SDK，讓使用者能夠安全地持有、管理和出示各類數位憑證，包含可驗證憑證（Verifiable Credential, VC）的加入與驗證憑證（Verifiable Presentation, VP）的出示功能。透過直觀的操作介面與多層安全機制，提供完整的數位身分管理功能。

---

## 主要功能

### 憑證管理
- **加入憑證（Verifiable Credential, VC）**
  - QR Code 掃描加入憑證
  - 搜尋可申請憑證

- **管理憑證**
  - 皮夾管理
  - 憑證排序
  - 憑證歷史記錄
  - 查看憑證資訊

- **出示憑證（Verifiable Presentation, VP）**
  - 憑證驗證與展示
  - QR Code 動態展示
  - 支援憑證選擇性揭露
  - 確保憑證真實性驗證

### 安全性功能
- **生物辨識驗證**
  - 指紋辨識支援
  - PIN 碼驗證機制
  - 圖形驗證機制

- **資料加密**
  - 本地資料庫加密（SQLCipher）
  - SharedPreferences 加密
  - Android KeyStore 金鑰管理

### 憑證狀態管理
- 憑證有效性即時驗證
- 自動更新憑證狀態

### 操作記錄
- 授權記錄查詢
- 憑證異動記錄
- 操作歷程追蹤

---

## 技術架構

本專案採用 **MVVM (Model-View-ViewModel)** 架構模式，結合 **Repository Pattern** 進行資料存取，確保程式碼的可維護性、可測試性與模組化設計。使用 Hilt 實作相依性注入，降低耦合度。

### 核心技術

| 技術 | 版本 | 說明 |
|------|------|------|
| Kotlin | 1.9.23 | 主要開發語言 |
| Android Gradle Plugin | 8.6.1 | 建構工具 |
| Gradle | 8.10.2 | 專案建構系統 |
| Java | 17 (OpenJDK) | 執行環境 |
| Android SDK | API 35 | 編譯版本 |
| Min SDK | API 28 (Android 9.0) | 最低支援版本 |
| Target SDK | API 35 | 目標版本 |

### 主要相依套件

| 套件名稱 | 版本 | 用途說明 |
|---------|------|---------|
| **AndroidX Core KTX** | 1.13.1 | Android KTX 核心擴充函式庫 |
| **AndroidX AppCompat** | 1.7.0 | 向下相容支援函式庫 |
| **Material Components** | 1.12.0 | Google Material Design 元件庫 |
| **ConstraintLayout** | 2.2.1 | 靈活的約束佈局系統 |
| **Hilt** | 2.52 | 相依性注入框架（基於 Dagger） |
| **Lifecycle (LiveData & ViewModel)** | 2.8.6 | 生命週期感知元件 |
| **Fragment KTX** | 1.8.4 | Fragment 擴充功能 |
| **Room** | 2.6.1 | 本地資料庫 ORM 框架 |
| **SQLCipher** | 4.9.0 | 資料庫加密函式庫 |
| **Security Crypto** | 1.1.0-alpha06 | 加密 SharedPreferences 支援 |
| **Biometric** | 1.1.0 | 生物辨識驗證 API |
| **ZXing Android Embedded** | 4.3.0 | QR Code 掃描與產生 |
| **ZXing Core** | 3.5.3 | 條碼處理核心函式庫 |
| **Gson** | 2.11.0 | JSON 序列化/反序列化 |
| **Lottie** | 6.6.6 | After Effects 動畫播放器 |
| **ML Kit Barcode Scanning** | 17.3.0 | Google ML Kit 條碼掃描 |
| **Play Services** | 18.5.0 | Google Play 服務基礎套件 |
| **SwipeRefreshLayout** | 1.1.0 | 下拉更新功能 |

### Flutter SDK 模組

專案包含一個本地 Flutter SDK 模組（`com.example.did_sdk_module`），位於 `repo/` 目錄：

| 模組 | 大小 | 說明 |
|------|------|------|
| **flutter_debug-1.0.aar** | 17MB | Debug 編譯版本 |
| **flutter_release-1.0.aar** | 4.0MB | Release 編譯版本 |

此模組提供 DID (Decentralized Identifier) 相關功能，包含：
- 數位身分識別處理
- 憑證加密與驗證
- 與 Flutter 框架整合的原生功能

**授權資訊：** 此模組採用 Flutter Framework (BSD 3-Clause) 與 Apache 2.0 授權，與本專案的 MIT 授權相容。

---

## 系統需求

### 開發環境需求

- **Android Studio**: Android Studio Koala Feature Drop | 2024.1.2 或以上
- **JDK**: OpenJDK 17 或更高版本
- **Android SDK**:
  - SDK Platform: API 35
  - SDK Build Tools: 35.0.0
  - SDK Platform Tools: 36.0.0 或以上
- **作業系統**: Windows、macOS 或 Linux
- **記憶體**: 建議至少 8GB RAM
- **磁碟空間**: 建議至少 10GB 可用空間

### 執行環境需求

- **Android 作業系統**: Android 9.0 (API 28) 或更高版本
- **硬體需求**:
  - 相機：支援 QR Code 掃描功能
  - 指紋感應器：支援生物辨識功能（選用）
- **儲存空間**: 建議至少 500MB 可用空間

---

## 快速開始

### 1. 取得原始碼

```bash
git clone https://github.com/moda-gov-tw/TWDIW-official-app.git
cd TWDIW-official-app/APP/moda-digitalwallet-holder-androidapp
```

### 2. 設定 Android SDK

#### 方法 A：建立 local.properties 檔案

在專案根目錄建立 `local.properties` 檔案：

```properties
# Android SDK 路徑
sdk.dir=/path/to/your/Android/sdk
```

**常見路徑：**
- **macOS**: `/Users/<使用者名稱>/Library/Android/sdk`
- **Windows**: `C:\\Users\\<使用者名稱>\\AppData\\Local\\Android\\sdk`
- **Linux**: `/home/<使用者名稱>/Android/sdk`

#### 方法 B：設定環境變數

**Linux/macOS:**
```bash
export ANDROID_HOME=/path/to/your/Android/sdk
export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools
```

**Windows (PowerShell):**
```powershell
$env:ANDROID_HOME="C:\Users\<使用者名稱>\AppData\Local\Android\sdk"
$env:Path += ";$env:ANDROID_HOME\tools;$env:ANDROID_HOME\platform-tools"
```

### 3. 編譯專案

#### 使用 Gradle Wrapper（建議）

```bash
# 清理專案
./gradlew clean

# 編譯 Debug 版本
./gradlew assembleProdDebug

# 編譯 Release 版本（未簽章）
./gradlew assembleProdRelease

# 完整建置（包含所有版本）
./gradlew build
```

**Windows 使用者請使用：**
```cmd
gradlew.bat clean
gradlew.bat assembleProdDebug
```

編譯成功後，APK 檔案會產生在以下位置：
- **Debug APK**: `app/build/outputs/apk/prod/debug/moda-1.0.0+20251117_01-prod-debug.apk`
- **Release APK**: `app/build/outputs/apk/prod/release/moda-1.0.0+20251117_01-prod-release-unsigned.apk`

### 4. 執行應用程式

#### 使用 Android Studio（建議）

1. 開啟 Android Studio
2. 選擇 `File` → `Open`，選取專案目錄
3. 等待 Gradle 同步完成
4. 連接 Android 裝置或啟動模擬器
5. 點選 `Run` → `Run 'app'` 或按下 `Shift + F10`

#### 使用指令列

```bash
# 安裝到連接的裝置
./gradlew installProdDebug

# 執行應用程式
adb shell am start -n tw.gov.moda.diw/.ui.splash.SplashActivity
```

---

## 專案結構

```
app/src/main/java/tw/gov/moda/digitalwallet/
├── ui/                       # 視圖層（Activities & Fragments）
│   ├── main/                     # 主畫面
│   ├── splash/                   # 啟動畫面
│   ├── login/                    # 登入相關
│   ├── create/                   # 建立皮夾
│   ├── home/                     # 主要功能頁面
│   │   ├── barcode/                 # 出示憑證
│   │   ├── add/                     # 加入憑證
│   │   ├── wallet/                  # 管理憑證
│   │   └── setting/                 # 設定
│   ├── verifiable/               # 憑證相關
│   │   └── presentation/            # 憑證出示
│   ├── record/                   # 操作記錄
│   ├── base/                     # 基礎 UI 元件
│   └── adapter/                  # RecyclerView 適配器
├── core/                     # 核心邏輯層
│   ├── repository/              # 資料存取層
│   │   ├── wallet/                 # 皮夾資料存取
│   │   ├── verifiable/             # 憑證資料存取
│   │   └── barcode/                # 條碼資料存取
│   ├── verifiable/              # 憑證業務邏輯
│   ├── wallet/                  # 錢包業務邏輯
│   ├── identifier/              # 裝置識別管理
│   ├── biometric/               # 生物辨識管理
│   ├── keystore/                # 金鑰管理
│   ├── db/                      # 資料庫 DAO
│   ├── pref/                    # SharedPreferences 代理器
│   ├── network/                 # 網路請求處理
│   └── deeplink/                # Deep Link 處理
├── data/                     # 資料層
│   ├── model/                   # 資料模型
│   ├── db/                      # Room Database 實體
│   ├── element/                 # UI 元素資料類別
│   └── annotation/              # API 方法註解定義
├── di/                       # 相依性注入
│   └── module/                  # Hilt Modules
├── common/                   # 共用元件與常數
├── extension/                # Kotlin 擴充函式
├── exception/                # 自訂例外類別
└── util/                     # 工具類別
```

---

## 開發指南

### 程式碼風格規範

本專案遵循以下程式碼規範：

#### Kotlin 程式碼風格
- 遵循 [Kotlin 官方程式碼風格指南](https://kotlinlang.org/docs/coding-conventions.html)
- 使用有意義的變數與函式命名
- 類別名稱使用大駝峰式命名（PascalCase）
- 函式與變數使用小駝峰式命名（camelCase）
- 常數使用全大寫加底線（UPPER_SNAKE_CASE）

#### 架構設計原則
- **MVVM 架構**：視圖層、ViewModel 層、Model 層分離
- **Repository Pattern**：統一資料存取介面
- **單一職責原則**：每個類別只負責一項功能
- **相依性注入**：使用 Hilt 進行相依性管理

#### 註解規範
- 公開的 API 必須撰寫 KDoc 註解
- 複雜的業務邏輯需加上說明註解
- TODO 註解需包含負責人與日期

### 除錯與測試

#### 開啟 Debug 模式

在 `app/build.gradle.kts` 中：

```kotlin
buildTypes {
    debug {
        isMinifyEnabled = false
        isDebuggable = true  // 啟用除錯
        isShrinkResources = false
    }
}
```

#### 查看日誌

```bash
# 過濾應用程式日誌
adb logcat | grep "tw.gov.moda.diw"

# 清除日誌後重新捕捉
adb logcat -c && adb logcat
```

#### 單元測試

```bash
# 執行所有單元測試
./gradlew test

# 執行特定測試類別
./gradlew test --tests "tw.gov.moda.digitalwallet.core.WalletManagerTest"
```

---

## 常見問題

### 1. SDK not found 錯誤

**問題：** 編譯時出現 `SDK location not found` 錯誤

**解決方案：**
1. 確認 `local.properties` 檔案存在且 `sdk.dir` 路徑正確
2. 或設定 `ANDROID_HOME` 環境變數
3. 檢查 Android SDK 是否已正確安裝

### 2. Gradle 快取問題

**問題：** 編譯時出現 Gradle 快取錯誤

**解決方案：**
```bash
# 停止所有 Gradle daemon
./gradlew --stop

# 清理快取
rm -rf ~/.gradle/caches

# 重新編譯
./gradlew clean build
```

### 3. Flutter 模組依賴問題

**問題：** 無法找到 Flutter SDK 模組

**解決方案：**
1. 確認 `repo/` 目錄完整包含所有 Flutter SDK 模組檔案
2. 檢查 `build.gradle.kts` 中的 repository 設定是否正確

### 4. 建置速度緩慢

**問題：** Gradle 建置速度過慢

**解決方案：**
在 `gradle.properties` 中加入以下設定：
```properties
org.gradle.daemon=true
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.jvmargs=-Xmx4096m
```

---

## 開源授權

### 本專案授權

本專案採用 **MIT License** 授權。詳見上層專案的 [LICENSE.txt](../../LICENSE.txt) 檔案。

### 第三方相依套件授權

本專案使用的第三方相依套件採用以下開源授權：

| 授權類型 | 套件 |
|---------|------|
| **Apache 2.0** | AndroidX, Hilt, Room, Gson, ZXing, Lottie, ML Kit |
| **BSD License** | Flutter Framework, SQLCipher |


---

## 貢獻指南

我們歡迎社群貢獻！如果您想為本專案做出貢獻，請參閱上層專案的 [貢獻指南 (CONTRIBUTING.md)](../../CONTRIBUTING.md)。

### Android App 專屬注意事項

在提交 Pull Request 前，請確保：

- 遵循 [Kotlin 官方程式碼風格指南](https://kotlinlang.org/docs/coding-conventions.html)
- 執行 `./gradlew build` 確保編譯成功
- 新增的功能都有適當的註解說明
- 測試在實體裝置或模擬器上正常運作

---

## 支援與聯絡

### 問題回報

如有任何問題或建議，歡迎透過以下方式聯絡：

- **問題回報**: [GitHub Issues](https://github.com/moda-gov-tw/TWDIW-official-app/issues)
- **技術支援信箱**: service@wallet.gov.tw

---

## 致謝

本專案使用以下優秀的開源專案與函式庫：

### 核心框架
- [Android Jetpack](https://developer.android.com/jetpack) - Google 官方元件庫
- [Kotlin](https://kotlinlang.org/) - 現代化的程式語言
- [Hilt](https://dagger.dev/hilt/) - 相依性注入框架

### 資料處理
- [Room](https://developer.android.com/training/data-storage/room) - 資料庫框架
- [SQLCipher](https://www.zetetic.net/sqlcipher/) - 資料庫加密
- [Gson](https://github.com/google/gson) - JSON 處理

### UI 元件
- [Material Components](https://material.io/develop/android) - Material Design 元件
- [Lottie](https://airbnb.design/lottie/) - 動畫函式庫
- [ZXing](https://github.com/zxing/zxing) - QR Code 處理

### 安全性
- [AndroidX Security](https://developer.android.com/topic/security) - 加密工具
- [AndroidX Biometric](https://developer.android.com/jetpack/androidx/releases/biometric) - 生物辨識

### 其他
- [Flutter](https://flutter.dev/) - Flutter SDK 模組
- [Google ML Kit](https://developers.google.com/ml-kit) - 機器學習工具

