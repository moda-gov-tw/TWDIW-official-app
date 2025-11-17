package com.example.demo.service;

// 引入 Spring 框架相關類別
import org.springframework.stereotype.Service;

// 引入 Java 加密相關類別
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.nio.charset.StandardCharsets;

// 引入日誌相關類別
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HMAC 服務
 * 此服務類別提供 HMAC-SHA256 訊息驗證碼的相關功能，包含：
 * 1. 產生 HMAC 金鑰
 * 2. 計算 HMAC 值
 */
@Service
public class HMACService {
    // 設定日誌記錄器
    private static final Logger logger = LoggerFactory.getLogger(HMACService.class);
    
    /**
     * 產生 HMAC-SHA256 金鑰
     * 
     * @return 包含 HMAC 金鑰的 Map，金鑰以 Base64 格式儲存
     * @throws RuntimeException 當金鑰產生失敗時拋出
     */
    public Map<String, String> generateHMACKey() {
        try {
            // 初始化 HMAC-SHA256 金鑰產生器
            KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
            keyGenerator.init(256); // 使用 256 位元（32 位元組）的金鑰長度
            
            // 產生金鑰
            SecretKey secretKey = keyGenerator.generateKey();
            
            // 將金鑰轉換為 Base64 字串格式
            String base64Key = Base64.getEncoder().encodeToString(secretKey.getEncoded());
            
            // 建立回傳結果
            Map<String, String> result = new HashMap<>();
            result.put("hmacKey", base64Key);
            
            return result;
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("HMAC 金鑰產生失敗", e);
        }
    }

    /**
     * 使用 HMAC-SHA256 演算法計算訊息的驗證碼
     * 
     * @param data 要計算 HMAC 的原始資料
     * @param key Base64 格式的 HMAC 金鑰
     * @return Base64 格式的 HMAC 值
     * @throws Exception 當計算過程發生錯誤時拋出
     */
    public String calculateHMAC(String data, String key) throws Exception {
        // 記錄開始計算的日誌
        logger.info("開始計算(calculateHMAC) HMAC:{},{}", data, key);
        
        // 將 Base64 格式的金鑰轉換為位元組陣列
        byte[] keyBytes = Base64.getDecoder().decode(key);
        
        // 建立 HMAC-SHA256 金鑰規格
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");
        
        // 初始化 MAC 物件
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKey);
        
        // 計算 HMAC 值並轉換為 Base64 字串
        byte[] hmacBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hmacBytes);
    }
} 