# 數位憑證皮夾 iOS 持有端應用程式

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](../../LICENSE.txt)
[![iOS](https://img.shields.io/badge/iOS-13.0+-blue.svg)](https://developer.apple.com/ios/)
[![Swift](https://img.shields.io/badge/Swift-5.9.2-orange.svg)](https://swift.org/)
[![Xcode](https://img.shields.io/badge/Xcode-16.2+-blue.svg)](https://developer.apple.com/xcode/)

---

## 目錄

- [專案簡介](#專案簡介)
- [主要功能](#主要功能)
- [技術架構](#技術架構)
  - [核心技術](#核心技術)
  - [主要相依套件](#主要相依套件)
  - [專案結構](#專案結構)
- [系統需求](#系統需求)
  - [開發環境需求](#開發環境需求)
  - [執行環境需求](#執行環境需求)
- [快速開始](#快速開始)
  - [取得原始碼](#1-取得原始碼)
  - [安裝相依套件](#2-安裝相依套件)
  - [開啟專案](#3-開啟專案)
  - [設定開發環境](#4-設定開發環境)
  - [編譯與執行](#5-編譯與執行)
- [設定說明](#設定說明)
  - [必要設定項目](#必要設定項目)
  - [URL 設定](#url-設定)
  - [客服資訊設定](#客服資訊設定)
- [預編譯框架說明](#預編譯框架說明)
- [開發指南](#開發指南)
  - [程式碼風格規範](#程式碼風格規範)
  - [提交程式碼前檢查](#提交程式碼前檢查)
- [疑難排解](#疑難排解)
- [安全性注意事項](#安全性注意事項)
- [授權條款](#授權條款)
- [支援與聯絡](#支援與聯絡)
- [致謝](#致謝)

---

## 專案簡介

本專案為**數位憑證皮夾 iOS 持有端應用程式**，提供使用者在 iOS 平台上安全且便利的數位憑證管理解決方案。本應用程式整合數位憑證核心 SDK，讓使用者能夠安全地持有、管理和出示各類數位憑證，支援可驗證憑證（Verifiable Credential, VC）的加入與驗證憑證（Verifiable Presentation, VP）的出示功能。透過直覺的操作介面與強大的安全機制，提供使用者完整的數位身分管理體驗。

---

## 主要功能

### 憑證管理
- **加入憑證**（Verifiable Credential, VC）
  - QR Code 掃描加入憑證
  - 搜尋可申請憑證
  - 支援多種數位憑證格式

- **管理憑證**
  - 憑證排序
  - 憑證歷史記錄
  - 查看憑證資訊

- **出示憑證**（Verifiable Presentation, VP）
  - 憑證驗證與展示
  - QR Code 動態展示
  - 支援憑證選擇性揭露
  - 確保憑證真實性驗證

### 身分驗證
- **生物辨識驗證**
  - Face ID 支援
  - Touch ID 支援
  - PIN 碼驗證機制

### 憑證狀態管理
- 憑證有效性即時驗證
- 到期提醒通知（1天/7天）
- 自動更新憑證狀態

### 操作記錄
- 授權記錄查詢
- 憑證異動記錄
- 操作歷程追蹤

---

## 技術架構

### 核心技術

| 技術 | 版本 | 說明 |
|------|------|------|
| iOS | 13.0+ | 最低支援版本 |
| Swift | 5.9.2 | 程式語言 |
| Xcode | 16.2+ | 開發工具 |
| CocoaPods | 1.16.2 | 相依套件管理工具 |
| SQLite | - | 本地資料庫 |
| SQLCipher | 0.15.3 | 加密資料庫 |

### 主要相依套件

| 套件名稱 | 版本 | 用途 |
|----------|------|------|
| IQKeyboardManager | 最新版 | 自動管理鍵盤顯示/隱藏，避免輸入欄位被遮擋 |
| Swinject | 最新版 | 相依注入容器，實現 IoC 設計模式 |
| SQLite.swift/SQLCipher | ~> 0.15.3 | 加密的 SQLite 資料庫，儲存敏感資料 |
| lottie-ios | 最新版 | Lottie 動畫播放器，支援 JSON 格式動畫 |
| ZIPFoundation | ~> 0.9 | ZIP 檔案壓縮與解壓縮功能 |

> 詳細的第三方套件授權資訊請參閱 [THIRD_PARTY_LICENSES.md](THIRD_PARTY_LICENSES.md)

### 專案結構

本專案採用 **MVVM (Model-View-ViewModel)** 架構模式，結合 **Repository Pattern** 進行資料存取。

```
DigitalWallet/
├── UI/Page/              # 視圖層 (ViewControllers)
│   ├── ShowCertificates/    # 憑證展示頁面
│   ├── AddCertificate/      # 加入憑證頁面
│   ├── CardOverview/        # 憑證總覽頁面
│   ├── Scan/                # QR Code 掃描頁面
│   ├── VerifiablePresentation/  # VP 出示頁面
│   ├── Setting/             # 設定頁面
│   ├── Login/               # 登入頁面
│   └── Common/              # 共用元件
├── Core/                 # 核心邏輯層
│   ├── Manager/             # 業務邏輯管理器
│   │   ├── VerifiableManager.swift
│   │   ├── BiometricVerifyManager.swift
│   │   ├── CryptoManager.swift
│   │   └── SQLiteManager.swift
│   └── Repository/          # 資料存取層
│       ├── UserRepository.swift
│       ├── LoginRepository.swift
│       └── VerifiablePresentationRepository.swift
├── Data/                 # 資料層
│   ├── Model/               # 資料模型
│   └── Enum/                # 列舉定義
├── Common/               # 共用元件與工具類別
│   ├── Config.swift         # 設定檔
│   ├── CountdownTimer.swift
│   └── SearchTextField.swift
├── Extension/            # Swift 型別擴充功能
├── Resources/            # 資源檔案
│   ├── Framework/           # 預編譯框架
│   └── Html/                # HTML 資源
├── Assets.xcassets/      # 圖片資源目錄
└── zh-Hant.lproj/        # 繁體中文本地化檔案
```

---

## 系統需求

### 開發環境需求

- **作業系統**: macOS Sonoma 14.5 或以上版本
- **Xcode**: 16.2 或以上版本
- **Swift**: 5.9.2
- **CocoaPods**: 1.16.2
- **Apple 開發者帳號**: 用於程式碼簽署與裝置測試

### 執行環境需求

- **iOS 裝置**: iOS 13.0 或以上版本
- **生物辨識**: 支援 Face ID 或 Touch ID 的裝置（建議）
- **相機**: 用於 QR Code 掃描功能
- **網路連線**: 用於憑證同步與驗證

> ⚠️ **重要**: 由於本應用程式使用 Face ID/Touch ID 與 MODA SDK 功能，因此**僅能在實體裝置上執行**，模擬器無法支援這些功能。

---

## 快速開始

### 1. 取得原始碼

```bash
git clone https://github.com/moda-gov-tw/TWDIW-official-app.git
cd TWDIW-official-app/APP/moda-digitalwallet-holder-iosapp
```

### 2. 安裝相依套件

本專案使用 CocoaPods 管理相依套件。

#### 2.1 安裝 Ruby（使用 Homebrew）

由於 macOS 系統內建的 Ruby 版本較舊且權限管理較嚴格，建議使用 Homebrew 安裝最新版本的 Ruby：

```bash
# 安裝 Ruby
brew install ruby

# 將 Homebrew 的 Ruby 加入環境變數（以 zsh 為例）
echo 'export PATH="/opt/homebrew/opt/ruby/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc

# 驗證 Ruby 版本（應顯示 3.x 版本）
ruby --version
```

#### 2.2 安裝 CocoaPods

使用 Homebrew 安裝的 Ruby 後，可直接安裝 CocoaPods（不需要使用 sudo）：

```bash
# 安裝 CocoaPods
gem install cocoapods

# 驗證安裝
pod --version
```

#### 2.3 安裝專案相依套件

進入專案目錄並安裝相依套件：

```bash
cd DigitalWallet
pod install
```

安裝完成後，會產生 `DigitalWallet.xcworkspace` 檔案。

> ⚠️ **注意**: 若您之前已使用系統 Ruby 安裝過 CocoaPods，建議移除後重新安裝：
> ```bash
> sudo gem uninstall cocoapods
> gem install cocoapods
> ```

### 3. 開啟專案

```bash
open DigitalWallet.xcworkspace
```

> ⚠️ **重要**: 請務必使用 `.xcworkspace` 而非 `.xcodeproj` 開啟專案，否則 CocoaPods 管理的相依套件將無法正確載入。

### 4. 設定開發環境

在編譯專案之前，您需要進行以下設定：

#### 4.1 設定開發團隊 (Development Team)

1. 在 Xcode 中選擇專案 → Targets → `DigitalWallet-PROD`
2. 選擇 **Signing & Capabilities** 頁籤
3. 在 **Team** 下拉選單中選擇您的 Apple 開發者帳號或個人團隊

#### 4.2 修改 Bundle Identifier

1. 在 **Signing & Capabilities** 頁籤中
2. 將 **Bundle Identifier** 從 `com.example.digitalwallet` 改為您自己的唯一識別碼
   - 格式範例：`com.yourcompany.digitalwallet`
   - 確保此識別碼在 App Store 中是唯一的

#### 4.3 更新設定檔 URL

編輯 `DigitalWallet/Common/Config.swift`，將範例 URL 替換為實際使用的後端服務網址：

```swift
// 範例網址，請替換為實際使用的網址
static let CORPORTE_WEBSITE_URL = "https://example.com"  // 替換為實際網址
static let CORPORTE_WEBSITE_QA_URL = "https://example.com/qa.html"
static let QUESTION_REPORT_URL = "https://example.com/feedback"

static var FRONTEND_URL: String {
    switch Config.environmentType {
    case .PROD:
        return "https://example.com/frontend"  // 替換為實際網址
    }
}
```

### 5. 編譯與執行

1. 將您的 iOS 實體裝置連接至電腦
2. 在 Xcode 中選擇您的裝置作為執行目標
3. 點擊 Run 按鈕（⌘R）進行編譯與安裝

> ⚠️ **注意**: 首次在裝置上執行時，可能需要在裝置的 **設定 > 一般 > VPN 與裝置管理** 中信任開發者憑證。

---

## 設定說明

### 必要設定項目

在部署應用程式到生產環境之前，請確認以下設定項目已正確設定：

| 設定項目 | 檔案位置 | 說明 |
|---------|---------|------|
| Development Team | Xcode 專案設定 | 設定為您的 Apple 開發者帳號 |
| Bundle Identifier | Xcode 專案設定 | 設定為唯一的應用程式識別碼 |
| 網站 URL | `Common/Config.swift` | 設定為實際的服務網址 |
| 前端 URL | `Common/Config.swift` | 設定為實際的前端服務網址 |
| 客服資訊 | `ContactCustomerServiceViewController.swift` | 設定為實際的客服聯絡資訊 |

### URL 設定

編輯 `DigitalWallet/Common/Config.swift`：

```swift
// 官方網站
static let CORPORTE_WEBSITE_URL = "https://your-actual-domain.com"

// 常見問題頁面
static let CORPORTE_WEBSITE_QA_URL = "https://your-actual-domain.com/qa.html"

// 問題回報網址
static let QUESTION_REPORT_URL = "https://your-actual-domain.com/feedback"

// 前端服務網址
static var FRONTEND_URL: String {
    switch Config.environmentType {
    case .PROD:
        return "https://your-actual-frontend.com"
    }
}
```

### 客服資訊設定

編輯 `UI/Page/Setting/ContactCustomerService/ContactCustomerServiceViewController.swift`：

```swift
// 設定實際的客服電話
phoneContentLabel.text = NSLocalizedString("CustomerPhoneContent", comment: "您的客服電話")

// 設定實際的服務時間
timeContentLabel.text = NSLocalizedString("CustomerTimeContent", comment: "您的服務時間")

// 設定實際的客服信箱
composeVC.setToRecipients(["your-support@example.com"])
```

---

## 預編譯框架說明

專案包含以下預編譯的框架（Framework），位於 `DigitalWallet/Resources/Framework/` 目錄：

| 框架名稱 | 用途 | 說明 |
|---------|------|------|
| `moda_digitalwallet_holder_sdk.framework` | 數位憑證核心 SDK | 提供數位憑證相關的核心功能 |
| `App.xcframework` | Flutter 應用框架 | Flutter 相關的應用程式框架 |
| `Flutter.xcframework` | Flutter 引擎 | Flutter 框架的核心引擎 |

> 📝 **注意**: 這些為已編譯的二進位檔案。如需原始碼或最新版本，請參考 `DigitalWallet/Resources/Framework/README.md`

---

## 開發指南

### 程式碼風格規範

- 遵循 [Swift API Design Guidelines](https://swift.org/documentation/api-design-guidelines/)
- 使用有意義的變數與方法命名
- 函式與類別應包含適當的註解說明
- 使用 MVVM 架構模式組織程式碼
- 保持程式碼簡潔，避免過度設計

### 提交程式碼前檢查

在提交 Pull Request 之前，請確保：

1. 已詳閱並遵守[專案貢獻指南](../../CONTRIBUTING.md)與[行為準則](../../CODE_OF_CONDUCT.md)
2. 程式碼遵循專案的編碼風格
3. 新增的功能包含適當的註解
4. 編譯通過且沒有警告訊息
5. 在實體裝置上測試通過
6. 提交訊息清楚描述變更內容
7. 沒有提交敏感資訊（API Keys、密碼等）

---

## 疑難排解

### CocoaPods 安裝問題

#### 問題 1：執行 `sudo gem install cocoapods` 失敗

**現象**：
- 使用 `sudo gem install cocoapods` 安裝時出現權限錯誤或安裝失敗
- macOS Sequoia 或更新版本的系統內建 Ruby 版本過舊（2.6.x）

**原因**：
macOS 系統內建的 Ruby 版本較舊，且在新版 macOS 中權限管理更加嚴格，不建議使用系統 Ruby 安裝 gem 套件。

**解決方法**：
請按照「[2. 安裝相依套件](#2-安裝相依套件)」章節的說明，使用 Homebrew 安裝最新版本的 Ruby，再安裝 CocoaPods。

#### 問題 2：安裝 CocoaPods 後執行 `pod --version` 出現 `command not found`

**現象**：
```bash
$ pod --version
zsh: command not found: pod
```

**原因**：
1. 環境變數 PATH 沒有包含 Homebrew Ruby 的路徑
2. 環境變數 PATH 沒有包含 gem 可執行檔的路徑

**檢查步驟**：
```bash
# 檢查目前使用的 Ruby 版本和路徑
which ruby
ruby --version

# 檢查 gem 環境資訊
gem environment
```

**解決方法**：

1. 確保 `~/.zshrc` 包含以下兩行設定：

```bash
# Homebrew Ruby 路徑
export PATH="/opt/homebrew/opt/ruby/bin:$PATH"

# Gem 可執行檔路徑
export PATH="/opt/homebrew/lib/ruby/gems/3.4.0/bin:$PATH"
```

2. 重新載入環境變數：

```bash
source ~/.zshrc
```

3. 驗證設定：

```bash
# 應該顯示 Homebrew 的 Ruby（不是 /usr/bin/ruby）
which ruby

# 應該顯示 Ruby 3.x 版本
ruby --version

# 應該顯示 CocoaPods 版本（例如：1.16.2）
pod --version
```

> 💡 **提示**：如果 gem 版本更新導致路徑改變（例如從 3.4.0 變成 3.5.0），需要同步更新 `~/.zshrc` 中的路徑。可以使用 `gem environment` 查看 `EXECUTABLE DIRECTORY` 的實際路徑。

#### 問題 3：已經設定 PATH 但終端機仍找不到 pod 指令

**原因**：
終端機視窗在設定 PATH 之前就已經開啟，尚未載入新的環境變數。

**解決方法**：
1. 關閉目前的終端機視窗，重新開啟新的終端機視窗
2. 或者在目前的終端機執行 `source ~/.zshrc` 重新載入設定

### 編譯錯誤：找不到 Framework

**問題**: 編譯時出現 "Framework not found" 錯誤

**解決方法**:
```bash
cd DigitalWallet
pod deintegrate
pod install
```

### CocoaPods 版本問題

**問題**: CocoaPods 版本過舊導致相依套件無法正確安裝

**解決方法**:
```bash
# 使用 Homebrew 的 Ruby，不需要 sudo
gem update cocoapods
pod setup
pod install
```

### 程式碼簽署錯誤

**問題**: 無法在裝置上執行應用程式

**解決方法**:
1. 確認已在 Xcode 中登入 Apple ID
2. 檢查 Development Team 設定是否正確
3. 確認 Bundle Identifier 未與其他應用程式衝突
4. 檢查裝置是否在開發者帳號的授權裝置清單中

### 模擬器無法執行

**問題**: 在模擬器上執行時發生錯誤

**說明**: 本應用程式使用 Face ID/Touch ID 與 MODA SDK，這些功能僅支援實體裝置，模擬器無法執行。

**解決方法**: 請使用實體 iOS 裝置進行測試。

### SQLCipher 相依套件錯誤

**問題**: 編譯時出現 SQLCipher 相關錯誤

**解決方法**:
```bash
cd DigitalWallet
pod cache clean SQLCipher
pod install
```

---

## 安全性注意事項

⚠️ **重要安全提醒**：

### 開發階段
1. **不要提交敏感資訊**: 請勿將 API Keys、Tokens、密碼等敏感資訊提交到版本控制系統
2. **使用環境變數**: 敏感設定應透過環境變數或安全的設定檔管理
3. **程式碼審查**: 提交前確認程式碼中沒有硬編碼的敏感資訊

### 生產環境
1. **保護使用者隱私**: 確保妥善處理使用者的個人資料與憑證資訊
2. **加密敏感資料**: 本機儲存的敏感資料應使用 SQLCipher 加密
3. **定期更新相依套件**: 定期檢查並更新第三方套件以修補已知漏洞
4. **啟用 App Transport Security**: 確保所有網路連線使用 HTTPS

### 安全漏洞回報
如發現安全漏洞，請**不要**公開發布。請透過私密方式聯絡專案維護團隊。

---

## 授權條款

本專案採用 **MIT License** 授權。詳細授權條款請參考[專案根目錄的 LICENSE.txt](../../LICENSE.txt) 檔案。

### 第三方套件授權

本專案使用了多個開源套件，其授權資訊請參考 [THIRD_PARTY_LICENSES.md](THIRD_PARTY_LICENSES.md)。

所有使用的第三方套件授權（MIT、BSD-3-Clause、Apache 2.0）均與本專案 MIT 授權相容。

---

## 支援與聯絡

如有問題、建議或需要協助，請透過以下方式聯絡：

- **問題回報**: 在 [TWDIW-official-app GitHub Repository](https://github.com/moda-gov-tw/TWDIW-official-app/issues) 中提交 Issue
- **功能建議**: 透過 [GitHub Discussions](https://github.com/moda-gov-tw/TWDIW-official-app/discussions) 進行討論
- **技術支援**: 請參考[疑難排解](#疑難排解)章節

> 📝 **注意**: 請在提交問題前先搜尋現有的 Issues，避免重複提問。

---

## 致謝

本專案使用以下優秀的開源專案：

- [IQKeyboardManager](https://github.com/hackiftekhar/IQKeyboardManager) - iOS 鍵盤管理工具
- [Swinject](https://github.com/Swinject/Swinject) - Swift 相依注入框架
- [SQLite.swift](https://github.com/stephencelis/SQLite.swift) - Swift 的 SQLite 包裝器
- [SQLCipher](https://www.zetetic.net/sqlcipher/) - 加密的 SQLite 資料庫
- [lottie-ios](https://github.com/airbnb/lottie-ios) - Lottie 動畫播放器
- [ZIPFoundation](https://github.com/weichsel/ZIPFoundation) - ZIP 檔案處理框架

感謝所有開源社群的貢獻者！

---

**最後更新日期**: 2025-11-27
