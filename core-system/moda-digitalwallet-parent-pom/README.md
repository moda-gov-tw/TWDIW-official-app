# 數位發展部數位憑證皮夾 Parent POM

[![Maven Central](https://img.shields.io/badge/maven-1.0.0-blue.svg)](https://search.maven.org/)
[![Java](https://img.shields.io/badge/Java-17%20%7C%2018%20%7C%2019%20%7C%2020%20%7C%2021-blue.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.13-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

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
  - [編譯並安裝](#2-編譯並安裝)
  - [驗證安裝結果](#3-驗證安裝結果)
- [設定說明](#設定說明)
  - [主要設定檔](#主要設定檔)
  - [版本與套件管理](#版本與套件管理)
- [在子專案中使用](#在子專案中使用)
  - [繼承設定範例](#繼承設定範例)
  - [常用插件啟用](#常用插件啟用)
- [建置與品質工具](#建置與品質工具)
  - [常用指令](#常用指令)
  - [程式碼品質檢查](#程式碼品質檢查)
  - [版本管理](#版本管理)
- [開發指南](#開發指南)
  - [專案結構](#專案結構)
  - [程式碼風格規範](#程式碼風格規範)
- [疑難排解](#疑難排解)
- [支援與聯絡](#支援與聯絡)
- [變更日誌](#變更日誌)
- [授權](#授權)
- [致謝](#致謝)

### 專案簡介

本專案為數位發展部數位憑證皮夾（Digital Identity Wallet）系列的 **Maven Parent POM**，提供統一的依賴版本、建置流程與程式碼品質設定，讓各子專案能以一致標準進行開發與發佈。

### 主要功能

- **依賴與外掛版本治理**：集中管理 Spring Boot、Liquibase、MapStruct 等主要套件版本
- **建置流程一致化**：預先設定常用的 Maven 外掛與建置參數
- **程式碼品質控管**：整合 Spotless、Checkstyle、NoHTTP 與 Google Java Format
- **版本資訊追蹤**：透過 Git Commit ID Plugin 產生 `git.properties`
- **Docker 映像建置支援**：預設配置 Spring Boot Build Image 參數，方便子專案直接使用

### 技術架構

#### 核心技術

| 技術 | 版本 | 說明 |
|------|------|------|
| Java | 17 | 開發與建置環境 |
| Spring Boot | 3.3.13 | 作為 Parent 來源並管理 Spring 依賴 |
| Maven | 3.2.5+ | 建置與依賴管理工具 |
| JHipster | 8.5.0 | 版本對應設定參考 |

#### 主要相依套件

| 套件 / 外掛 | 版本 | 用途 |
|-------------|------|------|
| Liquibase | 4.32.0 | 資料庫版本控管 |
| MapStruct | 1.5.5.Final | 物件對映 |
| Spotless Maven Plugin | 2.43.0（內含 Google Java Format 1.17.0） | 程式碼格式化 |
| Checkstyle | 10.17.0（NoHTTP 0.0.11） | 風格檢查與 HTTP 連結掃描 |
| Git Commit ID Maven Plugin | 8.0.2 | 產生 Git 版本資訊 |
| Springdoc OpenAPI | 2.5.0 | API 文件支援（子專案可套用） |

### 系統需求

#### 開發環境需求

- **JDK**：17（建議使用 Eclipse Temurin 發行版）
- **Apache Maven**：3.2.5 或以上
- **作業系統**：Windows、macOS、Linux（任一支援 JDK 17 的平台）

#### 執行環境需求

本專案為 Parent POM，不直接產出可執行程式，無額外執行環境需求。

### 快速開始

#### 1. 取得原始碼

```bash
git clone https://github.com/moda-gov-tw/TWDIW-official-app.git
cd TWDIW-official-app/core-system/moda-digitalwallet-parent-pom
```

#### 2. 編譯並安裝

```bash
mvn clean install
```

#### 3. 驗證安裝結果

確認本地 Maven 儲存庫已產生對應版本：

```bash
ls ~/.m2/repository/gov/moda/dw/moda-digitalwallet-parent/1.0.0
```

若有出現 `moda-digitalwallet-parent-1.0.0.pom` 即代表安裝成功。

### 設定說明

#### 主要設定檔

- `pom.xml`：Parent POM 主要設定與版本管理
- `settings.xml.example`：示範 Maven 設定檔，可依需求複製為 `~/.m2/settings.xml`
- `checkstyle.xml`、`google-java-format.xml`：程式碼風格與格式化規則

#### 版本與套件管理

- `properties` 區段統一管理 Java、Spring Boot、JHipster 以及各外掛版本
- 預設啟用 UTF-8 編碼並鎖定編譯目標為 Java 17
- 透過 `dependencyManagement` 提供 Liquibase 核心套件版本，子專案直接引用即可

### 在子專案中使用

#### 繼承設定範例

在子專案的 `pom.xml` 中加入以下設定即可繼承本 Parent POM：

```xml
<parent>
    <groupId>gov.moda.dw</groupId>
    <artifactId>moda-digitalwallet-parent</artifactId>
    <version>1.0.0</version>
</parent>
```

#### 常用插件啟用

子專案可視需求啟用已在 `pluginManagement` 宣告的外掛，例如 Spring Boot Maven Plugin：

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

### 建置與品質工具

#### 常用指令

```bash
# 清理與編譯
mvn clean compile

# 打包與安裝（Parent POM 會安裝 POM 至本地倉庫）
mvn clean install
```

#### 程式碼品質檢查

```bash
# 自動修正程式碼格式
mvn spotless:apply

# 僅檢查格式
mvn spotless:check

# 檢查檔案中是否含有 HTTP 連結等風格問題
mvn checkstyle:check
```

### 開發指南

#### 專案結構

```
moda-digitalwallet-parent-pom/
├── pom.xml                   # Parent POM 主設定
├── checkstyle.xml            # Checkstyle 規則
├── google-java-format.xml    # Google Java Format 設定
├── settings.xml.example      # Maven 設定範本
├── LICENSE
└── README.md
```

#### 程式碼風格規範

- 全專案採用 **UTF-8** 編碼與 Java 17 編譯目標
- 依照 **Google Java Style**，並以 Spotless 自動套用
- Checkstyle 強制檢查外部連結需使用 HTTPS，若連結不支援請於規則中列入例外

### 疑難排解

- **Maven 版本過舊**：請升級至 3.2.5 以上再建置
- **Java 版本不符**：請確認 `java -version` 為 17，並更新 `JAVA_HOME`
- **Checkstyle 失敗**：多半因為存在 `http://` 連結或未符合格式，請改用 HTTPS 或依規則修正

### 支援與聯絡

- **官方網站**：[https://wallet.gov.tw](https://wallet.gov.tw)
- **技術支援信箱**：service@wallet.gov.tw
- **問題回報**：[GitHub Issues](https://github.com/moda-gov-tw/TWDIW-official-app/issues)

### 變更日誌

詳細的變更歷史記錄請參閱 [CHANGELOG.md](CHANGELOG.md)

### 授權

本專案採用 **MIT License**，詳情請見 `LICENSE`。

### 致謝

感謝所有參與開發與維運的團隊，以及以下開源專案的支持：

- [Spring Boot](https://spring.io/projects/spring-boot)
- [JHipster](https://www.jhipster.tech/)
- [Liquibase](https://www.liquibase.org/)
- [MapStruct](https://mapstruct.org/)
- [Checkstyle](https://checkstyle.org/)
- [Spotless](https://github.com/diffplug/spotless)
