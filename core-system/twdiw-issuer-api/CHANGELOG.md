# 變更日誌

本專案的所有重大變更都將記錄在此檔案中。

本變更日誌格式基於 [Keep a Changelog](https://keepachangelog.com/zh-TW/1.0.0/)，
並且本專案遵循[語義化版本](https://semver.org/lang/zh-TW/)。

## [1.0.0] - 2024-11-27

### 🎉 首次發布

這是數位發展部數位憑證皮夾發行端管理服務的首次開源發布版本。本專案提供完整的數位憑證發行管理功能，支援 DID 註冊、VC 模板管理、憑證狀態管理及 API 介接服務。

### 新增

#### 核心功能
- **DID 管理功能**
  - DID 註冊與驗證
  - DID 文件管理
  - DID 解析服務

- **VC 模板管理功能**
  - 憑證模板建立與編輯
  - 模板欄位定義與驗證
  - 模板版本控制
  - 模板啟用與停用

- **VC 資料狀態管理功能**
  - 憑證發行記錄管理
  - 憑證撤銷功能
  - 憑證停用與復用
  - 憑證狀態查詢

- **API 介接服務**
  - RESTful API 端點
  - 憑證發行 API（501i）
  - 憑證序號管理 API（502i, 503i）
  - 功能開關 API（504i）
  - Nonce 管理 API（402i）
  - DID 管理 API（101i）

- **組織管理功能**
  - 發行機關註冊與管理
  - 組織資料維護
  - 組織權限設定

- **使用者與權限管理**
  - 使用者帳號管理
  - 角色與權限控管
  - 細緻的按鈕層級權限控制
  - JWT 認證機制

- **API 追蹤與稽核**
  - 完整的 API 呼叫記錄
  - 請求與回應內容記錄
  - 操作時間戳記
  - 錯誤記錄與追蹤

#### 安全性功能
- JWT Token 認證與授權
- RSA 非對稱式加密
- 屬性加密保護敏感資料
- CORS 跨域存取控制
- Cookie 安全設定（HttpOnly、SameSite、Secure）
- 輸入驗證與 XSS 防護
- SQL Injection 防護

#### 系統功能
- 資料庫遷移（Liquibase）
- 應用程式健康檢查
- 效能監控（Prometheus）
- 日誌管理與等級控制
- 排程任務管理（Quartz、Spring Schedule）
- 國際化支援（繁體中文、英文）

#### 整合功能
- VC 服務整合
- OID4VCI 服務整合
- 前端服務整合
- IVPAS 服務整合

### 技術架構

#### 核心技術棧
- Java 17 (Eclipse Temurin)
- Spring Boot 3.3.13
- JHipster 8.6
- PostgreSQL 16
- Maven 3.5+

#### 主要相依套件
- Nimbus JOSE+JWT 9.40 - JWT 與 JOSE 處理
- ZXing 3.5.3 - QR Code 產生
- Apache HttpClient 5.3.1 - HTTP 客戶端
- Liquibase - 資料庫版本控制
- MapStruct - 物件映射
- Spring Security - 安全性框架
- Quartz Scheduler - 排程任務管理

### 安全性

#### 已實作的安全措施
- 移除所有硬編碼的敏感資訊（密鑰、密碼）
- 強制使用環境變數管理敏感設定
- 加強 .gitignore 規則，防止機密檔案提交
- 實作完整的輸入驗證機制
- 實作 XSS 和 SQL Injection 防護
- Cookie 安全性設定（HttpOnly、SameSite、Secure）

#### 安全性最佳實踐
- JWT Token 有效期限控制（預設 1200 秒）
- 支援 Remember Me 機制（30 天）
- RSA 金鑰對保護
- 資料庫連線加密支援
- HTTPS 強制使用建議

### 文件

#### 已提供的文件
- 完整的 README.md 使用說明
- 環境變數設定指引
- 快速開始指南
- API 端點說明
- 資料庫遷移指南
- 專案結構說明
- 程式碼風格規範

### 開發工具

#### 支援的開發環境
- IntelliJ IDEA
- Eclipse
- Visual Studio Code
- Maven CLI
- Maven Wrapper

### 已知限制

- API 文件需要在 dev 環境下才能存取 Swagger UI
- 部分內部 API 端點需要額外的服務配合運行
- OTP 驗證功能預設為關閉狀態

---

## 版本說明

### 版本號格式

本專案採用[語義化版本](https://semver.org/lang/zh-TW/)規範：`主版本號.次版本號.修訂號`

- **主版本號**：進行不相容的 API 變更時
- **次版本號**：以向下相容的方式新增功能時
- **修訂號**：進行向下相容的問題修正時

### 標籤說明

- `新增 (Added)`: 新功能
- `變更 (Changed)`: 既有功能的變更
- `棄用 (Deprecated)`: 即將移除的功能
- `移除 (Removed)`: 已移除的功能
- `修正 (Fixed)`: 錯誤修正
- `安全性 (Security)`: 安全性相關的修正或改善

---

## 貢獻指南

如需回報問題或建議新功能，請至 [GitHub Issues](https://github.com/moda-gov-tw/TWDIW-official-app/issues) 提出。

## 授權

本專案採用 [MIT License](https://opensource.org/licenses/MIT) 授權。
