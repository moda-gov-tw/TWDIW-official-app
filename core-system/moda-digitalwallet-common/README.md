# 數位發展部數位憑證皮夾共用函式庫

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Version](https://img.shields.io/badge/version-1.0.0--SNAPSHOT-green.svg)](pom.xml)

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
  - [編譯套件](#2-編譯套件)
  - [安裝至本機倉庫](#3-安裝至本機倉庫)
- [使用方式](#使用方式)
  - [加入相依性](#1-加入相依性)
  - [產生建置資訊](#2-產生建置資訊)
  - [啟用 Actuator 端點](#3-啟用-actuator-端點)
  - [查詢應用程式資訊](#4-查詢應用程式資訊)
- [設定說明](#設定說明)
  - [Actuator 設定重點](#actuator-設定重點)
  - [元件載入方式](#元件載入方式)
- [開發指南](#開發指南)
  - [專案結構](#專案結構)
  - [擴充自訂欄位](#擴充自訂欄位)
  - [日誌紀錄行為](#日誌紀錄行為)
- [支援與聯絡](#支援與聯絡)
- [變更日誌](#變更日誌)
- [致謝](#致謝)

### 專案簡介

本專案為數位發展部數位憑證皮夾核心系統的 **共用函式庫**，提供跨服務可重用的 Spring Boot Actuator `InfoContributor`，協助輸出應用程式建置資訊並同步紀錄於日誌，方便營運監控與版本追蹤。

### 主要功能

- **建置資訊輸出**：在 `/actuator/info` 端點提供版本、名稱、群組、建置時間等欄位
- **日誌紀錄**：存取 `/actuator/info` 時自動將建置資訊寫入日誌，便於追蹤
- **客製欄位**：可依需求延伸額外欄位，適合各微服務共用

### 技術架構

#### 核心技術

| 技術 | 版本 | 說明 |
|------|------|------|
| Java | 17+ | 執行環境 |
| Spring Boot | 3.x | 應用程式框架 |
| Maven | 3.6+ | 專案建置與相依性管理 |

#### 主要相依套件

| 套件名稱 | 用途 |
|----------|------|
| `spring-boot-starter-actuator` | 監控與管理端點 |
| `spring-boot-starter` | Spring Boot 核心啟動器 |
| `slf4j-api` | 日誌介面 |

### 系統需求

#### 開發環境需求

- **JDK**：OpenJDK 17 或以上（建議使用 Eclipse Temurin）
- **Maven**：3.6 或以上
- **作業系統**：macOS、Windows 或 Linux

#### 執行環境需求

- **JRE**：OpenJDK 17 或以上
- **記憶體**：建議至少 512MB
- **磁碟空間**：建議至少 200MB 可用空間

### 快速開始

#### 1. 取得原始碼

```bash
git clone https://github.com/moda-gov-tw/TWDIW-official-app.git
cd TWDIW-official-app/core-systems/moda-digitalwallet-common
```

#### 2. 編譯套件

```bash
mvn clean package -DskipTests
```

完成後產出 `target/moda-digitalwallet-common-1.0.0-SNAPSHOT.jar`。

#### 3. 安裝至本機倉庫

```bash
mvn clean install -DskipTests
```

### 使用方式

#### 1. 加入相依性

在目標專案的 `pom.xml` 加入：

```xml
<dependency>
  <groupId>gov.moda.dw</groupId>
  <artifactId>moda-digitalwallet-common</artifactId>
  <version>1.0.0-SNAPSHOT</version>
</dependency>
```

若尚未上傳至共用套件庫，請先執行 `mvn clean install` 將套件安裝到本機 Maven 倉庫。

#### 2. 產生建置資訊

`InfoContributor` 依賴 `build-info.properties`。請在目標專案的 `pom.xml` 啟用：

```xml
<build>
  <plugins>
    <plugin>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-maven-plugin</artifactId>
      <executions>
        <execution>
          <goals>
            <goal>build-info</goal>
          </goals>
        </execution>
      </executions>
    </plugin>
  </plugins>
</build>
```

#### 3. 啟用 Actuator 端點

在 `application.yml` 打開 `info` 與 `health` 端點並啟用建置資訊：

```yaml
management:
  endpoints:
    web:
      exposure:
        include: info,health
  info:
    build:
      enabled: true
```

#### 4. 查詢應用程式資訊

啟動服務後，透過下列端點取得建置資訊並觸發日誌紀錄：

```
GET http://localhost:8080/actuator/info
```

回應範例：

```json
{
  "build": {
    "artifact": "your-app",
    "name": "your-app",
    "group": "gov.moda.dw",
    "version": "1.0.0",
    "time": "2025-10-01T08:00:00.000Z"
  }
}
```

### 設定說明

#### Actuator 設定重點

- `management.info.build.enabled=true` 為必填，否則建置欄位不會產生。
- 需確保 `build-info.properties` 已在編譯時生成，否則 Actuator 不會帶出建置欄位。

#### 元件載入方式

- 將 `gov.moda.dw.monitor.info` 納入元件掃描範圍，或在設定類別上使用 `@Import(LoggingAppInfoContributorConfiguration.class)`。

### 開發指南

#### 專案結構

```
src/
└── main/
    └── java/gov/moda/dw/monitor/info/
        ├── LoggingAppInfoContributor.java           # InfoContributor 實作
        └── LoggingAppInfoContributorConfiguration.java # Bean 組態
pom.xml
README.md
```

#### 擴充自訂欄位

在 `LoggingAppInfoContributor#contribute` 中追加欄位即可，例如：

```java
build.put("api-version", "v2");
build.put("environment", "production");
```

#### 日誌紀錄行為

當 `/actuator/info` 被呼叫時，會於日誌輸出建置資訊，方便比對部署版本與時點。

### 支援與聯絡

- **官方網站**：[https://wallet.gov.tw](https://wallet.gov.tw)
- **技術支援信箱**：service@wallet.gov.tw
- **問題回報**：[GitHub Issues](https://github.com/moda-gov-tw/TWDIW-official-app/issues)

### 變更日誌

目前未額外維護獨立的 `CHANGELOG.md`，請參考 Git 提交紀錄了解版本變更。

### 致謝

- [Spring Framework](https://spring.io/)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/reference/actuator/index.html)
- [SLF4J](https://www.slf4j.org/)
