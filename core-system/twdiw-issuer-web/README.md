# 數位發展部數位憑證皮夾發行端前端管理介面

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Node.js Version](https://img.shields.io/badge/node-%5E20%20%7C%7C%20%5E18%20%7C%7C%20%5E16-blue.svg)](https://nodejs.org/)
[![Vue.js Version](https://img.shields.io/badge/vue-3.4.18-brightgreen.svg)](https://vuejs.org/)
[![Quasar Version](https://img.shields.io/badge/quasar-2.8.0-1976D2.svg)](https://quasar.dev/)

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
  - [安裝相依套件](#3-安裝相依套件)
  - [編譯與執行](#4-編譯與執行)
  - [驗證服務運行](#5-驗證服務運行)
- [設定說明](#設定說明)
  - [主要設定檔](#主要設定檔)
  - [關鍵設定項目](#關鍵設定項目)
- [開發指南](#開發指南)
  - [專案結構](#專案結構)
  - [程式碼風格規範](#程式碼風格規範)
- [部署說明](#部署說明)
  - [Docker 部署](#docker-部署)
  - [Nginx 設定](#nginx-設定)
- [支援與聯絡](#支援與聯絡)
- [變更日誌](#變更日誌)
- [致謝](#致謝)

---

### 專案簡介

本專案為數位發展部數位憑證皮夾之**發行端前端管理介面**，此服務部署於憑證發行機關的環境中，提供完整的憑證發行管理功能，讓憑證發行機關能夠透過視覺化介面進行數位憑證的建立、管理與狀態控制。

### 主要功能

- **DID 管理**

  - DID 註冊與設定
  - 組織標誌（Logo）上傳與管理

- **VC 憑證範本管理**

  - VC Schema 建立與編輯
  - 欄位定義與驗證規則設定
  - 正規表示式管理
  - 範本啟用/停用控制

- **VC 資料狀態管理**

  - VC 撤銷（Revoke）
  - VC 停用（Suspend）
  - VC 復用（Resume）
  - VC 狀態查詢與檢視

- **系統管理**

  - 帳號管理與權限控制
  - 角色與功能管理
  - 組織資訊管理
  - Access Token 管理
  - 變更審核機制（帳號/角色/功能變更審核）
  - 正規表示式管理
  - 密碼重設功能

- **安全性功能**
  - 使用者登入與 OTP 驗證
  - 密碼加密傳輸（RSA）
  - JWT Token 自動更新
  - 完整的權限控制機制

### 技術架構

#### 核心技術

| 技術             | 版本                  | 說明                        |
| ---------------- | --------------------- | --------------------------- |
| Vue.js           | 3.4.18                | 前端框架（Composition API） |
| Quasar Framework | 2.8.0                 | UI 元件庫與 CLI 工具        |
| Pinia            | 2.0.11                | 狀態管理                    |
| Vue Router       | 4.0.12                | 路由管理                    |
| Vue I18n         | 10.0.6                | 國際化支援                  |
| Vite             | 2.9.18                | 建置工具與開發伺服器        |
| Axios            | 1.12.2                | HTTP 客戶端                 |
| Node.js          | ^20 \|\| ^18 \|\| ^16 | 執行環境                    |

#### 主要相依套件

| 套件名稱              | 版本     | 用途                          |
| --------------------- | -------- | ----------------------------- |
| @quasar/extras        | ^1.16.14 | Quasar 額外資源（圖示、字型） |
| @formkit/auto-animate | ^0.8.2   | 輕量動畫支援                  |
| js-sha512             | ^0.9.0   | SHA-512 雜湊處理              |
| jsencrypt             | ^3.3.2   | RSA 加密處理                  |

備註：

- 表格中的版本前綴 caret（^）是 npm 的版本範圍寫法，表示允許自動安裝同一主版本（major）內的新次要或修補版本。例如 ^1.2.3 可匹配 1.2.4 或 1.3.0，但不會自動升級到 2.0.0。

### 系統需求

#### 開發環境需求

- **作業系統**：Windows、macOS 或 Linux
- **Node.js**：^20 / ^18 / ^16
- **套件管理工具**：Yarn ≥ 1.21.1 或 npm ≥ 6.13.4
- **建議安裝**：Quasar CLI（`npm install -g @quasar/cli`）

#### 執行環境需求

- **瀏覽器支援**：
  - Chrome 87+
  - Firefox 78+
  - Safari 13.1+
  - Edge 88+
- **記憶體**：建議至少 4GB RAM
- **網路**：需連線至後端 API 服務

### 快速開始

#### 1. 取得原始碼

```bash
git clone https://github.com/moda-gov-tw/TWDIW-official-app.git
cd TWDIW-official-app/core-system/twdiw-issuer-web
```

#### 2. 設定環境變數

本專案使用 Vite，因此所有前端環境變數需以 `VITE_` 前綴定義，並由 `quasar.config.js` 注入應用程式。

**重要說明**：

- 專案預設將前端環境變數集中定義於 `quasar.config.js`
- 開發時 **不一定需要建立** `.env` 檔案
- 所有環境變數皆為 建置期設定（build-time），修改後需重新執行 npm run build

| 環境變數名稱          | 說明                                 | 設定位置                      |
| --------------------- | ------------------------------------ | ----------------------------- |
| `VITE_BASE_PATH`      | 應用程式的基礎路徑（若部署於子路徑） | `quasar.config.js` 或環境變數 |
| `VITE_RSA_PUBLIC_KEY` | 前端使用的 RSA 公鑰（用於密碼加密）  | `quasar.config.js` 或環境變數 |

**RSA 公鑰設定注意事項**：

- 僅能使用 純 Base64 字串
- 不可包含以下內容：
  - `-----BEGIN PUBLIC KEY-----`
  - `-----END PUBLIC KEY-----`
  - 換行字元
- 前端程式會於執行時自動將 Base64 字串組裝為 PEM 格式，以供 crypto.subtle 使用

**RSA 金鑰產生範例**：

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

#### 安全性注意事項：

- 公鑰必須與後端所使用的 RSA 私鑰配對。若金鑰對不一致，後端無法解密前端加密內容。
- 建議在部署時透過環境變數或機密管理系統注入。
- 定期輪替金鑰以維護系統安全性。

#### 若需自訂環境變數，可選擇以下方式之一：

**方式 1：透過指令行注入**（建議用於 CI / Docker）

```
VITE_BASE_PATH=/issuermgr/ \
VITE_RSA_PUBLIC_KEY=MIIBIjANBgkq... \
npm run build
```

**方式 2：使用 `.env` 檔案**（開發環境）

#### 於專案根目錄建立 .env 檔案：

```
VITE_BASE_PATH=/issuermgr/
VITE_RSA_PUBLIC_KEY=MIIBIjANBgkq...
```

**注意**：

- 開發環境的後端 API proxy 設定位於 `quasar.config.js` 的 `devServer.proxy` 區段
- 生產環境的 API 路由由 Nginx 反向代理處理（參考 `docker/default.conf`）
- 環境變數為 建置期設定（build-time），部署後若需修改，必須重新執行建置流程

#### 3. 安裝相依套件

##### 使用 Yarn（建議）

```bash
yarn install
```

##### 使用 npm

```bash
npm install
```

#### 4. 編譯與執行

##### A. 開發模式（Hot Reload）

使用 Yarn：

```bash
yarn dev
```

使用 npm：

```bash
npm run dev
```

**注意**：

- 開發模式下，系統預設會在 `http://localhost:9000` 啟動前端開發伺服器。
- 若 9000 已被占用，系統會自動使用下一個可用的連接埠（如 9001、9002 等）。
- 實際使用的連接埠會在啟動時於終端機輸出提示。

##### B. 生產建置

使用 Yarn：

```bash
yarn build
```

使用 npm：

```bash
npm run build
```

建置輸出位置：

```
dist/spa/
```

##### C. 開發環境建置

若需要產生開發版本的建置檔案：

使用 Yarn：

```bash
yarn build:dev
```

使用 npm：

```bash
npm run build:dev
```

**Windows 使用者注意**：

CMD：

```bash
set BUILDENVIROMENT=DEV && npm run build
```

PowerShell：

```powershell
$env:BUILDENVIROMENT="DEV"; npm run build
```

#### 5. 驗證服務運行

開發伺服器啟動後，開啟瀏覽器連線至：

```
http://localhost:9000
```

您應該會看到登入頁面。

### 設定說明

#### 主要設定檔

專案的主要設定檔位於以下位置：

- `quasar.config.js`：Quasar 與 Vite 建置設定
- `src/router/index.js`：路由設定
- `src/router/routes.js`：路由定義
- `src/i18n/`：多語系資源檔

#### 關鍵設定項目

##### 應用程式基本資訊

在 `package.json` 中定義：

```json
{
  "name": "moda-digitalwallet-issuer-manager-web",
  "version": "0.0.1",
  "productName": "數位憑證皮夾-發行端模組系統"
}
```

##### 建置設定

在 `quasar.config.js` 中設定：

```javascript
module.exports = configure(function (ctx) {
  return {
    build: {
      env: {
        VITE_BASE_PATH: PATH,
        VITE_RSA_PUBLIC_KEY: RSA_PUBLIC_KEY
        // ... 其他環境變數
      }
    },

    devServer: {
      port: 9000,
      open: true
      // proxy 設定請根據實際後端服務位置調整
    }
  };
});
```

##### 開發伺服器 Proxy 設定

開發時若需要連接本地後端服務，請在 `quasar.config.js` 取消註解並調整 proxy 設定：

```javascript
devServer: {
  port: 9000,
  open: true,
  proxy: {
    "/api": {
      target: "http://localhost:8080/",
      changeOrigin: true
    },
    "/management": {
      target: "http://localhost:8080/",
      changeOrigin: true
    }
  }
}
```

##### Axios 設定

全域 Axios 設定位於 `src/boot/axios.js`，包含：

- JWT Token 自動注入
- Token 自動更新機制
- 錯誤處理與重導向
- 請求/回應攔截器

##### 路由設定

路由定義位於 `src/router/routes.js`，主要路由包括：

- `/`：登入頁面
- `/modadw/*`：系統管理功能
- `/dw/*`：DID 與組織管理
- `/vc/*`：VC 欄位管理
- `/vcSchema/*`：VC Schema 管理

### 開發指南

#### 專案結構

以下為專案的主要目錄結構（**簡化示例**，實際專案包含更多檔案與目錄）：

```
twdiw-issuer-web/
├── docker/                      # Docker 部署設定
│   ├── default.conf             # Nginx 設定檔
│   └── 403.html                 # 錯誤頁面
├── public/                      # 靜態資源
├── src/                         # 原始碼
│   ├── assets/                  # 靜態資源（圖片、字型、SVG）
│   │   ├── js/                  # JavaScript 工具類別
│   │   ├── *.svg                # SVG 圖檔
│   │   └── *.png                # 圖片檔案
│   ├── boot/                    # Boot 檔案（初始化模組）
│   │   ├── auth.js              # 認證模組
│   │   ├── axios.js             # Axios 設定
│   │   └── i18n.js              # 國際化設定
│   ├── components/              # 共用元件
│   │   ├── ActionDropdown.vue   # 動作下拉選單
│   │   ├── DataTable.vue        # 資料表格
│   │   ├── ConfirmDialog.vue    # 確認對話框
│   │   └── ...
│   ├── i18n/                    # 多語系資源
│   │   ├── zh-TW/               # 繁體中文
│   │   │   ├── common.js        # 共用詞彙
│   │   │   ├── errorCode.js     # 錯誤訊息
│   │   │   └── ...
│   │   └── en-US/               # 英文（預留）
│   ├── layouts/                 # 版面配置
│   │   ├── AuthLayout.vue       # 認證頁面版面
│   │   └── MainLayout.vue       # 主要頁面版面
│   ├── pages/                   # 頁面元件
│   │   ├── auth/                # 認證相關頁面
│   │   ├── account/             # 帳號管理
│   │   ├── role/                # 角色管理
│   │   ├── func/                # 功能管理
│   │   ├── org/                 # 組織管理
│   │   ├── issuer/              # DID 與組織設定
│   │   ├── fieldsR/             # VC 欄位管理
│   │   ├── vcSchemaR/           # VC Schema 管理
│   │   ├── trace/               # 稽核追蹤
│   │   ├── schedule/            # 排程管理
│   │   └── ...
│   ├── router/                  # 路由設定
│   │   ├── index.js             # 路由器實例
│   │   └── routes.js            # 路由定義
│   ├── stores/                  # Pinia 狀態管理
│   │   ├── userConfig.js        # 使用者設定
│   │   ├── login.js             # 登入狀態
│   │   ├── account.js           # 帳號管理
│   │   ├── role.js              # 角色管理
│   │   ├── vcSchema.js          # VC Schema
│   │   └── ...
│   ├── utils/                   # 工具函式
│   │   ├── encrypt.js           # RSA 加密
│   │   ├── query.js             # 查詢參數處理
│   │   ├── plugin.js            # 外掛工具
│   │   └── ...
│   ├── App.vue                  # 根元件
│   └── css/                     # 樣式檔案
├── quasar.config.js             # Quasar 設定
├── package.json                 # 專案設定
├── jsconfig.json                # JavaScript 設定
└── README.md                    # 專案說明文件
```

#### 程式碼風格規範

- **Vue 元件**：使用 Composition API
- **命名慣例**：
  - 元件檔案：PascalCase（如 `DataTable.vue`）
  - 工具函式：camelCase（如 `formatDate`）
  - 常數：UPPER_SNAKE_CASE（如 `API_BASE_URL`）
- **程式碼格式化**：使用 Prettier
  ```bash
  npm run format
  ```
- **程式碼檢查**：使用 ESLint
  ```bash
  npm run lint
  ```

### 部署說明

#### Docker 部署

專案包含 Docker 部署所需的 Nginx 設定檔（`docker/default.conf`）。

#### Nginx 設定

Nginx 設定檔提供以下功能：

1. **靜態檔案服務**：提供建置後的 SPA 檔案
2. **反向代理**：將 API 請求轉發至後端服務
3. **安全標頭**：設定 X-Frame-Options、CSP 等安全標頭
4. **GZIP 壓縮**：啟用靜態資源壓縮

主要設定說明：

```nginx
# 前端靜態檔案
location /issuermgr/ {
    alias  /usr/share/nginx/html/;
    index  index.html index.htm;
    try_files $uri $uri/ /issuermgr/index.html;
}

# 後端 API 代理
location /issuermgr/api/ {
    proxy_pass http://moda-digitalwallet-docker-issuer-manager-service:8080/api/;
    # ... proxy 設定
}
```

**注意**：

- 內部服務名稱（如 `moda-digitalwallet-docker-issuer-manager-service`）需根據實際 Docker Compose 設定調整
- 若修改 `VITE_BASE_PATH`，需同步更新 Nginx 設定中的 location 路徑

#### 建置步驟

1. 建置前端靜態檔案：

   ```bash
   npm run build
   ```

2. 將 `dist/spa/` 目錄內容複製到容器的 `/usr/share/nginx/html/`

3. 使用 `docker/default.conf` 作為 Nginx 設定檔

### 支援與聯絡

- **官方網站**：[https://wallet.gov.tw](https://wallet.gov.tw)
- **技術支援信箱**：service@wallet.gov.tw
- **問題回報**：[GitHub Issues](https://github.com/moda-gov-tw/TWDIW-official-app/issues)

### 變更日誌

詳細的變更歷史記錄請參閱 [CHANGELOG.md](CHANGELOG.md)

### 致謝

本專案使用以下優秀的開源專案：

- [Vue.js](https://vuejs.org/) - 漸進式 JavaScript 框架
- [Quasar Framework](https://quasar.dev/) - Vue.js 跨平台 UI 框架
- [Vite](https://vitejs.dev/) - 次世代前端建置工具
- [Axios](https://axios-http.com/) - Promise based HTTP 客戶端
- [Pinia](https://pinia.vuejs.org/) - Vue.js 狀態管理
