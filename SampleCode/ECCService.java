package com.example.demo.service;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.NamedParameterSpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * ECC 加密服務
 * 使用 Curve25519 演算法進行非對稱加密，結合 ECDH 金鑰交換和 ChaCha20-Poly1305 對稱加密
 * 
 * 主要功能：
 * 1. 產生 ECC 金鑰對
 * 2. 使用公鑰加密資料
 * 3. 使用私鑰解密資料
 * 
 * 加密流程：
 * 1. 產生臨時金鑰對
 * 2. 使用 ECDH 產生共享金鑰
 * 3. 使用 SHA-256 作為 KDF 衍生對稱金鑰
 * 4. 使用 ChaCha20-Poly1305 加密資料
 * 5. 組合臨時公鑰、Nonce 和加密資料
 * 
 * 解密流程：
 * 1. 分離臨時公鑰、Nonce 和加密資料
 * 2. 使用 ECDH 重建共享金鑰
 * 3. 使用 SHA-256 作為 KDF 衍生對稱金鑰
 * 4. 使用 ChaCha20-Poly1305 解密資料
 */
@Service
public class ECCService {

    // 金鑰演算法與對稱加密演算法名稱
    private static final String KEY_AGREEMENT_ALGO = "X25519";
    private static final String SYMMETRIC_ALGO = "ChaCha20-Poly1305";
    private static final int NONCE_LENGTH = 12;
    private static final String KDF_ALGO = "SHA-256";
    private static final int SYMMETRIC_KEY_SIZE = 32; // 256 bits

    /**
     * 產生 ECC 金鑰對
     * 
     * @return 包含 Base64 編碼的公鑰和私鑰的字串陣列，索引 0 為公鑰，索引 1 為私鑰
     * @throws NoSuchAlgorithmException 當指定的演算法不存在時拋出
     * @throws NoSuchProviderException 當指定的加密提供者不存在時拋出
     * @throws InvalidAlgorithmParameterException 當指定的演算法參數不合法時拋出
     */
    public String[] generateKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        // 建立 X25519 金鑰產生器
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(KEY_AGREEMENT_ALGO);
        // 初始化金鑰產生器，使用 X25519 曲線與安全隨機數
        kpg.initialize(new NamedParameterSpec("X25519"), new SecureRandom());
        // 產生金鑰對
        KeyPair kp = kpg.generateKeyPair();

        // 將公鑰與私鑰進行 Base64 編碼
        String publicKeyBase64 = Base64.getEncoder().encodeToString(kp.getPublic().getEncoded());
        String privateKeyBase64 = Base64.getEncoder().encodeToString(kp.getPrivate().getEncoded());

        // 回傳 [公鑰, 私鑰]
        return new String[]{publicKeyBase64, privateKeyBase64};
    }

    /**
     * 使用 KDF 衍生對稱金鑰
     * 
     * @param sharedSecret ECDH 共享金鑰
     * @return 衍生的對稱金鑰
     * @throws NoSuchAlgorithmException 當指定的演算法不存在時拋出
     */
    private SecretKey deriveKey(byte[] sharedSecret) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(KDF_ALGO);
        byte[] derivedKey = digest.digest(sharedSecret);
        return new SecretKeySpec(derivedKey, 0, SYMMETRIC_KEY_SIZE, "ChaCha20");
    }

    /**
     * 使用對方公鑰加密明文
     * 
     * @param plaintext 要加密的文字
     * @param receiverPublicKeyBase64 Base64 編碼的對方公鑰
     * @return Base64 編碼的加密結果，格式為：[ephemeral 公鑰長度(4 bytes) + ephemeral 公鑰 + nonce + 加密資料]
     * @throws Exception 當加密過程發生錯誤時拋出
     */
    public String encrypt(String plaintext, String receiverPublicKeyBase64) throws Exception {
        // 1. 載入對方公鑰（Base64 解碼 → X509EncodedKeySpec → PublicKey）
        KeyFactory kf = KeyFactory.getInstance(KEY_AGREEMENT_ALGO);
        PublicKey receiverPublicKey = kf.generatePublic(
                new java.security.spec.X509EncodedKeySpec(Base64.getDecoder().decode(receiverPublicKeyBase64)));

        // 2. 產生臨時發送端金鑰對（每次加密都不同，確保前向安全性）
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(KEY_AGREEMENT_ALGO);
        kpg.initialize(new NamedParameterSpec("X25519"), new SecureRandom());
        KeyPair ephemeralKeyPair = kpg.generateKeyPair();

        // 3. ECDH 金鑰交換，產生共享金鑰
        KeyAgreement ka = KeyAgreement.getInstance(KEY_AGREEMENT_ALGO);
        ka.init(ephemeralKeyPair.getPrivate()); // 使用臨時私鑰
        ka.doPhase(receiverPublicKey, true);    // 對方公鑰
        byte[] sharedSecret = ka.generateSecret(); // 取得共享金鑰

        // 4. 使用 KDF 衍生對稱金鑰
        SecretKey sharedKey = deriveKey(sharedSecret);

        // 5. 產生 12 bytes 隨機 nonce，並建立 IvParameterSpec
        byte[] nonce = new byte[NONCE_LENGTH];
        new SecureRandom().nextBytes(nonce);
        IvParameterSpec ivSpec = new IvParameterSpec(nonce);

        // 6. 使用 ChaCha20-Poly1305 進行加密
        Cipher cipher = Cipher.getInstance(SYMMETRIC_ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, sharedKey, ivSpec);
        byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

        // 7. 組合輸出：ephemeral 公鑰長度(4 bytes) + ephemeral 公鑰 + nonce + ciphertext
        byte[] ephemeralPubKey = ephemeralKeyPair.getPublic().getEncoded(); // 臨時公鑰
        byte[] keyLength = intToBytes(ephemeralPubKey.length); // 公鑰長度（4 bytes）
        byte[] output = new byte[keyLength.length + ephemeralPubKey.length + nonce.length + ciphertext.length];
        
        int offset = 0;
        System.arraycopy(keyLength, 0, output, offset, keyLength.length); // 前 4 bytes 放公鑰長度
        offset += keyLength.length;
        System.arraycopy(ephemeralPubKey, 0, output, offset, ephemeralPubKey.length); // 接著放公鑰
        offset += ephemeralPubKey.length;
        System.arraycopy(nonce, 0, output, offset, nonce.length); // 接著放 nonce
        offset += nonce.length;
        System.arraycopy(ciphertext, 0, output, offset, ciphertext.length); // 最後放密文

        // 回傳 Base64 編碼的最終加密資料
        return Base64.getEncoder().encodeToString(output);
    }

    /**
     * 將整數轉換為 4 bytes 的位元組陣列
     */
    private byte[] intToBytes(int value) {
        return new byte[] {
            (byte) (value >> 24),
            (byte) (value >> 16),
            (byte) (value >> 8),
            (byte) value
        };
    }

    /**
     * 將 4 bytes 的位元組陣列轉換為整數
     */
    private int bytesToInt(byte[] bytes) {
        return ((bytes[0] & 0xFF) << 24) |
               ((bytes[1] & 0xFF) << 16) |
               ((bytes[2] & 0xFF) << 8) |
               (bytes[3] & 0xFF);
    }

    /**
     * 使用私鑰解密密文
     * 
     * @param encryptedBase64 Base64 編碼的加密資料，格式為：[ephemeral 公鑰長度(4 bytes) + ephemeral 公鑰 + nonce + 加密資料]
     * @param privateKeyBase64 Base64 編碼的私鑰
     * @return 解密後的明文
     * @throws Exception 當解密過程發生錯誤時拋出
     */
    public String decrypt(String encryptedBase64, String privateKeyBase64) throws Exception {
        try {
            // 1. Base64 解碼取得原始加密資料
            byte[] input = Base64.getDecoder().decode(encryptedBase64);

            // 2. 載入私鑰（Base64 解碼 → PKCS8EncodedKeySpec → PrivateKey）
            KeyFactory kf = KeyFactory.getInstance(KEY_AGREEMENT_ALGO);
            PrivateKey privateKey = kf.generatePrivate(
                    new java.security.spec.PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyBase64)));

            // 3. 分離 ephemeral 公鑰長度、公鑰、nonce、密文
            byte[] keyLengthBytes = new byte[4];
            System.arraycopy(input, 0, keyLengthBytes, 0, 4);
            int ephemeralPubKeyLength = bytesToInt(keyLengthBytes);

            byte[] ephemeralPubKeyBytes = new byte[ephemeralPubKeyLength];
            byte[] nonce = new byte[NONCE_LENGTH];
            byte[] ciphertext = new byte[input.length - 4 - ephemeralPubKeyLength - NONCE_LENGTH];

            int offset = 4;
            System.arraycopy(input, offset, ephemeralPubKeyBytes, 0, ephemeralPubKeyLength);
            offset += ephemeralPubKeyLength;
            System.arraycopy(input, offset, nonce, 0, NONCE_LENGTH);
            offset += NONCE_LENGTH;
            System.arraycopy(input, offset, ciphertext, 0, ciphertext.length);

            // 4. 還原臨時公鑰
            PublicKey ephemeralPublicKey = kf.generatePublic(
                    new java.security.spec.X509EncodedKeySpec(ephemeralPubKeyBytes));

            // 5. ECDH 金鑰交換，重建共享金鑰
            KeyAgreement ka = KeyAgreement.getInstance(KEY_AGREEMENT_ALGO);
            ka.init(privateKey); // 使用本地私鑰
            ka.doPhase(ephemeralPublicKey, true); // 臨時公鑰
            byte[] sharedSecret = ka.generateSecret(); // 取得共享金鑰

            // 6. 使用 KDF 衍生對稱金鑰
            SecretKey sharedKey = deriveKey(sharedSecret);

            // 7. 使用 ChaCha20-Poly1305 解密
            Cipher cipher = Cipher.getInstance(SYMMETRIC_ALGO);
            cipher.init(Cipher.DECRYPT_MODE, sharedKey, new IvParameterSpec(nonce));
            byte[] decrypted = cipher.doFinal(ciphertext);

            // 回傳明文
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (javax.crypto.AEADBadTagException e) {
            throw new SecurityException("MAC 驗證失敗：資料可能被竄改", e);
        }
    }

    /**
     * 產生 ECC 金鑰對並以 Map 格式回傳
     * 
     * @return 包含 Base64 編碼的公鑰和私鑰的 Map
     * @throws NoSuchAlgorithmException 當指定的演算法不存在時拋出
     * @throws NoSuchProviderException 當指定的加密提供者不存在時拋出
     * @throws InvalidAlgorithmParameterException 當指定的演算法參數不合法時拋出
     */
    public Map<String, String> generateKeyPairMap() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        // 取得金鑰對 [公鑰, 私鑰]
        String[] keyPair = this.generateKeyPair();

        // 將金鑰對放入 Map
        Map<String, String> result = new HashMap<>();
        result.put("privateKey", keyPair[1]);
        result.put("publicKey", keyPair[0]);

        // 回傳 Map
        return result;
    }
} 