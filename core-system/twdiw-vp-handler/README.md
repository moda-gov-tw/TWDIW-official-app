# 臺灣數位憑證皮夾 - 驗證端 VP 服務

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
  - [驗證服務執行狀態](#5-驗證服務執行狀態)
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

本專案為 **數位憑證皮夾驗證端 VP 服務**，部署於卡片驗證機關，負責驗證可驗證簡報（Verifiable Presentation, VP）與內含的可驗證憑證（Verifiable Credential, VC），並支援 SD-JWT 選擇性揭露。

### 主要功能

- **VP 驗證**：驗證 JWT 格式的 VP，檢查簽章與持有人一致性
- **VC 驗證**：並行驗證多張 VC（內容、簽章、Schema、狀態清單等）
- **DID 支援**：透過 DID 文件進行簽章驗證
- **狀態檢查**：支援 Status List 2021 即時狀態確認
- **選擇性揭露**：支援 SD-JWT 隱私保護簡報
- **非同步處理**：以 CompletableFuture 提升驗證效能

### 技術架構

#### 核心技術

| 技術 | 版本 | 說明 |
|------|------|------|
| Java | 17 (Eclipse Temurin) | 執行環境 |
| Spring Boot | 3.3.13 | 應用程式框架 |
| JHipster | 8.6 | 開發樣板與設定管理 |
| Maven | 3.5+ | 專案建置工具 |
| PostgreSQL | 16 | 資料庫系統 |

#### 主要相依套件

| 套件名稱 | 版本 | 用途 |
|----------|------|------|
| verifiable-credentials-java | 1.12.0 | W3C VC 處理 |
| sd-jwt | 1.4 | 選擇性揭露 JWT |
| key-formats-java | 1.16.0 | 金鑰格式處理 |
| ld-signatures-java | 1.6.0 | Linked Data 簽章 |
| nimbus-jose-jwt | 10.0.2 | JWT/JOSE 處理 |
| jsonschemafriend | 0.12.3 | JSON Schema 驗證 |
| jsoup | 1.15.3 | HTML/JSON 解析 (相依套件更新) |

### 系統需求

#### 開發環境需求

- **JDK**：OpenJDK 17 或更高版本（建議使用 Eclipse Temurin）
- **Apache Maven**：3.5 或更高版本
- **PostgreSQL**：16 或更高版本
- **作業系統**：Windows、macOS 或 Linux

#### 執行環境需求

- **JRE**：OpenJDK 17 或更高版本
- **記憶體**：建議至少 2GB RAM
- **磁碟空間**：建議至少 1GB 可用空間

### 快速開始

#### 1. 取得原始碼

```bash
git clone https://github.com/moda-gov-tw/TWDIW-official-app.git
cd TWDIW-official-app/core-system/twdiw-vp-handler
```

#### 2. 設定環境變數

在啟動之前請設定以下環境變數：

| 環境變數名稱 | 說明 | 範例 |
|-------------|------|------|
| `SPRING_DATASOURCE_URL` | 資料庫連線 URL | `jdbc:postgresql://localhost:5432/modadw_verifier?currentSchema=vp` |
| `SPRING_DATASOURCE_USERNAME` | 資料庫帳號 | `vpuser` |
| `SPRING_DATASOURCE_PASSWORD` | 資料庫密碼 | `your-db-password` |
| `JHIPSTER_SECURITY_AUTHENTICATION_JWT_BASE64_SECRET` | JWT 簽章密鑰（Base64，至少 256 bits） | `$(openssl rand -base64 64)` |
| `SERVER_PORT` | HTTP 埠（非必填，預設 8080） | `8080` |
| `SPRING_LIQUIBASE_CONTEXTS` | Liquibase context (`dev`/`test`/`release`) | `dev` |

範例（Linux/macOS）：

```bash
export SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/modadw_verifier?currentSchema=vp"
export SPRING_DATASOURCE_USERNAME="vpuser"
export SPRING_DATASOURCE_PASSWORD="your-db-password"
export JHIPSTER_SECURITY_AUTHENTICATION_JWT_BASE64_SECRET="$(openssl rand -base64 64)"
export SERVER_PORT=8080
export SPRING_LIQUIBASE_CONTEXTS=dev
```

#### 3. 編譯專案

```bash
# 清理並打包（跳過測試）
chmod +x mvnw  
./mvnw clean package -DskipTests

```

#### 4. 執行應用程式

```bash
java -jar target/moda-digitalwallet-verifier-vp-service-0.0.1-SNAPSHOT.jar
# 或
mvn spring-boot:run
```

#### 5. 驗證服務執行狀態

```bash
# 健康檢查
curl http://localhost:8080/management/health

# 版本資訊
curl http://localhost:8080/api/version
```

### 設定說明

#### 主要設定檔

設定檔位於 `src/main/resources/config/`：

- `application.yml`：基礎設定
- `application-dev.yml`：開發環境設定（預設啟用）

#### 關鍵設定項目

##### 應用程式設定

```yaml
spring:
  application:
    name: twdiwVerifierVpService
  profiles:
    active: dev
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

##### 安全性設定（JWT）

```yaml
jhipster:
  security:
    authentication:
      jwt:
        base64-secret: ${JHIPSTER_SECURITY_AUTHENTICATION_JWT_BASE64_SECRET}
        token-validity-in-seconds: 86400
        token-validity-in-seconds-for-remember-me: 2592000
```

JWT 密鑰需為至少 256 bits 的 Base64 字串。產生範例：

```bash
openssl rand -base64 64 | tr -d '\n'
```

> 下列僅為示意輸出，請勿用於任何環境，務必自行重新產生：
> ```
> BASE64_SAMPLE_ONLY_DO_NOT_USE_IN_PROD==
> ```

##### Frontend DID 查詢位址

Liquibase 會初始化 `setting` 資料表的 `url.frontend.query_did`。CSV 內為佔位值，請啟動前依環境調整：

- 正式環境：修改 `src/main/resources/config/liquibase/data/vp_setting_release.csv`，將 `https://replace-with-frontend-host.example/api/did/` 改成實際網址，並以 `SPRING_LIQUIBASE_CONTEXTS=release` 執行。
- 測試/UAT：修改 `src/main/resources/config/liquibase/data/vp_setting_test.csv`，將 `https://replace-with-frontend-uat-host.example/api/did/` 改成測試網址，並以 `SPRING_LIQUIBASE_CONTEXTS=test` 執行。
- 若服務已啟動後需變更，請直接更新資料庫 `setting` 表中的 `url.frontend.query_did`。
- 目前專案未提供 `application-release.yml` 或 `application-test.yml`，若需對應 profile，請自行新增並依環境設定資料庫與安全性相關參數。

### API 文件

#### 主要端點

- **VP 驗證**：`POST /api/presentation/validation`
  - 內容型別：`application/json`
  - 請求範例：

    ```json
    [
      "<vp-jwt-1>",
      "<vp-jwt-2>"
    ]
    ```

  - 回應：依驗證結果回傳 HTTP 200/400/404/500，內容為字串型 JSON 結果。

- **版本查詢**：`GET /api/version`，回傳目前版本字串。

- **管理端點**（Actuator）：`/management/health`、`/management/info`、`/management/prometheus`、`/management/loggers`、`/management/liquibase` 等。

> 注意：預設 Security 設定允許 `/api/**` 與部分管理端點無需驗證，正式環境建議依需求收緊規則並啟用 JWT 驗證。

### 資料庫遷移

本專案使用 Liquibase 進行資料庫版控。

#### 資料庫變更日誌位置

```
src/main/resources/config/liquibase/
├── master.xml
├── changelog/
│   └── 10000000000001_vp_initial_data.xml
└── data/
    ├── vp_setting_release.csv
    └── vp_setting_test.csv
```

#### 執行資料庫遷移

應用啟動時會自動套用遷移；手動執行：

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
│   ├── java/gov/moda/dw/verifier/vc/
│   │   ├── config/        # Spring 設定（安全性、執行緒池等）
│   │   ├── management/    # 指標與監控
│   │   ├── service/       # 業務邏輯（含 VP 選擇性揭露驗證）
│   │   ├── task/          # 非同步任務
│   │   ├── util/          # 工具類別
│   │   └── web/rest/      # REST API 控制器
│   └── resources/
│       ├── config/        # YAML、Liquibase、其他設定
│       ├── logback-spring.xml
│       └── banner.txt
└── test/                  # 測試程式碼
```

#### 程式碼風格規範

- 遵循 Java Code Conventions
- 優先使用英文或中性命名，避免含意模糊的縮寫
- 提交前執行 `mvn clean verify` 以跑測試與程式碼檢查

### 支援與聯絡

- **官方網站**：[https://wallet.gov.tw](https://wallet.gov.tw)
- **技術支援信箱**：service@wallet.gov.tw
- **問題回報**：[GitHub Issues](https://github.com/moda-gov-tw/TWDIW-official-app/issues)

### 變更日誌

請參閱 [CHANGELOG.md](CHANGELOG.md) 以了解版本歷程。

### 致謝

感謝以下開源專案與社群貢獻：
- [Spring Framework](https://spring.io/)
- [JHipster](https://www.jhipster.tech/)
- [Nimbus JOSE+JWT](https://connect2id.com/products/nimbus-jose-jwt)
- [PostgreSQL](https://www.postgresql.org/)
- 以及本專案相依的其他開源套件
