# Flutter Framework 設定說明

本專案依賴 Flutter Framework 3.16.5。由於二進位檔案較大，已從版本控制中移除。

## 設定步驟

### 1. 安裝 Flutter SDK

請先安裝 Flutter SDK 3.16.5：

```bash
# 使用 fvm (推薦)
fvm install 3.16.5
fvm use 3.16.5

# 或直接從 Flutter 官網下載
# https://docs.flutter.dev/release/archive
```

### 2. 取得 Flutter.framework

將 Flutter.framework 複製到專案中：

```bash
# 方法一：從 Flutter SDK 複製
cp -R <FLUTTER_SDK_PATH>/bin/cache/artifacts/engine/ios/Flutter.framework \
  moda-digitalwallet-holder-sdk/

# 方法二：建立 Flutter 專案並從中提取
flutter create temp_flutter_project
cd temp_flutter_project
flutter build ios
# Framework 位於: build/ios/Release-iphoneos/Flutter.framework
```

### 3. 驗證設定

確認 Flutter.framework 已正確放置：

```bash
ls -la moda-digitalwallet-holder-sdk/Flutter.framework/
```

應該要看到以下檔案：
- Flutter (主要二進位檔案)
- Headers/ (標頭檔案目錄)
- Info.plist
- Modules/
- icudtl.dat

### 4. 編譯專案

使用 Xcode 或 xcodebuild 編譯專案：

```bash
xcodebuild \
  -project moda-digitalwallet-holder-sdk.xcodeproj \
  -scheme moda-digitalwallet-holder-sdk \
  -configuration Release \
  -sdk iphoneos \
  -derivedDataPath Build \
  BUILD_LIBRARY_FOR_DISTRIBUTION=YES \
  SKIP_INSTALL=NO \
  clean build
```

## 注意事項

- Flutter.framework 已被加入 .gitignore，不會被提交到版本控制
- 請確保使用正確的 Flutter SDK 版本 (3.16.5)
- 如果編譯失敗，請確認 Framework 路徑設定正確
