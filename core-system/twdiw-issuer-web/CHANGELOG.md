# 變更日誌

本文件記錄專案的所有重要變更。

格式基於 [Keep a Changelog](https://keepachangelog.com/zh-TW/1.0.0/)，
版本編號遵循 [Semantic Versioning](https://semver.org/lang/zh-TW/)。

## [未發布]

### 新增

- 無

### 變更

- 無

### 修正

- 無

### 移除

- 無

---

## [0.0.1] - 2025-11-27

### 首次公開發布

這是數位發展部數位憑證皮夾發行端前端管理介面的首次公開發布版本。本專案提供憑證發行機關完整的數位憑證管理功能，包含 DID 註冊、VC 憑證範本建立與管理、VC 資料狀態控制等核心功能。

#### 核心功能

**DID 管理**

- DID 註冊與設定功能
- 組織標誌（Logo）上傳與管理

**VC 憑證範本管理**

- VC Schema 建立與編輯
- 欄位定義與驗證規則設定
- 正規表示式管理
- 範本啟用/停用控制

**VC 資料狀態管理**

- VC 撤銷（Revoke）功能
- VC 停用（Suspend）功能
- VC 復用（Resume）功能
- VC 狀態查詢與檢視

**系統管理**

- 帳號管理與權限控制
- 角色與功能管理
- 組織資訊管理
- Access Token 管理
- 稽核追蹤記錄
- 資料清理排程設定

**安全性功能**

- 使用者登入與 OTP 驗證
- 密碼加密傳輸（RSA）
- JWT Token 自動更新
- 完整的權限控制機制

#### 技術架構

- **前端框架**：Vue.js 3.4.18（Composition API）
- **UI 框架**：Quasar Framework 2.8.0
- **狀態管理**：Pinia 2.0.11
- **路由管理**：Vue Router 4.0.12
- **建置工具**：Vite 2.9.18
- **HTTP 客戶端**：Axios 1.12.2
- **國際化**：Vue I18n 10.0.6

#### 專案文件

- 完整的 README.md 專案說明文件
- 詳細的技術架構說明
- 快速開始指南
- 設定說明與範例
- Docker 部署指南
- CHANGELOG.md 變更日誌

#### 已知限制

- 目前僅支援繁體中文介面
- 需要連接後端 API 服務才能完整運作
- 開發環境需要手動設定 Proxy 連線後端

---

## 版本說明

### 版本編號規則

專案採用語意化版本（Semantic Versioning）：`主版號.次版號.修訂號`

- **主版號（Major）**：不相容的 API 變更
- **次版號（Minor）**：向下相容的功能新增
- **修訂號（Patch）**：向下相容的問題修正

### 變更類型

- **新增（Added）**：新功能
- **變更（Changed）**：既有功能的變更
- **已棄用（Deprecated）**：即將移除的功能
- **移除（Removed）**：已移除的功能
- **修正（Fixed）**：錯誤修正
- **安全性（Security）**：安全性相關的變更

---

[未發布]: https://github.com/moda-gov-tw/TWDIW-official-app/compare/v0.0.1...HEAD
[0.0.1]: https://github.com/moda-gov-tw/TWDIW-official-app/releases/tag/v0.0.1
