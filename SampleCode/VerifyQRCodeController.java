package com.example.demo.controller;

// 引入 Spring MVC 相關類別
import com.example.demo.service.ECCService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

// 引入日誌相關類別
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// 引入 JSON 處理相關類別
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

// 引入常數時間比對與字元編碼相關類別
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

// 引入服務層類別
import com.example.demo.service.TOTPService;
import com.example.demo.service.HMACService;

/**
 * QR Code 驗證控制器
 * 此控制器示範驗證端於離線環境自行解密數位憑證皮夾所出示 QR Code 的完整流程，包含：
 * 1. ECC 解密（X25519 ECDH + ChaCha20-Poly1305）
 * 2. TOTP 驗證
 * 3. HMAC 驗證
 * 4. 資料完整性檢查
 *
 * 驗證邏輯與驗證端模組的 DWVP-05-404 API 一致：
 * - HMAC 的計算對象為「整段解密後明文」，比對目標為 QR Code 的 h 欄位
 * - 解密後明文中僅 totp 為必填欄位，其餘欄位由數位憑證皮夾端定義
 */
@RestController
@RequestMapping("/api")
public class VerifyQRCodeController {
    // 設定日誌記錄器
    private static final Logger logger = LoggerFactory.getLogger(VerifyQRCodeController.class);

    // 注入 TOTP 服務
    @Autowired
    private TOTPService totpService;

    // 注入 HMAC 服務
    @Autowired
    private HMACService hmacService;

    // 注入加解密服務
    @Autowired
    private ECCService eccService;

    // 建立 JSON 處理器
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 驗證 QR Code 內容
     *
     * @param request 包含 QR Code 四個欄位與驗證所需金鑰的請求
     * @return 驗證結果，包含驗證狀態與解密後的資料
     */
    @PostMapping("/verify-qrcode")
    public ResponseEntity<VerifyQRCodeResponse> verifyQRCode(@RequestBody VerifyQRCodeRequest request) {
        logger.info("開始驗證 QR Code");
        try {
            // 1. 驗證請求參數是否完整
            if (!validateRequest(request)) {
                // 參數不完整時回傳 400，並以固定訊息說明必填欄位
                return ResponseEntity.badRequest().body(new VerifyQRCodeResponse(
                    "請求參數驗證失敗：t、d、h、k 與 privateKey、totpKey、hmacKey 皆為必填",
                    false,
                    null
                ));
            }

            // 2. 使用私鑰解密 d 欄位，取得原始明文
            // 注意：嚴禁將解密後明文寫入日誌，其中包含姓名、電話等個人資料
            String decryptedData = eccService.decrypt(request.getData(), request.getPrivateKey());
            logger.info("ECC 解密完成");

            // 3. 將解密後的明文解析為 JSON 物件
            JsonNode dataNode = objectMapper.readTree(decryptedData);
            logger.info("JSON 解析完成");

            // 4. 驗證必要欄位是否存在（與驗證端模組一致，僅 totp 為必填）
            if (!validateRequiredFields(dataNode)) {
                // 缺少 totp 欄位時視為解密結果不符預期格式
                return ResponseEntity.ok(new VerifyQRCodeResponse(
                    "解密失敗：缺少必要欄位（totp）",
                    false,
                    null
                ));
            }

            // 取得 TOTP 碼欄位值
            String totp = dataNode.get("totp").asText();

            // 5. 驗證 TOTP 碼是否有效
            if (!totpService.verifyTOTP(totp, request.getTotpKey())) {
                // TOTP 驗證失敗代表 QR Code 已過期或金鑰不符
                logger.warn("TOTP 驗證失敗");
                return ResponseEntity.ok(new VerifyQRCodeResponse(
                    "TOTP 驗證失敗：TOTP 碼無效或已過期",
                    false,
                    null
                ));
            }
            logger.info("TOTP 驗證通過");

            // 6. 驗證 HMAC 值：對「整段解密後明文」計算，與 QR Code 的 h 欄位比對
            // 注意：嚴禁將計算結果或金鑰寫入日誌
            String calculatedHmac = hmacService.calculateHMAC(decryptedData, request.getHmacKey());

            // 以常數時間比對 HMAC，避免逐字元短路比對洩漏比對進度（時序側通道）
            if (!MessageDigest.isEqual(calculatedHmac.getBytes(StandardCharsets.UTF_8),
                    request.getHmac().getBytes(StandardCharsets.UTF_8))) {
                // HMAC 不符代表資料遭竄改或並非由合法發行方產生
                logger.warn("HMAC 驗證失敗");
                return ResponseEntity.ok(new VerifyQRCodeResponse(
                    "HMAC 驗證失敗：資料完整性檢查失敗",
                    false,
                    null
                ));
            }
            logger.info("HMAC 驗證通過");

            // 7. 所有驗證都通過，回傳解密後的資料
            logger.info("所有驗證都通過，準備回傳結果");
            return ResponseEntity.ok(new VerifyQRCodeResponse(
                "驗證成功",
                true,
                decryptedData
            ));

        } catch (Exception e) {
            // 完整堆疊僅記錄於伺服器端，避免內部細節外洩給呼叫端
            logger.error("驗證過程發生錯誤", e);
            return ResponseEntity.ok(new VerifyQRCodeResponse(
                "驗證過程發生錯誤",
                false,
                null
            ));
        }
    }

    /**
     * 驗證請求參數是否完整
     *
     * @param request 驗證請求物件
     * @return 如果所有必要參數都存在且不為空則回傳 true，否則回傳 false
     */
    private boolean validateRequest(VerifyQRCodeRequest request) {
        // 請求物件本身為 null 時直接視為不合法
        if (request == null) {
            return false;
        }
        // QR Code 的 t 欄位（交易類型）為必填
        if (!StringUtils.hasText(request.getTag())) {
            return false;
        }
        // QR Code 的 d 欄位（加密資料）為必填
        if (!StringUtils.hasText(request.getData())) {
            return false;
        }
        // QR Code 的 h 欄位（HMAC 驗證值）為必填
        if (!StringUtils.hasText(request.getHmac())) {
            return false;
        }
        // QR Code 的 k 欄位（金鑰代碼）為必填
        if (!StringUtils.hasText(request.getKeyId())) {
            return false;
        }
        // 解密用私鑰為必填
        if (!StringUtils.hasText(request.getPrivateKey())) {
            return false;
        }
        // TOTP 驗證金鑰為必填
        if (!StringUtils.hasText(request.getTotpKey())) {
            return false;
        }
        // HMAC 驗證金鑰為必填
        if (!StringUtils.hasText(request.getHmacKey())) {
            return false;
        }
        // 全部通過
        return true;
    }

    /**
     * 驗證解密後的資料是否包含必要欄位
     * 與驗證端模組一致，解密後明文中僅 totp 為必填欄位
     *
     * @param dataNode JSON 資料節點
     * @return 如果 totp 欄位存在且不為空則回傳 true，否則回傳 false
     */
    private boolean validateRequiredFields(JsonNode dataNode) {
        // JSON 節點為 null 時視為不合法
        if (dataNode == null) {
            return false;
        }
        // 必須存在 totp 欄位
        if (!dataNode.has("totp")) {
            return false;
        }
        // totp 欄位不得為空字串
        return StringUtils.hasText(dataNode.get("totp").asText());
    }

    /**
     * QR Code 驗證請求物件
     * 前四個欄位對應 QR Code 掃描後取得的 JSON 內容，後三個欄位為驗證端持有的金鑰
     *
     * 重要：此範例為求呈現完整流程，將三組金鑰放在請求內容中傳遞。
     * 實際部署時，privateKey、totpKey 與 hmacKey 必須保存在 POS 機本機的設定檔、
     * 環境變數或安全模組中，嚴禁透過網路請求傳遞，否則金鑰會出現在存取日誌、
     * APM 追蹤紀錄與例外堆疊之中。
     */
    public static class VerifyQRCodeRequest {
        @JsonProperty("t")
        private String tag;            // QR Code 的交易類型
        @JsonProperty("d")
        private String data;           // QR Code 的加密資料（Base64）
        @JsonProperty("h")
        private String hmac;           // QR Code 的 HMAC 驗證值（Base64）
        @JsonProperty("k")
        private String keyId;          // QR Code 的金鑰代碼，用於選擇對應的金鑰組
        private String privateKey;     // X25519 私鑰（Base64 編碼的 PKCS#8 格式）
        private String totpKey;        // TOTP 金鑰（Base64 編碼，32 位元組）
        private String hmacKey;        // HMAC 金鑰（Base64 編碼，32 位元組）

        // Getters and Setters
        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public String getHmac() {
            return hmac;
        }

        public void setHmac(String hmac) {
            this.hmac = hmac;
        }

        public String getKeyId() {
            return keyId;
        }

        public void setKeyId(String keyId) {
            this.keyId = keyId;
        }

        public String getPrivateKey() {
            return privateKey;
        }

        public void setPrivateKey(String privateKey) {
            this.privateKey = privateKey;
        }

        public String getTotpKey() {
            return totpKey;
        }

        public void setTotpKey(String totpKey) {
            this.totpKey = totpKey;
        }

        public String getHmacKey() {
            return hmacKey;
        }

        public void setHmacKey(String hmacKey) {
            this.hmacKey = hmacKey;
        }
    }

    /**
     * QR Code 驗證回應物件
     * 用於回傳驗證結果給前端
     */
    public static class VerifyQRCodeResponse {
        private String message;    // 回應訊息
        private boolean isValid;   // 驗證是否通過
        private String data;       // 解密後的資料

        /**
         * 建構子
         *
         * @param message 回應訊息
         * @param isValid 驗證是否通過
         * @param data 解密後的資料
         */
        public VerifyQRCodeResponse(String message, boolean isValid, String data) {
            this.message = message;
            this.isValid = isValid;
            this.data = data;
        }

        // Getters and Setters
        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public boolean isValid() {
            return isValid;
        }

        public void setValid(boolean isValid) {
            this.isValid = isValid;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }
}
