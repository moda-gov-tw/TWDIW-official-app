# 數位發展部數位憑證皮夾 OID4VP 驗證端服務

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
    - [應用程式與 Profile](#應用程式與-profile)
    - [JWT 與安全性](#jwt-與安全性)
    - [JWK 儲存加密](#jwk-儲存加密)
    - [服務端點與驗證設定](#服務端點與驗證設定)
    - [管理端點](#管理端點)
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

本專案為數位發展部數位憑證皮夾之 **OID4VP（OpenID for Verifiable Presentation）驗證端服務**（moda-digitalwallet-verifier-oid4vp-service），部署於驗證機關環境。負責處理憑證驗證、授權請求產生與呈現、並校驗錢包提交的 Verifiable Presentation 與 Verifiable Credentials，符合 [OpenID for Verifiable Presentations](https://openid.net/specs/openid-4-verifiable-presentations-1_0.html) 與 [DIF Presentation Exchange](https://identity.foundation/presentation-exchange/) 規範。

### 主要功能

- **OID4VP 流程支援**：生成含 Presentation Definition 的授權請求（含 request / request_uri）。
- **QR Code 產生**：輸出授權請求 QR Code，提供錢包掃描。
- **VP/VC 驗證**：驗證 VP Token 與 VC 簽章、Nonce/ClientId、Presentation Submission 符合性。
- **DID 管理**：驗證端 DID 生成、註冊、審核流程對接。
- **Presentation Definition 管理**：支援多組定義與版本化（透過資料庫存取）。

### 技術架構

#### 核心技術

| 技術 | 版本 | 說明 |
|------|------|------|
| Java | 17 (Eclipse Temurin) | 執行環境 |
| Spring Boot | 3.3.13 | 應用程式框架 |
| JHipster | 8.6 | 開發平台 |
| Maven | 3.5+ | 建置工具 |
| PostgreSQL | 16 | 資料庫 |

#### 主要相依套件

| 套件名稱 | 版本 | 用途 |
|----------|------|------|
| Nimbus OAuth2 OIDC SDK | 11.23.1 | OIDC/OAuth2 協定、JWS/JWE |
| ZXing | 3.5.3 | QR Code 產生 |
| Networknt JSON Schema | 1.5.2 | Presentation Definition/Submission schema 驗證 |
| Caffeine | 3.2.0 | 快取 |
| Apache HttpClient 5 | 5.3.1 | HTTP 客戶端 |
| Liquibase | - | 資料庫版本控制 |

### 系統需求

#### 開發環境需求

- **Java Development Kit (JDK)**: OpenJDK 17 或更高版本（建議 Eclipse Temurin）
- **Apache Maven**: 3.5 或更高版本，或使用隨附的 Maven Wrapper
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
cd TWDIW-official-app/core-system/twdiw-oid4vp-handler
```

#### 2. 設定環境變數

在執行應用程式前，請設定下列必要與建議變數：

| 環境變數名稱 | 說明 | 範例 |
|-------------|------|------|
| `SPRING_DATASOURCE_URL` | 資料庫連線 URL | `jdbc:postgresql://localhost:5432/modadw_verifier?currentSchema=vp` |
| `SPRING_DATASOURCE_USERNAME` | 資料庫使用者名稱 | `modadw_verifier` |
| `SPRING_DATASOURCE_PASSWORD` | 資料庫密碼 | `your_password` |
| `JHIPSTER_SECURITY_AUTHENTICATION_JWT_BASE64_SECRET` | JWT 簽章祕鑰（Base64，解碼後至少 32 bytes/256-bit，必填） | `$(openssl rand -base64 32)` |
| `DID_ENCRYPT_KEY` | JWK 加密用 AES 金鑰，長度必須正好 32 字元（必填，缺少會阻止啟動）。建議只用英數字，方便複製貼上。 | 見下方產生方式 |
| `AP_ACCOUNT_USERNAME` | AP 帳號（若有管理端需求） | `your-ap-username` |
| `AP_ACCOUNT_PASSWORD` | AP 密碼 | `your-ap-password` |
| `VERIFIER_HOST_NAME` | 驗證端對外主機名稱（供 Well-known/redirect 使用） | `verifier.example.com` |
| `VP_INTERNAL_URL` | VP 服務內部 URL | `http://moda-digitalwallet-docker-verifier-vp-service:8080` |

設定範例（Linux/macOS）：

```bash
export SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/modadw_verifier?currentSchema=vp"
export SPRING_DATASOURCE_USERNAME="modadw_verifier"
export SPRING_DATASOURCE_PASSWORD="your_password"
export JHIPSTER_SECURITY_AUTHENTICATION_JWT_BASE64_SECRET="$(openssl rand -base64 32)"
export DID_ENCRYPT_KEY="$(LC_ALL=C tr -dc 'A-Za-z0-9' </dev/urandom | head -c 32)"
export VERIFIER_HOST_NAME="verifier.example.com"
export VP_INTERNAL_URL="http://vp-service:8080"
```

設定範例（Windows PowerShell）：

```powershell
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/modadw_verifier?currentSchema=vp"
$env:SPRING_DATASOURCE_USERNAME="modadw_verifier"
$env:SPRING_DATASOURCE_PASSWORD="your_password"
$env:JHIPSTER_SECURITY_AUTHENTICATION_JWT_BASE64_SECRET="(Use openssl rand -base64 32)"
$env:DID_ENCRYPT_KEY="(32 printable characters, e.g. from tr/head)"
$env:VERIFIER_HOST_NAME="verifier.example.com"
$env:VP_INTERNAL_URL="http://vp-service:8080"
```

祕鑰產生與注意事項：

- **JWT Base64 Secret**：啟動時會檢查必填且解碼後 >= 32 bytes，建議使用 `openssl rand -base64 32` 產生並以環境變數注入。
- **DID_ENCRYPT_KEY**：字串長度必須正好 32（將作為 AES-256 key 使用），缺失或長度不符將阻止應用啟動。簡單生成方式：

  ```bash
  # 產生 32 個英數字元，適合直接貼到環境變數或秘密管理
  export DID_ENCRYPT_KEY="$(LC_ALL=C tr -dc 'A-Za-z0-9' </dev/urandom | head -c 32)"
  echo "$DID_ENCRYPT_KEY"
  ```

  Windows PowerShell：
  ```powershell
  $env:DID_ENCRYPT_KEY = -join ((48..57 + 65..90 + 97..122) | Get-Random -Count 32 | ForEach-Object {[char]$_})
  Write-Output $env:DID_ENCRYPT_KEY
  ```
- 請勿將任何祕鑰寫入版本控制，部署時改由環境變數或秘密管理服務下發。

#### 3. 編譯專案

清理並編譯（跳過測試）：

```bash
mvn clean package -DskipTests
```

包含測試的完整編譯：

```bash
mvn clean package
```

**使用 Maven Wrapper 編譯（無需安裝 Maven）**

Linux/macOS：

```bash
chmod +x mvnw
./mvnw clean package -DskipTests
```

Windows：

```powershell
mvnw.cmd clean package -DskipTests
```

編譯完成後，JAR 檔案位於：
```
target/moda-digitalwallet-verifier-oidvp-service-0.0.1-SNAPSHOT.jar
```

#### 4. 執行應用程式

```bash
mvn spring-boot:run
java -jar -Dspring.profiles.active=dev target/moda-digitalwallet-verifier-oidvp-service-0.0.1-SNAPSHOT.jar
```

#### 5. 驗證服務運行

```bash
curl http://localhost:8080/management/health       # 健康檢查（公開）
curl http://localhost:8080/management/info         # 應用資訊（已開放匿名）
```

### 設定說明

#### 主要設定檔

- `src/main/resources/config/application.yml`：基礎設定（預設啟用 `dev` profile 群組）。
- `src/main/resources/config/application-dev.yml`：開發環境覆寫設定。

#### 關鍵設定項目

##### 應用程式與 Profile

```yaml
spring:
  application:
    name: modaDigitalwalletVerifierOidvpService
  profiles:
    active: dev  # 依部署環境覆寫
```

##### JWT 與安全性

```yaml
jhipster:
  security:
    authentication:
      jwt:
        base64-secret: ${JHIPSTER_SECURITY_AUTHENTICATION_JWT_BASE64_SECRET}
        token-validity-in-seconds: 86400
```

> 啟動時會檢查 `base64-secret` 是否存在且長度符合，否則拒絕啟動。

##### JWK 儲存加密

```yaml
DID_ENCRYPT_KEY=<32-char-string>  # 環境變數，無預設值
```

> `DefaultJWKStore` 在啟動時會驗證長度，缺失或長度不符將拋出例外。

##### 服務端點與驗證設定

```yaml
verifier:
  host:
    name: ${VERIFIER_HOST_NAME:localhost}

vp:
  internal:
    url: ${VP_INTERNAL_URL:http://moda-digitalwallet-docker-verifier-vp-service:8080}
```

##### 管理端點

- `/management/health`：允許匿名。
- `/management/info`：允許匿名（驗證邏輯交由其他微服務處理）。
- 其他管理端點依預設授權規則處理。

### API 文件

#### 主要端點

主要路徑：`/api/oidvp/`

| 方法與路徑 | 說明 |
|-----------|------|
| `GET /api/oidvp/authorization-request` | 生成含 Presentation Definition 的授權請求 URI |
| `GET /api/oidvp/qr-code` | 生成授權請求 QR Code（Base64） |
| `GET /api/oidvp/request/{state}` | 取得授權請求之 JWS request object |
| `GET /api/oidvp/presentation-definition/{state}` | 取得指定 state 的 Presentation Definition JSON |
| `POST /api/oidvp/authorization-response` (x-www-form-urlencoded) | 接收錢包授權回應並進行驗證 |
| `POST /api/oidvp/result` | 透過 transaction-id/response-code 查詢驗證結果 |
| `GET /api/oidvp/well-known/openid-configuration` | 取得 verifier metadata（well-known） |

> `authorization-response` 的 `vp_token`、`presentation_submission`、`state` 等欄位會進行完整驗證，包括 VP 簽章、Nonce、ClientId 與 Presentation Definition 符合性。

### 資料庫遷移

本專案使用 Liquibase 控管 schema 與初始資料。

#### 資料庫變更日誌位置

```
src/main/resources/config/liquibase/
├── master.xml
└── changelog/
    ├── 20000000000001_oidvp_initial_data.xml
    └── 20000000000002_add_custom_data_column.xml
```

#### 執行資料庫遷移

應用啟動時會自動執行；如需手動：

```bash
mvn liquibase:update
```

#### 查看待執行的變更

```bash
mvn liquibase:status
```

#### 回滾資料庫變更

```bash
mvn liquibase:rollback -Dliquibase.rollbackCount=1
```

### 開發指南

#### 專案結構

```
src/
├── main/
│   ├── java/gov/moda/dw/verifier/oidvp/
│   │   ├── config/          # Spring 設定
│   │   ├── domain/          # JPA 實體
│   │   ├── repository/      # 資料存取
│   │   ├── service/         # 業務邏輯（OID4VP、VP 驗證、DID、JWK）
│   │   ├── web/rest/        # REST API 控制器
│   │   ├── aop/             # AOP 日誌
│   │   ├── model/           # DTO/模型
│   │   └── util/            # 共用工具
│   └── resources/
│       ├── config/          # YAML 設定、Liquibase 腳本
│       ├── schema/          # Presentation Definition/Submission JSON Schema
│       ├── logback-spring.xml
│       └── banner.txt
└── test/                    # 測試程式碼
```

#### 程式碼風格規範

- 遵循 Java Code Conventions。
- 重要參數（如憑證、祕鑰、個資）記錄時必須脫敏（`@PrivacyInfo`、`RequestLoggerAspect`）。
- 配置檔與祕鑰請使用環境變數或秘密管理服務，勿提交實際值。

### 支援與聯絡

- **官方網站**：[https://wallet.gov.tw](https://wallet.gov.tw)
- **技術支援信箱**：service@wallet.gov.tw
- **問題回報**：[GitHub Issues](https://github.com/moda-gov-tw/TWDIW-official-app/issues)


### 變更日誌

詳見 [CHANGELOG.md](CHANGELOG.md)

### 致謝

本專案使用以下開源專案：

- [Spring Framework](https://spring.io/)
- [JHipster](https://www.jhipster.tech/)
- [Nimbus JOSE+JWT](https://connect2id.com/products/nimbus-jose-jwt)
- [Networknt JSON Schema](https://github.com/networknt/json-schema-validator)
- [PostgreSQL](https://www.postgresql.org/)
