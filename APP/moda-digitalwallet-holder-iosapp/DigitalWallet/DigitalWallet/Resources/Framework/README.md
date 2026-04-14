# Framework 說明

本目錄包含以下預編譯的 Framework：

## 1. moda_digitalwallet_holder_sdk.framework
- **用途**: 數位憑證皮夾核心 SDK
- **版本**: 請參考 Info.plist
- **說明**: 此為預編譯的二進位 framework，包含數位憑證相關的核心功能
- **注意**:
  - 此 framework 為編譯後的二進位檔案
  - 如需原始碼或最新版本，請聯繫專案維護者
  - 使用此 SDK 前請確認授權條款

## 2. App.xcframework
- **用途**: Flutter 應用框架
- **說明**: Flutter 相關的應用程式框架

## 3. Flutter.xcframework
- **用途**: Flutter 引擎
- **說明**: Flutter 框架的核心引擎

---

## 開發者注意事項

這些 framework 是專案編譯所必需的依賴項。如果你要：

1. **修改 SDK 功能**: 需要取得 `moda_digitalwallet_holder_sdk` 的原始碼
2. **更新 Flutter 版本**: 請參考 Flutter 官方文檔進行升級
3. **移除 Flutter 依賴**: 需要重構相關的程式碼

## 替代方案

如果你想要完全從原始碼建置：
- 聯繫專案維護者取得 SDK 原始碼
- 或者實作自己的數位憑證處理邏輯來替代此 SDK
