# 數位發展部數位憑證皮夾發行端管理服務

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.13-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![JHipster](https://img.shields.io/badge/JHipster-8.5.0-orange.svg)](https://www.jhipster.tech/)

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

本專案為數位發展部數位憑證皮夾之 **發行端管理服務**，此服務部署於憑證發行機關的環境中，提供完整的數位憑證管理功能與 API 介接服務。透過本服務，發行機關可以進行 DID 註冊、數位憑證模板管理、憑證狀態管理，以及提供標準化的 API 介面供外部系統介接使用。

### 主要功能

- **DID 管理**：註冊與管理分散式識別碼（Decentralized Identifier）
- **VC 建立管理**：數位憑證模板的建立、編輯與停用
- **VC 資料狀態管理**：憑證的撤銷、停用、復用與查詢功能
- **API 介接服務**：提供標準化的 API 介面，支援憑證發行、撤銷、停用、復用與查詢
- **API 追蹤稽核**：完整的 API 呼叫追蹤與稽核功能，確保操作可追溯性
- **權限管理**：細緻的使用者權限控管與角色管理
- **組織管理**：支援多組織架構的憑證發行管理

### 技術架構

#### 核心技術

| 技術 | 版本 | 說明 |
|------|------|------|
| Java | 17 (Eclipse Temurin) | 執行環境 |
| Spring Boot | 3.3.13 | 應用程式框架 |
| JHipster | 8.5.0 | 開發平台 |
| Maven | 3.5+ | 專案建構工具 |
| PostgreSQL | 16 | 資料庫系統 |

#### 主要相依套件

| 套件名稱 | 版本 | 用途 |
|----------|------|------|
| Nimbus JOSE+JWT | 9.40 | JWT 與 JOSE 處理 |
| ZXing | 3.5.3 | QR Code 產生 |
| Apache HttpClient 5 | 5.3.1 | HTTP 客戶端 |
| Liquibase | - | 資料庫版本控制 |
| MapStruct | - | 物件映射 |
| Spring Security | - | 安全性框架 |
| Quartz Scheduler | - | 排程任務管理 |

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

#### 🚀 使用 .env 和 Docker 快速啟動（推薦）

本專案已配置完整的 `.env` 環境變數檔案和 Docker Compose，可以快速啟動開發環境：

**步驟 1：建立資料庫**

```bash
# 使用現有的 PostgreSQL（如果已有其他專案的 PostgreSQL 容器）
docker exec <postgres_container_name> psql -U <username> -c "CREATE DATABASE twdiw_issuer;"

# 或啟動新的 PostgreSQL 容器（若專案目錄有 docker-compose.yml）
docker-compose up -d
```

**步驟 2：配置環境變數**

複製 `.env.example` 為 `.env` 並根據實際環境修改設定值：

```bash
# 1. 複製環境變數範本
cp .env.example .env

# 2. 編輯 .env 檔案，填入實際的設定值
vi .env  # 或使用任意文字編輯器
```

> ⚠️ **安全提示**：
> - `.env` 檔案包含敏感資訊，請勿提交到版本控制系統
> - JWT 密鑰和加密金鑰務必重新產生，切勿使用範例值
> - 正式環境請使用強密碼並定期更換密鑰

**步驟 3：啟動應用**

```bash
# 使用啟動腳本（會自動載入 .env 環境變數）
./start.sh

# 或手動啟動
./mvnw spring-boot:run
```

**步驟 4：驗證服務**

```bash
# 檢查健康狀態
curl http://localhost:8080/management/health

# 預期回應
{"status":"UP","groups":["liveness","readiness"]}
```

> 💡 **提示**：首次啟動會自動執行 Liquibase 資料庫遷移，建立所需的資料表結構。

---

#### 1. 取得原始碼

```bash
git clone https://github.com/moda-gov-tw/TWDIW-official-app.git
cd TWDIW-official-app/core-system/twdiw-issuer-api
```

#### 2. 設定環境變數

在執行應用程式前，請設定以下必要的環境變數：

##### 核心環境變數

| 環境變數名稱 | 說明 | 範例 |
|-------------|------|------|
| `SPRING_DATASOURCE_URL` | 資料庫連線 URL | `jdbc:postgresql://localhost:5432/twdiw_issuer?currentSchema=vc_manager` |
| `SPRING_DATASOURCE_USERNAME` | 資料庫使用者名稱 | `your_database_username` |
| `SPRING_DATASOURCE_PASSWORD` | 資料庫密碼 | `your_database_password` |
| `JHIPSTER_SECURITY_AUTHENTICATION_JWT_BASE64_SECRET` | JWT 簽章密鑰（Base64 編碼，至少 256 bits） | 使用 `openssl rand -base64 64` 產生 |
| `ATTRIBUTEENCRYPTOR_KEY` | 屬性加密金鑰（Base64 編碼後解碼為 24 bytes/256-bit，**必填**） | 請參考`初始化 AttributeEncryptor 金鑰`產生並設定 |

##### RSA 金鑰設定

| 環境變數名稱 | 說明 | 產生方式 |
|-------------|------|---------|
| `FIN_RSAUTIL_PRVKY` | RSA 私鑰（Base64 編碼） | 請參考`初始化 RSA 金鑰對`產生並設定 |
| `FIN_RSAUTIL_PKY` | RSA 公鑰（Base64 編碼） | 請參考`初始化 RSA 金鑰對`產生並設定 |
| `ISSUER_SECURITY_AUTHENTICATION_JWT_PRIVATE_KEY` | Issuer JWT 簽署私鑰（PEM 格式） | 請參考`初始化 RSA 金鑰對`產生並設定 |
| `ISSUER_SECURITY_AUTHENTICATION_JWT_PUBLIC_KEY` | Issuer JWT 簽署公鑰（PEM 格式） | 請參考`初始化 RSA 金鑰對`產生並設定 |

##### 郵件服務設定

| 環境變數名稱 | 說明 | 範例 |
|-------------|------|------|
| `SPRING_MAIL_HOST` | 郵件伺服器主機名稱或 IP 位址 | `smtp.gmail.com`, `smtp.office365.com`, `localhost` |
| `SPRING_MAIL_PORT` | 郵件伺服器埠號 | `25` (SMTP), `465` (SMTPS), `587` (SMTP with STARTTLS) |
| `MAIL_NICKNAME`    | 郵件寄件者名稱 | `發行端模組系統`（預設）|
| `MAIL_FROM_ADDRESS` | 郵件寄件者地址 | `noreply@yourcompany.com` |

##### 服務端點設定

| 環境變數名稱 | 說明 | 範例 |
|-------------|------|------|
| `ISSUER_HOST_NAME` | 發行端主機名稱 | `localhost:8080` 或 `issuer.example.com` |
| `METADATA_SCHEMA_CREDENTIAL_ISSUER` | 憑證發行者元資料端點 | `https://issuer.example.com/oid4vci/api/issuer/` |
| `METADATA_SCHEMA_CREDENTIAL_ENDPOINT` | 憑證端點元資料 | `https://issuer.example.com/oid4vci/api/issuer/` |

##### 內部 API 端點設定

| 環境變數名稱 | 說明 | 預設值 |
|-------------|------|--------|
| `OID4VCI_INTERNAL_URL` | OID4VCI 服務內部 URL | `http://moda-digitalwallet-docker-issuer-oid4vci-service:8080` |
| `VC_INTERNAL_URL` | VC 服務內部 URL | `http://moda-digitalwallet-docker-issuer-vc-service:8080` |

##### 功能開關設定

| 環境變數名稱 | 說明 | 預設值 |
|-------------|------|--------|
| `SWITCH_VERIFY_OTP` | OTP 驗證開關 | `false` |
| `OTP_EXPIRE_TIME` | OTP 有效時間（秒） | `300` |
| `OTP_RE_SEND_TIME` | OTP 重發時間（秒） | `60` |
| `QRCODE_LINK_TYPE` | 動態 Qr Code 型態，1: 原始 deeplink, 2: Universal/App link | `2` |

設定範例（Linux/macOS）：

```bash
# 資料庫設定
export SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/twdiw_issuer?currentSchema=vc_manager"
export SPRING_DATASOURCE_USERNAME="your_database_username"
export SPRING_DATASOURCE_PASSWORD="your_database_password"

# 安全性設定
export JHIPSTER_SECURITY_AUTHENTICATION_JWT_BASE64_SECRET="$(openssl rand -base64 64)"
export ATTRIBUTEENCRYPTOR_KEY="$(openssl rand -base64 24)"
export FIN_RSAUTIL_PRVKY="your-rsa-private-key"
export FIN_RSAUTIL_PKY="your-rsa-public-key"
export ISSUER_SECURITY_AUTHENTICATION_JWT_PRIVATE_KEY="your-rsa-jwt-private-key"
export ISSUER_SECURITY_AUTHENTICATION_JWT_PUBLIC_KEY="your-rsa-jwt-public-key"

# 主機名稱設定
export ISSUER_HOST_NAME="localhost:8080"

# 郵件服務設定
export SPRING_MAIL_HOST="localhost"
export SPRING_MAIL_PORT="25"
```

設定範例（Windows PowerShell）：

```powershell
# 資料庫設定
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/twdiw_issuer?currentSchema=vc_manager"
$env:SPRING_DATASOURCE_USERNAME="your_database_username"
$env:SPRING_DATASOURCE_PASSWORD="your_database_password"

# 安全性設定
$env:JHIPSTER_SECURITY_AUTHENTICATION_JWT_BASE64_SECRET="your-jwt-base64-secret"
$env:ATTRIBUTEENCRYPTOR_KEY="your-attribute-encryptor-key"
$env:FIN_RSAUTIL_PRVKY="your-rsa-private-key"
$env:FIN_RSAUTIL_PKY="your-rsa-public-key"
$env:ISSUER_SECURITY_AUTHENTICATION_JWT_PRIVATE_KEY="your-rsa-jwt-private-key"
$env:ISSUER_SECURITY_AUTHENTICATION_JWT_PUBLIC_KEY="your-rsa-jwt-public-key"

# 主機名稱設定
$env:ISSUER_HOST_NAME="localhost:8080"

# 郵件服務設定
$env:SPRING_MAIL_HOST="localhost"
$env:SPRING_MAIL_PORT="25"
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

編譯成功後，會在 `target` 目錄下產生 JAR 檔案：

```
target/moda-digitalwallet-issuer-manager-service-0.0.1-SNAPSHOT.jar
```

#### 4. 執行應用程式

##### 使用 Maven 執行

```bash
mvn spring-boot:run
```

##### 使用 JAR 檔案執行

```bash
java -jar target/moda-digitalwallet-issuer-manager-service-0.0.1-SNAPSHOT.jar
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

- `application.yml`：主要設定檔（包含所有基礎設定）

**環境管理：**
- 所有環境特定的設定（如資料庫連線、服務 URL 等）都透過環境變數控制
- 本地開發時使用預設值（如 `localhost`）
- 部署到不同環境時透過環境變數覆寫

#### 關鍵設定項目

##### 應用程式設定

```yaml
spring:
  application:
    name: modaDigitalWalletIssuerManagerService
  profiles:
    active: '@spring.profiles.active@'  # 由 Maven 動態設定（可設定為 test 或 release）
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
        same-site: strict
        secure: true
```

##### 安全性設定

```yaml
jhipster:
  security:
    authentication:
      jwt:
        base64-secret: ${JHIPSTER_SECURITY_AUTHENTICATION_JWT_BASE64_SECRET}
        token-validity-in-seconds: 1200

attributeencryptor:
  key: ${ATTRIBUTEENCRYPTOR_KEY}

fin.rsaUtil.prvKy: ${FIN_RSAUTIL_PRVKY}
fin.rsaUtil.pky: ${FIN_RSAUTIL_PKY}

issuer:
  security:
    authentication:
      jwt:
        private-key: ${ISSUER_SECURITY_AUTHENTICATION_JWT_PRIVATE_KEY}
        public-key: ${ISSUER_SECURITY_AUTHENTICATION_JWT_PUBLIC_KEY}
```

##### 發行端設定

```yaml
issuer:
  host:
    name: ${ISSUER_HOST_NAME:localhost:8080}
```

##### 初始化 AttributeEncryptor 金鑰

- 此值為 **必填**，需為 256-bit（24 bytes）並以 Base64 編碼，缺失會導致應用啟動失敗。
- 產生方式範例：

```bash
openssl rand -base64 24
# 或
head -c 24 /dev/urandom | base64
```

**重要提醒**：
- AttributeEncryptor 秘鑰一旦用於加密資料（例如帳號暱稱），初始化後請勿變更。
- 若在未完全解密舊資料前更換秘鑰，將導致原有加密內容無法還原，造成資料無法正確顯示或永久損毀。
- 建議使用環境變數：`export ATTRIBUTEENCRYPTOR_KEY="$(openssl rand -base64 24)"`，部署時透過 Secret/KMS 注入。
- 或在 `application.yml` 的 `attributeencryptor.key` 以外部化設定提供，但請勿將實際值提交至版本控制。

##### 初始化 RSA 金鑰對

系統需要兩組 RSA 金鑰對：

1. **fin.rsaUtil 金鑰對**（用於一般加密）
2. **issuer.security.authentication.jwt 金鑰對**（用於 JWT 簽署）

產生方式：

```bash
# 產生 RSA 私鑰
openssl genrsa -out private.pem 2048

# 從私鑰產生公鑰
openssl rsa -in private.pem -pubout -out public.pem

# 提取私鑰的 Base64 編碼並移除 PEM 標頭
openssl rsa -in private.pem -outform DER | base64

# 提取公鑰的 Base64 編碼並移除 PEM 標頭
openssl rsa -in public.pem -pubin -outform DER | base64
```

**重要提醒**：
- fin.rsaUtil 金鑰對的私鑰必須與前端所使用的 RSA 公鑰配對。若金鑰對不一致，則無法解密前端加密內容。
- 絕對不要將實際的私鑰提交到版本控制系統
- 建議在部署時透過環境變數或機密管理系統注入
- 定期輪替金鑰以維護系統安全性

### API 文件

#### 主要端點

##### 管理端點

所有管理端點位於 `/management` 路徑下：

| 端點 | 說明 | 權限需求 |
|------|------|---------|
| `/management/health` | 健康檢查 | 公開 |
| `/management/info` | 應用程式資訊 | 公開 |
| `/management/metrics` | 效能指標 | ADMIN |
| `/management/prometheus` | Prometheus 格式指標 | ADMIN |
| `/management/loggers` | 日誌層級管理 | ADMIN |
| `/management/liquibase` | 資料庫遷移狀態 | ADMIN |

##### 業務 API

核心業務 API 位於 `/api` 路徑下：

| 端點分類 | 說明 | 範例路徑 |
|---------|------|---------|
| VC 建立管理 | 憑證模板 CRUD | `/api/vc-items/*` |
| VC 資料狀態管理 | 憑證資料狀態管理 | `/api/vc-item-data/*` |
| 組織管理 | 發行機關組織管理 | `/api/modadworg/*` |
| 帳號管理 | 帳號與權限管理 | `/api/modadw311w/*` |

**注意事項**：
- 所有業務 API 都需要有效的 JWT Token

### 資料庫遷移

本專案使用 Liquibase 進行資料庫版本控制。

#### 資料庫變更日誌位置

```
src/main/resources/config/liquibase/
├── master.xml
└── changelog/
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
│   ├── java/gov/moda/dw/manager/
│   │   ├── annotation/                 # 自定義 Annotation
│   │   ├── aop/                        # AOP 切面
│   │   │   └── logging/                # 日誌記錄切面
│   │   ├── config/                     # Spring 設定類別
│   │   ├── domain/                     # JPA 實體類別
│   │   ├── job/                        # 排程相關（Quartz Job）
│   │   ├── management/                 # 管理端點與 Actuator 相關（/management 端點、監控面板）
│   │   ├── monitor/                    # 應用層監控實作（自訂健康檢查、健康擴充、Prometheus/metrics 整合）
│   │   ├── repository/                 # 資料存取層
│   │   ├── schedule/                   # 排程相關（Spring Schedule）
│   │   ├── security/                   # 安全性相關
│   │   ├── service/                    # 業務邏輯層
│   │   ├── type/                       # Enum 相關
│   │   ├── util/                       # 共用工具類別
│   │   └── web/rest/                   # REST API 控制器
│   └── resources/
│       ├── config/                     # 設定檔
│       │   ├── application.yml
│       │   └── liquibase/              # 資料庫遷移腳本
│       ├── fonts/                      # 字型相關
│       ├── i18n/                       # 國際化資源
│       ├── img/                        # 圖片相關
│       ├── templates/                  # 範本檔案
│       ├── banner.txt
│       └── logback-spring.xml          # 日誌設定
└── test/                               # 測試程式碼
```

#### 程式碼風格規範

- 遵循 Java 編碼規範（Java Code Conventions）
- 使用有意義的變數與方法命名
- 類別名稱使用 PascalCase
- 方法名稱使用 camelCase
- 常數名稱使用 UPPER_SNAKE_CASE
- 所有公開的類別和方法都必須有 JavaDoc 註解
- 註解使用繁體中文，符合本地用語習慣

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
- [Liquibase](https://www.liquibase.org/)
- [MapStruct](https://mapstruct.org/)
