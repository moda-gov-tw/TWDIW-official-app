# 變更日誌

本檔案記錄了數位發展部數位憑證皮夾驗證端管理服務的所有重要變更。

本專案遵循[語義化版本](https://semver.org/lang/zh-TW/)，變更日誌格式基於 [Keep a Changelog](https://keepachangelog.com/zh-TW/1.0.0/)。

## [1.0.0] - 2025-11-27

### 首次開源發佈

此為數位發展部數位憑證皮夾驗證端管理服務的首次開源版本。

#### 新增功能

- **DID 註冊管理**
  - 支援驗證機關去中心化識別碼（DID）的註冊與管理
  - 提供 DID 文件的建立與查詢功能

- **VP 模板管理**
  - 可驗證表述（Verifiable Presentation）模板的建立
  - VP 模板的編輯與更新功能
  - VP 模板的查詢與刪除功能
  - 支援多種憑證類型的驗證模板設定

- **驗證授權流程**
  - 授權 QR Code 產生功能
  - 支援多種驗證場景的授權請求
  - 整合 OID4VP（OpenID for Verifiable Presentations）協議

- **驗證結果管理**
  - 驗證結果的即時查詢
  - 支援資料加密與解密
  - 提供金鑰設定與管理功能

- **API 追蹤與稽核**
  - 完整的 API 呼叫記錄
  - 稽核日誌功能
  - 支援追蹤查詢與分析

- **安全性功能**
  - JWT（JSON Web Token）身分驗證
  - 屬性加密保護
  - RSA 金鑰對支援
  - HTTPS 與 Cookie 安全設定

- **資料庫支援**
  - PostgreSQL 16 資料庫整合
  - Liquibase 資料庫版本控制
  - 自動資料庫遷移功能

#### 技術架構

- **核心框架**
  - Java 17 (Eclipse Temurin)
  - Spring Boot 3.3.13
  - JHipster 8.6
  - Maven 3.5+

- **主要相依套件**
  - Nimbus JOSE+JWT 9.40
  - ZXing 3.5.3
  - Apache HttpClient 5.3.1
  - Liquibase
  - MapStruct

#### 文件與說明

- 完整的 README.md 使用說明
- 環境變數設定指南
- API 端點文件
- 資料庫遷移指南
- 專案結構說明

---

## 版本說明

### 版本編號規則

本專案採用[語義化版本 2.0.0](https://semver.org/lang/zh-TW/) 規範：

- **主版本號（MAJOR）**：進行不相容的 API 變更時
- **次版本號（MINOR）**：新增向下相容的功能時
- **修訂號（PATCH）**：進行向下相容的問題修正時

### 變更類型

- **Added**：新增功能
- **Changed**：既有功能的變更
- **Deprecated**：即將移除的功能
- **Removed**：已移除的功能
- **Fixed**：問題修正
- **Security**：安全性修正

---

[1.0.0]: https://github.com/moda-gov-tw/TWDIW-official-app/releases/tag/verifier-api-v1.0.0
