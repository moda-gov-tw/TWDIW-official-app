# 數位憑證皮夾開源專案

數位發展部公開數位憑證皮夾相關原始碼，期望透過公私協力推動以 OID4VC / OID4VP 為核心的可信賴身份服務。本文件提供專案概況、元件架構與合規事項，並說明各子專案閱讀路徑，協助開發者依循統一標準進行開發與貢獻。

## 專案概覽

此儲存庫預計包含以下元件，並陸續釋出程式碼：

| 項次 | 元件名稱 | 內容摘要 | 子目錄 / 文件 |
|------|----------|----------|----------------|
| 1 | 數位憑證皮夾 Android App | 原生 Android 應用程式，整合 APPSDK 與模組化服務。 | 即將公開 |
| 2 | 數位憑證皮夾 iOS App | 原生 iOS 應用程式，對應 iOS 端整合與部署流程。 | 即將公開 |
| 3 | 行動端 App SDK (Flutter) | 已釋出的 APPSDK，可產出 Android AAR / iOS Framework。 | `APP/APPSDK/README.md` |
| 4 | 數位憑證皮夾核心系統 Parent POM | 提供所有核心模組共用的 Maven Parent 設定、版本治理與品質規則。 | `core-system/moda-digitalwallet-parent-pom/README.md` |
| 5 | 數位憑證皮夾共用函式庫 | 提供 Actuator 建置資訊輸出與日誌記錄的共用 Spring Boot 套件。 | `core-system/moda-digitalwallet-common/README.md` |
| 6 | 發行端 OID4VCI 服務 | 部署於發行機關，處理 OID4VCI 發卡與驗證流程。 | `core-system/twdiw-oid4vci-handler/README.md` |
| 7 | 驗證端 OID4VP 服務 | 部署於驗證機關，支援 OID4VP 流程與 Presentation 驗證。 | `core-system/twdiw-oid4vp-handler/README.md` |
| 8 | 發證機關 VC 服務 | 提供 VC 簽發、狀態清冊、DID 管理等功能。 | `core-system/twdiw-vc-handler/README.md` |
| 9 | 驗證端 VP 服務 | 驗證 VP/VC 與 SD-JWT，支援選擇性揭露。 | `core-system/twdiw-vp-handler/README.md` |
| 10 | 共用服務與工具 | 共用函式庫、範例資料與文件。 | 依子目錄說明，例如 `Docs/`、`doctemplate/`、`SampleCode/` |

> 各子專案的詳細使用方式請直接閱讀對應目錄下的 `README.md`。本檔案僅保留高層級資訊與合規指引。

### 核心目標

- 建置符合國際標準的行動憑證載具，支援 OID4VC、OID4VP 與 DID 等規範。
- 協助開發者擴充介面與驗證流程，滿足不同應用情境需求。
- 以 MIT License 授權，讓政府及民間單位可在合規前提下延伸或再利用。

### 專案特色

- **安全機制**：強化憑證保護、金鑰管理與雙向驗證，對應政府信任架構。
- **國際相容**：遵循主要開放標準，提升跨國互通能力。
- **治理文件完備**：搭配 `CONTRIBUTING.md`、`CODE_OF_CONDUCT.md` 等文件，確保協作流程透明。

## 資料夾結構速覽

```
.
├── APP/               # 行動端資源
│   └── APPSDK/        # Flutter SDK 主體與編譯指引（詳見子目錄 README）
├── Docs/              # 技術文件與架構說明
├── SampleCode/        # 對應文件所引用的程式範例
├── core-system/       # 核心系統模組（Parent POM、共用函式庫與各服務）
│   ├── moda-digitalwallet-parent-pom/  # Maven Parent POM 設定
│   ├── moda-digitalwallet-common/      # Spring Boot 共用函式庫
│   ├── twdiw-oid4vci-handler/          # 發行端 OID4VCI 服務
│   ├── twdiw-oid4vp-handler/           # 驗證端 OID4VP 服務
│   ├── twdiw-vc-handler/               # 發證機關 VC 服務
│   └── twdiw-vp-handler/               # 驗證端 VP 服務
├── .github/           # GitHub Issue / PR 模板（自動套用）
├── images/            # 參考示意圖與素材
├── CONTRIBUTING.md    # 貢獻流程
├── CODE_OF_CONDUCT.md # 行為準則
├── CLA.md             # 貢獻者授權協議
└── LICENSE.txt        # MIT 授權條款
```

> `.github/` 目錄已包含 Issue 模板與 PR 模板，將由 GitHub 自動套用。

## 快速開始

1. 使用 `git clone` 取得儲存庫程式碼。
2. 依需求進入對應子目錄（例如 `core-system/*`、`APP/APPSDK/`），閱讀該資料夾下的 `README.md`，以取得詳細安裝、編譯與部署步驟。
3. 執行測試或驗證時，請遵循各子專案提供的測試指令；若有額外測試策略，可於 PR 中一併提出。

## 開源合規與貢獻守則

1. **授權**：本專案採用 MIT License，重用或修改時請保留原授權聲明。（`LICENSE.txt`）
2. **行為準則**：所有貢獻者應遵守 `CODE_OF_CONDUCT.md`，共同維護專業與友善環境。
3. **貢獻者授權**：提交 PR 前請確認已閱讀並同意 `CLA.md`。
4. **提交流程**：
   - 建立 Issue 討論需求或回報問題。
   - Fork 或建立分支，並依子專案指引完成修正與測試。
   - 提交 PR，說明修改內容、測試結果與受影響子模組。
5. **安全回報**：如發現資安問題，請透過私密 Issue 或電子郵件聯繫官方窗口（`service@wallet.gov.tw`）。

## 常見問題

- **主 App 為何尚未開源？**  
  相關程式仍在整備與內部審查中，待安全檢核與法規程序完成後將另行公告。

- **如何掌握 Roadmap？**  
  建議追蹤 GitHub Issue 與 Discussions。若有建議事項，也可提出 Issue 供討論。

## 聯絡與支援

- **Issue Tracker**：請透過 GitHub Issue 回報問題，以利紀錄與追蹤。
- **討論串**：如啟用 Discussions，歡迎交流建議或技術議題。
- **Email**：若含機敏資訊，請以電子郵件聯繫官方窗口（`service@wallet.gov.tw`）。
