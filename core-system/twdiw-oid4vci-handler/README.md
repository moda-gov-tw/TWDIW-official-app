# 數位發展部數位憑證皮夾 OID4VCI 發行端服務

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.13-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![JHipster](https://img.shields.io/badge/JHipster-8.6-orange.svg)](https://www.jhipster.tech/)

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
  - [設定環境變數](#2-設定環境變數)
  - [編譯專案](#3-編譯專案)
  - [執行應用程式](#4-執行應用程式)
  - [驗證服務運行](#5-驗證服務運行)
- [設定說明](#設定說明)
  - [主要設定檔](#主要設定檔)
  - [關鍵設定項目](#關鍵設定項目)
- [API 文件](#api-文件)
  - [主要端點](#主要端點)
- [資料庫遷移](#資料庫遷移)
  - [資料庫變更日誌位置](#資料庫變更日誌位置)
  - [執行資料庫遷移](#執行資料庫遷移)
  - [查看待執行的變更](#查看待執行的變更)
  - [回滾資料庫變更](#回滾資料庫變更)
- [開發指南](#開發指南)
  - [專案結構](#專案結構)
  - [程式碼風格規範](#程式碼風格規範)
- [支援與聯絡](#支援與聯絡)
- [變更日誌](#變更日誌)
- [致謝](#致謝)

### 專案簡介

本專案為數位發展部數位憑證皮夾之 **OID4VCI（OpenID for Verifiable Credential Issuance）發行端服務**，提供符合 OpenID for Verifiable Credentials 國際標準的數位憑證發放服務。此服務部署於憑證發行機關的環境中，負責處理數位憑證的發放申請、驗證與核發流程。

### 主要功能

- **OpenID for Verifiable Credential Issuance (OID4VCI)** 協議支援
- **QR Code 產生**：產生憑證申請用的 QR Code
- **安全性**：實作完整的金鑰持有證明（Proof of Possession）機制
- **API 追蹤**：完整的 API 呼叫追蹤與稽核功能

### 技術架構

#### 核心技術

| 技術 | 版本 | 說明 |
|------|------|------|
| Java | 17 (Eclipse Temurin) | 執行環境 |
| Spring Boot | 3.3.13 | 應用程式框架 |
| JHipster | 8.6 | 開發平台 |
| Maven | 3.5+ | 專案建構工具 |
| PostgreSQL | 16 | 資料庫系統 |

#### 主要相依套件

| 套件名稱 | 版本 | 用途 |
|----------|------|------|
| Nimbus JOSE+JWT | 9.40 | JWT 與 JOSE 處理 |
| OAuth2 OIDC SDK | 11.19 | OpenID Connect 實作 |
| ZXing | 3.5.3 | QR Code 產生 |
| BitcoinJ Core | 0.17 | Base58 編碼 |
| Apache HttpClient 5 | 5.3.1 | HTTP 客戶端 |
| Liquibase | - | 資料庫版本控制 |
| MapStruct | - | 物件映射 |

### 系統需求

#### 開發環境需求

- **Java Development Kit (JDK)**: OpenJDK 17 或更高版本（建議使用 Eclipse Temurin）
- **Apache Maven**: 3.5 或更高版本
- **PostgreSQL**: 16 或更高版本
- **作業系統**: Windows、macOS 或 Linux

#### 執行環境需求

- **Java Runtime Environment (JRE)**: OpenJDK 17 或更高版本
- **記憶體**: 建議至少 2GB RAM
- **磁碟空間**: 建議至少 1GB 可用空間

### 快速開始

#### 1. 取得原始碼

```bash
git clone https://github.com/moda-gov-tw/TWDIW-official-app.git
cd TWDIW-official-app/core-system/twdiw-oid4vci-handler
```

#### 2. 設定環境變數

在執行應用程式前，請設定以下必要的環境變數：

| 環境變數名稱 | 說明 | 範例 |
|-------------|------|------|
| `SPRING_DATASOURCE_URL` | 資料庫連線 URL | `jdbc:postgresql://localhost:5432/modadw_issuer?currentSchema=vc` |
| `SPRING_DATASOURCE_USERNAME` | 資料庫使用者名稱 | `modadw_issuer` |
| `SPRING_DATASOURCE_PASSWORD` | 資料庫密碼 | `your_password` |
| `JHIPSTER_SECURITY_AUTHENTICATION_JWT_BASE64_SECRET` | JWT 簽章密鑰（Base64 編碼，至少 256 bits） | 使用 `openssl rand -base64 64` 產生 |
| `ATTRIBUTEENCRYPTOR_SECRET` | 屬性加密金鑰（Base64 編碼後解碼為 32 bytes/256-bit，必填） | `$(openssl rand -base64 32)` |
| `AP_ACCOUNT_USERNAME` | AP 帳號使用者名稱 | `your-ap-username` |
| `AP_ACCOUNT_PASSWORD` | AP 帳號密碼（加密後） | `encrypted-password` |
| `VC_INTERNAL_URL` | VC 服務內部 URL | `http://localhost:8081` |

設定範例（Linux/macOS）：

```bash
export SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/modadw_issuer?currentSchema=vc"
export SPRING_DATASOURCE_USERNAME="modadw_issuer"
export SPRING_DATASOURCE_PASSWORD="your_password"
export JHIPSTER_SECURITY_AUTHENTICATION_JWT_BASE64_SECRET="$(openssl rand -base64 64)"
export ATTRIBUTEENCRYPTOR_SECRET="$(openssl rand -base64 32)"
export AP_ACCOUNT_USERNAME="your-ap-username"
export AP_ACCOUNT_PASSWORD="your-encrypted-password"
export VC_INTERNAL_URL="http://localhost:8081"
```

設定範例（Windows PowerShell）：

```powershell
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/modadw_issuer?currentSchema=vc"
$env:SPRING_DATASOURCE_USERNAME="modadw_issuer"
$env:SPRING_DATASOURCE_PASSWORD="your_password"
$env:JHIPSTER_SECURITY_AUTHENTICATION_JWT_BASE64_SECRET="your-jwt-base64-secret"
$env:ATTRIBUTEENCRYPTOR_SECRET="(Use openssl rand -base64 32)"
$env:AP_ACCOUNT_USERNAME="your-ap-username"
$env:AP_ACCOUNT_PASSWORD="your-encrypted-password"
$env:VC_INTERNAL_URL="http://localhost:8081"
```

#### 3. 編譯專案

##### 使用 Maven 編譯

```bash
# 清理並編譯專案（跳過測試）
mvn clean package -DskipTests

# 包含測試的完整編譯
mvn clean package
```

##### 使用 Maven Wrapper 編譯（無需安裝 Maven）

```bash
# Linux/macOS
chmod +x mvnw  
./mvnw clean package -DskipTests

# Windows
mvnw.cmd clean package -DskipTests
```

編譯成功後，會在 `target` 目錄下產生 WAR 檔案：
```
target/moda-digitalwallet-issuer-oidvci-service-0.0.1-SNAPSHOT.war
```

#### 4. 執行應用程式

##### 使用 Maven 執行

```bash
mvn spring-boot:run
```

#### 5. 驗證服務運行

服務啟動後，預設會在 `http://localhost:8080` 執行。您可以透過以下方式驗證：

```bash
# 檢查健康狀態
curl http://localhost:8080/management/health

# 查看應用程式資訊
curl http://localhost:8080/management/info
```

### 設定說明

#### 主要設定檔

專案使用 YAML 格式的設定檔，位於 `src/main/resources/config/` 目錄下：

- `application.yml`：基礎設定
- `application-dev.yml`：開發環境設定（預設啟用）

#### 關鍵設定項目

##### 應用程式設定

```yaml
spring:
  application:
    name: twdiwIssuerOidvciService
  profiles:
    active: dev  # 預設啟用 dev，其他 profile 依需求覆寫
```

##### 資料庫設定

```yaml
spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL}
    username: ${SPRING_DATASOURCE_USERNAME}
    password: ${SPRING_DATASOURCE_PASSWORD}
    type: com.zaxxer.hikari.HikariDataSource
    hikari:
      poolName: Hikari
      auto-commit: false
```

##### 伺服器設定

```yaml
server:
  port: 8080
  servlet:
    session:
      cookie:
        http-only: true
```

##### 安全性設定

```yaml
applications:
  config:
    attributeencryptor_secret: ${ATTRIBUTEENCRYPTOR_SECRET}

jhipster:
  security:
    authentication:
      jwt:
        base64-secret: ${JHIPSTER_SECURITY_AUTHENTICATION_JWT_BASE64_SECRET}
        token-validity-in-seconds: 86400
```

##### 發行者設定

```yaml
issuer:
  host:
    name: ${ISSUER_HOST_NAME:localhost}

vc:
  internal:
    url: ${VC_INTERNAL_URL:http://localhost:8081}

```

##### 初始化 AttributeEncryptor 秘鑰

- 此值為必填，需為 256-bit（32 bytes）並以 Base64 編碼，缺失會導致應用啟動失敗。
- 產生方式範例：

```bash
openssl rand -base64 32
# 或
head -c 32 /dev/urandom | base64
```

- 設定方式：
  - 建議使用環境變數：`export ATTRIBUTEENCRYPTOR_SECRET="$(openssl rand -base64 32)"`，部署時透過 Secret/KMS 注入。
  - 或在 `application.yml` 的 `applications.config.attributeencryptor_secret` 以外部化設定提供，但請勿將實際值提交至版本控制。

##### 初始化 Credential Issuer Config（db_secret/db_iv 與端點）

- `src/main/resources/config/liquibase/data/credential_issuer_config.csv` 目前全部是示範值，請勿直接使用。上線前請改成正式環境的實際值：
  - 服務網址：`credential_issuer`、`credential_endpoint`、`credential_url`
  - App Scheme：`app_url_scheme`
  - 金鑰：`db_secret`、`db_iv`
- 產生密鑰：

```bash
# 生成 256-bit AES 金鑰（Base64），對應 db_secret
openssl rand -base64 32

# 生成 96-bit GCM IV（Base64），對應 db_iv，需保持 12 bytes 長度
openssl rand -base64 12
```

- 設定建議：
  - 不要將實際值留在 CSV 或提交到版控；部署後以 DB migration、環境變數或運維腳本覆寫占位資料。
  - 端點（credential_issuer/credential_endpoint/credential_url）需替換為實際網域；`app_url_scheme` 也應改成實際行動端 Scheme。
```

### API 文件

#### 主要端點

##### 憑證發行 API

```
POST /api/issuer/{id}/qr-code
```

產生憑證申請用的 QR Code。

**請求範例：**

```bash
curl --location 'http://localhost:8080/api/issuer/qr-code' \
--header 'Content-Type: application/json' \
--data '{
  "authenticated": true,
  "id_token": "<YOUR_ID_TOKEN>"
}'
```

##### 管理端點

所有管理端點位於 `/management` 路徑下：

| 端點 | 說明 |
|------|------|
| `/management/health` | 健康檢查 |
| `/management/info` | 應用程式資訊 |
| `/management/metrics` | 效能指標 |
| `/management/prometheus` | Prometheus 格式指標 |
| `/management/loggers` | 日誌層級管理 |
| `/management/liquibase` | 資料庫遷移狀態 |

### 資料庫遷移

本專案使用 Liquibase 進行資料庫版本控制。

#### 資料庫變更日誌位置

```
src/main/resources/config/liquibase/
├── master.xml
└── changelog/
    ├── 20000000000001_oid4vci_initial_data.xml
    ├── 20250801_add_column.xml
    ├── 20250814_modify_table.xml
    └── 20250819_modify_table_download_ver.xml
```

#### 執行資料庫遷移

應用程式啟動時會自動執行資料庫遷移。若需手動執行：

```bash
mvn liquibase:update
```

#### 查看待執行的變更

```bash
mvn liquibase:status
```

#### 回滾資料庫變更

```bash
# 回滾最近一次變更
mvn liquibase:rollback -Dliquibase.rollbackCount=1
```

### 開發指南

#### 專案結構

```
src/
├── main/
│   ├── java/gov/moda/dw/issuer/oidvci/
│   │   ├── config/          # Spring 設定類別
│   │   ├── domain/          # JPA 實體類別
│   │   ├── repository/      # 資料存取層
│   │   ├── service/         # 業務邏輯層
│   │   ├── security/        # 安全性相關
│   │   ├── web/rest/        # REST API 控制器
│   │   ├── common/          # 共用工具類別
│   │   └── aop/             # AOP 切面
│   └── resources/
│       ├── config/          # 設定檔
│       │   ├── application.yml
│       │   ├── application-dev.yml
│       │   └── liquibase/   # 資料庫遷移腳本
│       ├── logback-spring.xml
│       └── banner.txt
└── test/                    # 測試程式碼
```

#### 程式碼風格規範

- 遵循 Java 編碼規範（Java Code Conventions）
- 使用有意義的變數與方法命名


### 支援與聯絡

- **官方網站**：[https://wallet.gov.tw](https://wallet.gov.tw)
- **技術支援信箱**：service@wallet.gov.tw
- **問題回報**：[GitHub Issues](https://github.com/moda-gov-tw/TWDIW-official-app/issues)

### 變更日誌

詳細的變更歷史記錄請參閱 [CHANGELOG.md](CHANGELOG.md)

### 致謝

本專案使用以下優秀的開源專案：

- [Spring Framework](https://spring.io/)
- [JHipster](https://www.jhipster.tech/)
- [Nimbus JOSE+JWT](https://connect2id.com/products/nimbus-jose-jwt)
- [PostgreSQL](https://www.postgresql.org/)
