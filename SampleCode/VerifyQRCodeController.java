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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

// 引入服務層類別
import com.example.demo.service.TOTPService;
import com.example.demo.service.HMACService;

/**
 * QR Code 驗證控制器
 * 此控制器負責驗證 QR Code 的內容，包含：
 * 1. RSA 解密
 * 2. TOTP 驗證
 * 3. HMAC 驗證
 * 4. 資料完整性檢查
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
     * @param request 包含加密資料和驗證金鑰的請求
     * @return 驗證結果，包含驗證狀態和解密後的資料
     */
    @PostMapping("/verify-qrcode")
    public ResponseEntity<?> verifyQRCode(@RequestBody VerifyQRCodeRequest request) {
        logger.info("開始驗證 QR Code");
        try {
            // 驗證請求參數是否完整
            if (!validateRequest(request)) {
                return ResponseEntity.badRequest().body(new VerifyQRCodeResponse(
                    "請求參數驗證失敗：請確保所有必要參數都已提供且不為空",
                    false,
                    null
                ));
            }

            // 1. 解密 DATA 欄位內容
            logger.info("開始解密 DATA 欄位內容");
            String decryptedData = eccService.decrypt(request.getEncryptedData(), request.getPrivateKey());
            logger.info("解密完成，解密後的資料：{}", decryptedData);
            
            // 輸出解密後的明文到日誌
            logger.info("==========================================");
            logger.info("解密後的明文：");
            logger.info(decryptedData);
            logger.info("==========================================");
            
            // 解析 JSON 資料
            JsonNode dataNode = objectMapper.readTree(decryptedData);
            logger.info("JSON 解析完成");

            // 2. 驗證必要欄位是否存在
            logger.info("開始驗證必要欄位");
            if (!validateRequiredFields(dataNode)) {
                return ResponseEntity.ok(new VerifyQRCodeResponse(
                    "解密失敗：缺少必要欄位 (totp, phone, hmac, name)",
                    false,
                    null
                ));
            }
            logger.info("必要欄位驗證通過");

            // 取得各欄位值
            String totp = dataNode.get("totp").asText();
            String phone = dataNode.get("phone").asText();
            String hmac = dataNode.get("hmac").asText();
            String name = dataNode.get("name").asText();
            logger.info("取得欄位值：totp={}, phone={}, name={}", totp, phone, name);

            // 3. 驗證 TOTP 碼
            logger.info("開始驗證 TOTP");
            if (!totpService.verifyTOTP(totp, request.getTotpKey())) {
                logger.error("TOTP 驗證失敗");
                return ResponseEntity.ok(new VerifyQRCodeResponse(
                    "TOTP 驗證失敗：TOTP 碼無效或已過期",
                    false,
                    null
                ));
            }
            logger.info("TOTP 驗證通過");

            // 4. 驗證 HMAC 值
            logger.info("開始驗證 HMAC");
            String calculatedHmac = hmacService.calculateHMAC(totp + name, request.getHmacKey());
            logger.info("計算出的 HMAC：{}", calculatedHmac);
            logger.info("原始 HMAC：{}", hmac);
            
            if (!calculatedHmac.equals(hmac)) {
                logger.error("HMAC 驗證失敗");
                return ResponseEntity.ok(new VerifyQRCodeResponse(
                    "HMAC 驗證失敗：資料完整性檢查失敗",
                    false,
                    null
                ));
            }
            logger.info("HMAC 驗證通過");

            // 5. 所有驗證都通過，回傳解密後的資料
            logger.info("所有驗證都通過，準備回傳結果");
            return ResponseEntity.ok(new VerifyQRCodeResponse(
                "驗證成功",
                true,
                decryptedData
            ));

        } catch (Exception e) {
            // 處理驗證過程中的錯誤
            logger.error("驗證過程發生錯誤", e);
            return ResponseEntity.ok(new VerifyQRCodeResponse(
                "驗證過程發生錯誤：" + e.getMessage(),
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
        return request != null &&
               StringUtils.hasText(request.getEncryptedData()) &&
               StringUtils.hasText(request.getPrivateKey()) &&
               StringUtils.hasText(request.getTotpKey()) &&
               StringUtils.hasText(request.getHmacKey());
    }

    /**
     * 驗證解密後的資料是否包含所有必要欄位
     * 
     * @param dataNode JSON 資料節點
     * @return 如果所有必要欄位都存在且不為空則回傳 true，否則回傳 false
     */
    private boolean validateRequiredFields(JsonNode dataNode) {
        return dataNode != null &&
               dataNode.has("totp") &&
               dataNode.has("phone") &&
               dataNode.has("hmac") &&
               dataNode.has("name") &&
               StringUtils.hasText(dataNode.get("totp").asText()) &&
               StringUtils.hasText(dataNode.get("phone").asText()) &&
               StringUtils.hasText(dataNode.get("hmac").asText()) &&
               StringUtils.hasText(dataNode.get("name").asText());
    }

    /**
     * QR Code 驗證請求物件
     * 用於接收前端傳來的驗證請求資料
     */
    public static class VerifyQRCodeRequest {
        private String encryptedData;  // 加密後的資料
        private String privateKey;     // RSA 私鑰
        private String totpKey;        // TOTP 金鑰
        private String hmacKey;        // HMAC 金鑰

        // Getters and Setters
        public String getEncryptedData() {
            return encryptedData;
        }

        public void setEncryptedData(String encryptedData) {
            this.encryptedData = encryptedData;
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