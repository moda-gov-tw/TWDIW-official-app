# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2025-09-24

### Added
- 初始開源版本釋出 / Initial open source release
- 支援 OID4VC (OpenID for Verifiable Credentials) 協議 / Support for OID4VC protocol
- 支援 OID4VP (OpenID for Verifiable Presentations) 協議 / Support for OID4VP protocol
- DID 金鑰生成功能 / DID key generation functionality
- 可驗證憑證 (VC) 的簽發與驗證 / Issuance and verification of Verifiable Credentials
- 可驗證展示 (VP) 的生成與驗證 / Generation and verification of Verifiable Presentations
- 支援 Android 9+ 和 iOS 13+ 平台 / Support for Android 9+ and iOS 13+ platforms
- Flutter SDK 模組化架構 / Modular Flutter SDK architecture
- SD-JWT 解碼支援 / SD-JWT decoding support
- 憑證狀態列表檢查 / Credential status list checking

### Security
- 實作安全的金鑰管理機制 / Implemented secure key management mechanism
- JWT 簽名驗證 / JWT signature verification
- 憑證有效性檢查 / Credential validity checking

### Documentation
- 新增 README.md 說明文件 / Added README.md documentation
- 新增 CODE_OF_CONDUCT.md 行為準則 / Added Code of Conduct
- 新增 CONTRIBUTING.md 貢獻指南 / Added Contributing Guidelines
- 新增 LICENSE.txt (MIT) 授權文件 / Added MIT License

### Dependencies
- Flutter SDK ^3.5.1
- pointycastle: ^3.9.1 (加密運算 / Cryptographic operations)
- dart_jsonwebtoken: ^2.14.0 (JWT 處理 / JWT handling)
- http: ^1.2.2 (HTTP 請求 / HTTP requests)
- archive: ^3.6.1 (壓縮處理 / Archive handling)
- crypto: ^3.0.5 (雜湊演算法 / Hash algorithms)
- convert: ^3.1.1 (編碼轉換 / Encoding conversion)
- base_x: ^2.0.1 (Base編碼 / Base encoding)
- uuid: ^4.5.1 (UUID 生成 / UUID generation)