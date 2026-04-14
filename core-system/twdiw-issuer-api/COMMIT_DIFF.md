# Commit 差異報告

## 摘要

本次 commit (`ecb8c17b`) 與上一次 commit (`9946b048`) 之間的變更：

- **變更檔案數量**: 1 個檔案
- **變更類型**: CORS 設定調整
- **主要變更**:
  - 在 `application.yml` 中調整 CORS `allowed-origins` 設定
  - 新增支援帶有明確 port 80 的 HTTP/HTTPS 來源（`http://${ISSUER_HOST_NAME}:80/` 和 `https://${ISSUER_HOST_NAME}:80/`）
  - 允許更彈性的 CORS 來源設定，支援有無明確 port 的連線方式

---

## 詳細變更內容

### 變更統計
```
 src/main/resources/config/application.yml | 2 +-
 1 file changed, 1 insertion(+), 1 deletion(-)
```

### 檔案變更詳情

#### `src/main/resources/config/application.yml`

**變更位置**: 第 240 行

**變更內容**:

```diff
--- a/src/main/resources/config/application.yml
+++ b/src/main/resources/config/application.yml
@@ -237,7 +237,7 @@ jhipster:
   # CORS is only enabled by default with the "dev" profile
   cors:
     # Allow Ionic for JHipster by default (* no longer allowed in Spring Boot 2.4+)
-    allowed-origins: http://${ISSUER_HOST_NAME}/,https://${ISSUER_HOST_NAME}/
+    allowed-origins: http://${ISSUER_HOST_NAME}/,https://${ISSUER_HOST_NAME}/,http://${ISSUER_HOST_NAME}:80/,https://${ISSUER_HOST_NAME}:80/
     # Enable CORS when running in GitHub Codespaces
     # allowed-origin-patterns: 'https://*.githubpreview.dev'
     allowed-methods: '*'
```

**說明**:
- **修改前**: 僅允許 `http://${ISSUER_HOST_NAME}/` 和 `https://${ISSUER_HOST_NAME}/` 兩個來源
- **修改後**: 新增允許 `http://${ISSUER_HOST_NAME}:80/` 和 `https://${ISSUER_HOST_NAME}:80/` 兩個來源
- **影響**: 解決了某些客戶端明確指定 port 80 時可能出現的 CORS 錯誤問題

---

## Commit 資訊

- **Commit Hash**: `ecb8c17b`
- **Commit Message**: `調整cors設定`
- **上一個 Commit**: `9946b048` (調整fileNamePattern語法錯誤)
- **變更日期**: 請查看 git log 獲取詳細時間資訊






