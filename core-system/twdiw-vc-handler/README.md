# 數位發展部數位憑證皮夾 - 發證機關 VC 服務

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.13-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![JHipster](https://img.shields.io/badge/JHipster-8.6-orange.svg)](https://www.jhipster.tech/)

---

## 目錄

- [專案簡介](#專案簡介)
- [主要功能](#主要功能)
- [技術架構](#技術架構)
  - [核心技術](#核心技術)
  - [主要相依套件](#主要相依套件)
- [系統需求](#系統需求)
  - [開發環境需求](#開發環境需求)
  - [執行環境需求](#執行環境需求)
- [快速開始](#快速開始)
  - [取得原始碼](#取得原始碼)
  - [設定環境變數](#設定環境變數)
  - [編譯專案](#編譯專案)
  - [執行應用程式](#執行應用程式)
  - [驗證服務運行](#驗證服務運行)
- [設定說明](#設定說明)
  - [主要設定檔](#主要設定檔)
  - [關鍵設定項目](#關鍵設定項目)
- [API 文件](#api-文件)
  - [主要端點](#主要端點)
  - [API 認證](#api-認證)
- [資料庫遷移](#資料庫遷移)
  - [資料庫變更日誌位置](#資料庫變更日誌位置)
  - [執行資料庫遷移](#執行資料庫遷移)
  - [查看待執行的變更](#查看待執行的變更)
  - [回滾資料庫變更](#回滾資料庫變更)
- [開發指南](#開發指南)
  - [專案結構](#專案結構)
  - [程式碼風格規範](#程式碼風格規範)
- [系統監控](#系統監控)
- [支援與聯絡](#支援與聯絡)
- [變更日誌](#變更日誌)
- [致謝](#致謝)
- [相關資源](#相關資源)

## 專案簡介

本專案為數位發展部推動之數位憑證皮夾基礎建設的一部分，提供發證機關端的可驗證憑證（Verifiable Credential, VC）服務。系統部署於各發證機關環境，負責產生、管理與驗證數位憑證，並提供狀態清冊、DID、憑證轉移等功能。

## 主要功能

- VC 簽發、查詢、狀態更新（撤銷/停用/恢復）
- DID 產生與註冊
- 憑證轉移（產生 QR Code）
- 狀態清冊產製與簽章金鑰生成
- 系統設定查詢/更新、序號管理
- 公開查詢：版本、狀態清冊、Schema、公鑰

## 技術架構

### 核心技術

| 技術 | 版本 | 說明 |
|------|------|------|
| Java | 17 (Eclipse Temurin) | 執行環境 |
| Spring Boot | 3.3.13 | 應用程式框架 |
| JHipster | 8.6 | 開發平台 |
| Maven | 3.5+ | 建構工具 |
| PostgreSQL | 16 | 資料庫 |

### 主要相依套件

| 套件名稱 | 版本 | 用途 |
|----------|------|------|
| Nimbus JOSE+JWT | 10.0.2 | JWT/JOSE |
| Verifiable Credentials Java | 1.7.0 | VC 處理 |
| SD-JWT | 1.4 | Selective Disclosure |
| Apache HttpClient 4 | 4.5.14 | HTTP 客戶端 |
| Liquibase | - | 資料庫版本控制 |
| MapStruct | - | DTO 映射 |

## 系統需求

### 開發環境需求
- Java Development Kit (JDK) 17（建議 Eclipse Temurin）
- Apache Maven 3.5 以上（可使用專案內的 Maven Wrapper）
- PostgreSQL 16 以上
- 作業系統：Windows、macOS 或 Linux

### 執行環境需求
- Java Runtime Environment (JRE) 17 以上
- 記憶體：建議至少 2GB
- 磁碟空間：建議至少 1GB

## 快速開始

### 取得原始碼

```bash
git clone https://github.com/moda-gov-tw/TWDIW-official-app.git
cd TWDIW-official-app/core-system/twdiw-vc-handler
```

### 設定環境變數

| 變數名稱 | 說明 | 範例值 | 必要性 |
|---------|------|--------|--------|
| `SPRING_DATASOURCE_URL` | 資料庫連線字串 | `jdbc:postgresql://localhost:5432/modadw_issuer?currentSchema=vc` | 必要 |
| `SPRING_DATASOURCE_USERNAME` | 資料庫使用者 | `modadw_issuer` | 必要 |
| `SPRING_DATASOURCE_PASSWORD` | 資料庫密碼 | `your_secure_password` | 必要 |
| `JHIPSTER_SECURITY_AUTHENTICATION_JWT_BASE64_SECRET` | JWT 簽章密鑰（Base64，≥256 bits） | `openssl rand -base64 64` | **必要** |
| `VC_KEY_ENC` | VC 資料加密金鑰（AES256） | `openssl rand -hex 32` | **必要** |
| `ISSUER_HOST_NAME` | 發證機關主機名稱 | `issuer.example.gov.tw` | 必要 |
| `ISSUER_INTERNAL_URL` | 發證機關內部服務網址 | `http://issuer-internal:8080` | 必要 |
| `OID4VCI_INTERNAL_URL` | OID4VCI 內部服務網址 | `http://oid4vci-internal:8080` | 必要 |
| `VP_INTERNAL_URL` | VP 服務內部網址 | `http://vp-internal:8080` | 必要 |
| `JHIPSTER_CORS_ALLOWED_ORIGINS` | CORS 允許來源 | `http://localhost:8080,https://example.com` | 選用 |
| `JHIPSTER_CORS_ALLOWED_ORIGIN_PATTERNS` | CORS 允許模式 | `https://*.example.com` | 選用 |
| `AP_ACCOUNT_USERNAME` | AP 系統帳號 | `ap_user` | 選用 |
| `AP_ACCOUNT_PASSWORD` | AP 系統密碼 | `ap_password` | 選用 |
| `VC_ACCOUNT_USERNAME` | VC 系統帳號 | `vc_user` | 選用 |
| `VC_ACCOUNT_PASSWORD` | VC 系統密碼 | `vc_password` | 選用 |
| `VC_ACCOUNT_TOKEN` | VC 系統 Token | `vc_token` | 選用 |

狀態清單簽章金鑰（`status.list.key`）：
1. 產生 P-256 私鑰：`openssl ecparam -name prime256v1 -genkey -noout -out status-list-private.pem`
2. 產生公鑰：`openssl ec -in status-list-private.pem -pubout -out status-list-public.pem`
3. 轉 JWK 並取得 `x/y/d`：
   ```bash
   node -e "const fs=require('fs');const {createPrivateKey}=require('crypto');const key=createPrivateKey(fs.readFileSync('status-list-private.pem'));const jwk=key.export({format:'jwk'});jwk.kid='your-key-id';console.log(JSON.stringify({kty:jwk.kty,crv:jwk.crv,kid:jwk.kid,x:jwk.x,y:jwk.y,d:jwk.d},null,2));"
   ```
4. 將值填入 `src/main/resources/config/liquibase/data/setting_release.csv` 的 `status.list.key`（或部署時以 DB/設定服務覆蓋），格式：  
   `{"kty":"EC","crv":"P-256","kid":"<REPLACE_WITH_KID>","x":"<REPLACE_WITH_BASE64URL_X>","y":"<REPLACE_WITH_BASE64URL_Y>","d":"<REPLACE_WITH_BASE64URL_PRIVATE_D>"}`
5. 私鑰僅應存放於 Secret/KMS 或部署環境變數，勿提交版控。

### 編譯專案

使用 Maven Wrapper（推薦）：
```bash
# Linux/macOS
chmod +x mvnw
./mvnw clean package -DskipTests

# Windows
mvnw.cmd clean package -DskipTests
```

完整編譯（含測試）：
```bash
./mvnw clean package
```

### 執行應用程式

開發環境：
```bash
./mvnw spring-boot:run -Dspring.profiles.active=dev
# 或
java -jar target/moda-digitalwallet-issuer-vc-service-0.0.1-SNAPSHOT.jar -Dspring.profiles.active=dev
```

### 驗證服務運行

```bash
curl http://localhost:8080/management/health
curl http://localhost:8080/management/info
```

## 設定說明

### 主要設定檔

- `src/main/resources/config/application.yml`：基礎設定
- `src/main/resources/config/application-dev.yml`：開發設定
- Liquibase：`src/main/resources/config/liquibase/`

### 關鍵設定項目

應用程式：
```yaml
spring:
  application:
    name: twdiwIssuerVcService
  profiles:
    active: dev  # 建議於部署時覆寫為對應環境
```

資料庫：
```yaml
spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL}
    username: ${SPRING_DATASOURCE_USERNAME}
    password: ${SPRING_DATASOURCE_PASSWORD}
    type: com.zaxxer.hikari.HikariDataSource
    hikari:
      pool-name: Hikari
      auto-commit: false
  jpa:
    hibernate:
      ddl-auto: none
```

伺服器：
```yaml
server:
  port: 8080
  servlet:
    session:
      cookie:
        http-only: true
```

安全性（JWT 基礎配置，預設資源伺服器已被註解，需自行啟用）：
```yaml
jhipster:
  security:
    authentication:
      jwt:
        base64-secret: ${JHIPSTER_SECURITY_AUTHENTICATION_JWT_BASE64_SECRET}
        token-validity-in-seconds: ${JHIPSTER_SECURITY_AUTHENTICATION_JWT_TOKEN_VALIDITY:86400}
        token-validity-in-seconds-for-remember-me: ${JHIPSTER_SECURITY_AUTHENTICATION_JWT_REMEMBER_ME_VALIDITY:2592000}
```

發證機關/對外服務：
```yaml
issuer:
  host:
    name: ${ISSUER_HOST_NAME:localhost}
  internal:
    url: ${ISSUER_INTERNAL_URL:http://issuer-manager-service:8080}

oid4vci:
  internal:
    url: ${OID4VCI_INTERNAL_URL:http://oid4vci-service:8080}

vp:
  internal:
    url: ${VP_INTERNAL_URL:http://vp-service:8080}
```

狀態清冊金鑰與排程：請透過 `setting_release.csv` 或 DB 設定，勿提交真實私鑰。

## API 文件

### 主要端點

> 目前 `SecurityConfiguration` 將 `/api/**` 設為 `permitAll`；正式環境請啟用 JWT/OAuth2 與授權規則。

**DID 管理**
- `POST /api/did`：產生並註冊發證機關 DID 金鑰

**憑證管理**
- `POST /api/credential`：簽發新憑證
- `PUT /api/credential/{cid}/{action}`：更新憑證狀態（revocation/suspension/recovery）
- `GET /api/credential/{cid}`：以 CID 查詢憑證
- `GET /api/credential/nonce/{nonce}`：以 Nonce 查詢憑證
- `GET /api/credentials/{credentialType}`：依憑證類型分頁查詢
- `POST /api/credential/{cid}/transfer`：憑證轉移產生 QRCode

**憑證資料管理**
- `POST /api/setdata`：設定憑證資料

**狀態清冊管理**
- `POST /api/status-list/{credentialType}`：產製最新狀態清冊（可附帶 `statusListType`、`gid` 參數）
- `POST /api/status-list/genKey`：產製狀態清冊簽署金鑰

**序號管理**
- `POST /api/setseq/{credentialType}`：設定序號
- `POST /api/delseq/{credentialType}`：刪除序號
- `POST /api/funcswitch/{credentialType}`：啟用/停用功能

**使用者/設定（管理用途）**
- `GET /api/extendusers`：查詢擴展使用者資料
- `GET /api/checksetting`：讀取系統設定
- `POST /api/updatesetting`：更新系統設定

**公開資訊（無需認證）**
- `GET /api/version`：取得系統版本
- `GET /api/status-list/{credentialType}/{groupName}`：取得狀態列表
- `GET /api/schema/{schemaName}`：取得憑證 Schema
- `GET /api/keys`：取得公鑰資訊

## 資料庫遷移

### 資料庫變更日誌位置

```
src/main/resources/config/liquibase/
├── master.xml
└── changelog/
    ├── 10000000000001_vc_initial_data.xml
    ├── 10000000000002_vc_initial_data.xml
    ├── 20250715_add_suspension_table.xml
    └── 20250724_alter_credential_type_length.xml
```

### 執行資料庫遷移

應用啟動時自動執行；若需手動：
```bash
./mvnw liquibase:update
```

### 查看待執行的變更
```bash
./mvnw liquibase:status
```

### 回滾資料庫變更
```bash
./mvnw liquibase:rollback -Dliquibase.rollbackCount=1
```

## 開發指南

### 專案結構

```
src/
├── main/
│   ├── java/gov/moda/dw/issuer/vc/
│   │   ├── config/      # Spring & 安全設定
│   │   ├── domain/      # JPA 實體
│   │   ├── repository/  # 資料存取層
│   │   ├── service/     # 業務邏輯
│   │   ├── web/rest/    # REST API 控制器
│   │   ├── security/    # 安全與權限
│   │   ├── util/        # 工具類別
│   │   └── vo/          # 值物件
│   └── resources/
│       ├── config/      # application yml、liquibase
│       ├── templates/   # 郵件與錯誤頁面
│       ├── banner.txt
│       └── logback-spring.xml
└── test/                # 測試程式碼
```

### 程式碼風格規範

- 遵循 Java Code Conventions
- 使用 Checkstyle 進行程式碼檢查
- 公開 API 需搭配文件
- 提交前請格式化程式碼

檢查指令：
```bash
./mvnw checkstyle:check
```

## 系統監控

`/management/health`、`/management/info` 預設允許匿名；其餘端點預設需授權，請依需求開啟並限制角色。

已啟用的監控端點包含：
- `/management/health` - 健康檢查
- `/management/info` - 系統資訊
- `/management/logfile` - 日誌檔位置
- `/management/loggers` - 動態調整 logger
- `/management/prometheus` - Prometheus 指標
- `/management/threaddump` - 線程狀態
- `/management/liquibase` - Liquibase 狀態
- `/management/configprops` - Spring 配置
- `/management/env` - 環境變數
- `/management/jhimetrics`、`/management/jhiopenapigroups` - JHipster 指標

## 支援與聯絡

- **官方網站**：[https://wallet.gov.tw](https://wallet.gov.tw)
- **技術支援信箱**：service@wallet.gov.tw
- **問題回報**：[GitHub Issues](https://github.com/moda-gov-tw/TWDIW-official-app/issues)

## 變更日誌

請參閱 [CHANGELOG.md](CHANGELOG.md)。

## 致謝

本專案使用以下開源專案：
- Spring Framework, Spring Boot
- JHipster
- Nimbus JOSE+JWT
- PostgreSQL

## 相關資源

- [W3C Verifiable Credentials](https://www.w3.org/TR/vc-data-model/)
- [DID Core](https://www.w3.org/TR/did-core/)
- [OpenID for Verifiable Credential Issuance](https://openid.net/specs/openid-4-verifiable-credential-issuance-1_0.html)
