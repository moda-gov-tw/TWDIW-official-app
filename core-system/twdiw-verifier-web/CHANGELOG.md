# 變更日誌

本文件記錄專案的所有重要變更。

本專案的版本編號遵循 [語意化版本 2.0.0](https://semver.org/lang/zh-TW/)。

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

### 新增

- 初始開源版本發布
- **DID 註冊管理**：提供去中心化識別碼（Decentralized Identifier）的註冊功能
- **VP 模板管理**：
  - 建立可驗證表述（Verifiable Presentation）模板
  - 編輯現有 VP 模板
  - 刪除不需要的 VP 模板
  - 匯入 VC（Verifiable Credential）憑證定義
  - 自訂欄位支援
- **組織設定**：
  - 組織 LOGO 上傳與管理
  - 組織金鑰設定與管理
  - 組織資訊設定
- **帳號權限管理**：
  - 使用者帳號管理
  - 角色權限設定
  - 功能權限控制
- **稽核追蹤**：完整的操作記錄與變更追蹤
- **Docker 部署支援**：提供完整的 Docker 部署方案
- **完整的專案文件**：
  - 詳細的 README.md 說明文件
  - 環境設定指南
  - 開發指南與規範
  - API 整合說明

### 技術特色

- 採用 Vue.js 3.4.18 與 Composition API
- 使用 Quasar Framework 2.8.0 企業級 UI 元件庫
- 整合 Pinia 狀態管理
- 支援多語系（繁體中文、英文）
- 完整的安全性防護（XSS、CSRF）
- 響應式設計，支援桌面與行動裝置

---

## 變更類型說明

- **新增**：新功能
- **變更**：現有功能的變更
- **棄用**：即將移除的功能
- **移除**：已移除的功能
- **修正**：錯誤修正
- **安全性**：安全性相關修正

---

## 版本編號說明

本專案採用[語意化版本](https://semver.org/lang/zh-TW/)編號規則：

- **主版本號（MAJOR）**：進行不相容的 API 變更時
- **次版本號（MINOR）**：以向下相容的方式新增功能時
- **修訂號（PATCH）**：進行向下相容的錯誤修正時

範例：`1.2.3` 表示主版本 1、次版本 2、修訂版本 3

---

## 如何貢獻

若您發現任何問題或有功能建議，歡迎透過以下方式聯絡我們：

- **問題回報**：[GitHub Issues](https://github.com/moda-gov-tw/TWDIW-official-app/issues)
- **技術支援信箱**：service@wallet.gov.tw
- **官方網站**：[https://wallet.gov.tw](https://wallet.gov.tw)

---

**備註**：本變更日誌遵循 [Keep a Changelog](https://keepachangelog.com/zh-TW/1.0.0/) 格式規範。
