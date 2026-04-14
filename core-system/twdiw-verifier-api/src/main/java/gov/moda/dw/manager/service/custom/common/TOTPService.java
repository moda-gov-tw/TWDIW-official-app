package gov.moda.dw.manager.service.custom.common;

// 引入 Spring 框架相關類別
import org.springframework.stereotype.Service;

import gov.moda.dw.manager.config.AppConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// 引入 Java 加密相關類別
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.KeyGenerator;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * TOTP（基於時間的一次性密碼）服務
 * 此服務類別提供 TOTP 的相關功能，包含：
 * 1. 產生 TOTP 金鑰
 * 2. 產生 TOTP 碼
 * 3. 驗證 TOTP 碼
 * 4. 檢核輸入的 TOTP 金鑰是否符合
 * 
 * 本服務實作 RFC 6238 標準，使用 HMAC-SHA256 演算法，
 * 結合時間戳記和金鑰來產生一次性密碼。
 * 
 * 主要特點：
 * - 使用 256 位元（32 位元組）的金鑰長度
 * - 提供 6 位數的 TOTP 碼
 * - 預設 60 秒的有效時間
 * - 允許前後 30 秒的時間偏移
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TOTPService {

    /** 讀取 yml 配置 */
    private final AppConfiguration appConfiguration;

    /**
     * 產生 TOTP 金鑰
     * 
     * @return 包含 TOTP 金鑰的 Map，金鑰以 Base64 格式儲存
     * @throws RuntimeException 當金鑰產生失敗時拋出
     */
    public Map<String, String> generateTOTPKey() {
        try {
            // 初始化 HMAC-SHA256 金鑰產生器
            KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
            keyGenerator.init(256);
            
            // 產生金鑰
            final byte[] secKey = keyGenerator.generateKey().getEncoded();
            
            // 將金鑰轉換為 Base64 字串格式
            String base64Key = Base64.getEncoder().encodeToString(secKey);
            
            // 建立回傳結果
            Map<String, String> result = new HashMap<>();
            result.put("totpKey", base64Key);
            return result;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("TOTP 金鑰產生失敗", e);
        }
    }
    
    /**
     * 產生 TOTP 碼
     * 
     * @param secretKey Base64 格式的 TOTP 金鑰
     * @return 6 位數的 TOTP 碼
     * @throws RuntimeException 當 TOTP 產生失敗時拋出
     */
    public String generateTOTP(String secretKey, int i) {
        try {
             // 取得當前時間戳記（秒）
            long currentTime = Instant.now().getEpochSecond();

            // 計算 TOTP 時間區間，加入時間偏移量
            long currentTimeStep = (currentTime + appConfiguration.getTotpTimeOffset()) / appConfiguration.getTotpTimeout();
            
            // 取得目前時間區間 -1 為前一區間 +1 為後一區間
            long timeStep = currentTimeStep + i; 
            
            // 將時間區間轉換為位元組陣列
            byte[] timeBytes = ByteBuffer.allocate(8).putLong(timeStep).array();
            
            // 解碼 Base64 格式的金鑰
            byte[] keyBytes = Base64.getDecoder().decode(secretKey);
            
            // 初始化 HMAC-SHA256 演算法
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "HmacSHA256");
            mac.init(keySpec);
            
            // 計算 HMAC 值
            byte[] hash = mac.doFinal(timeBytes);
            
            // 取得偏移量（使用最後一個位元組的低 4 位）
            int offset = hash[hash.length - 1] & 0xf;
            
            // 計算 6 位數的 TOTP
            int binary = ((hash[offset] & 0x7f) << 24) |
                        ((hash[offset + 1] & 0xff) << 16) |
                        ((hash[offset + 2] & 0xff) << 8) |
                        (hash[offset + 3] & 0xff);
            
            // 取模得到 6 位數
            int otp = binary % 1000000;
            
            // 轉換為 6 位數的字串，不足補 0
            return String.format("%06d", otp);
            
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("TOTP 產生失敗", e);
        }
    }

     /**
     * 驗證 TOTP 碼是否有效
     * 
     * @param totpCode 要驗證的 TOTP 碼
     * @param secretKey Base64 格式的 TOTP 金鑰
     * @return 如果 TOTP 碼有效則回傳 true，否則回傳 false
     */
    public boolean verifyTOTP(String totpCode, String secretKey) {
        try {
            for (int i = -1; i <= 1; i++) {
                String currentTOTP = generateTOTP(secretKey, i);
                // 比對是否相同
                if (totpCode.equals(currentTOTP)) {
                    return true;
                }
            }
            return false;
            
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 檢核輸入的 TOTP 金鑰是否符合
     * 
     * @param key Base64 格式的 TOTP 金鑰
     * @return 如果 TOTP 金鑰符合則回傳 true，否則回傳 false
     */
    public static boolean validTOTPKey(String key) {
        try {
            // 解碼 Base64 格式的金鑰
            byte[] keyBytes = Base64.getDecoder().decode(key);
            
            // 金鑰長度 32 位元組
            return keyBytes.length == 32;
        } catch (Exception e) {
            return false;
        }
    }
} 