package gov.moda.dw.issuer.oidvci.config;

import io.micrometer.common.util.StringUtils;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * AttributeEncryptor class used to encrypt and decrypt entity attributes
 * for storing securely in the database.
 *
 * <p>This class implements the AttributeConverter interface to provide
 * custom conversion logic for entity attributes.</p>
 *
 * <p>It uses AES encryption with a secret key.</p>
 *
 * <p>AttributeEncryptor 類用於加密和解密實體屬性，以便安全地存儲在數據庫中。</p>
 *
 * <p>此類實現 AttributeConverter 接口以提供實體屬性的自定義轉換邏輯。</p>
 *
 * <p>使用帶有密鑰的 AES 加密。</p>
 *
 * @author Alex Chang
 * @create 2024/06/12
 */
@Component
@Converter
public class AttributeEncryptor implements AttributeConverter<String, String> {

    private static final String AES = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int SECRET_LENGTH_BYTES = 32; // 256-bit key
    private static final int GCM_TAG_LENGTH = 128; // GCM 標籤長度為 128 位元 (16 bytes)
    private static final int IV_LENGTH = 12; // GCM 建議的 IV 長度為 12 bytes

    private final Key key;
    private final Cipher cipher;
    private final SecureRandom secureRandom;

    public AttributeEncryptor(@Value("${applications.config.attributeencryptor_secret}") String secret) throws Exception {
        if (StringUtils.isBlank(secret)) {
            throw new IllegalStateException("applications.config.attributeencryptor_secret is required and must be 256-bit Base64");
        }
        byte[] secretBytes;
        try {
            secretBytes = Base64.getDecoder().decode(secret);
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException("applications.config.attributeencryptor_secret must be valid Base64", ex);
        }
        if (secretBytes.length != SECRET_LENGTH_BYTES) {
            throw new IllegalStateException("applications.config.attributeencryptor_secret must be 256-bit (32 bytes) after Base64 decoding");
        }

        key = new SecretKeySpec(secretBytes, AES);
        cipher = Cipher.getInstance(TRANSFORMATION);
        secureRandom = new SecureRandom();
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (StringUtils.isBlank(attribute)) {
            return "";
        }
        try {
            // 生成隨機 IV
            byte[] iv = new byte[IV_LENGTH];
            secureRandom.nextBytes(iv);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);

            cipher.init(Cipher.ENCRYPT_MODE, key, gcmSpec);

            // 加密資料並附加 IV
            byte[] encryptedData = cipher.doFinal(attribute.getBytes());
            byte[] encryptedWithIv = new byte[iv.length + encryptedData.length];
            System.arraycopy(iv, 0, encryptedWithIv, 0, iv.length);
            System.arraycopy(encryptedData, 0, encryptedWithIv, iv.length, encryptedData.length);

            return Base64.getEncoder().encodeToString(encryptedWithIv);
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException |
                 InvalidAlgorithmParameterException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (StringUtils.isBlank(dbData)) {
            return "";
        }
        try {
            byte[] encryptedWithIv = Base64.getDecoder().decode(dbData);

            // 提取 IV
            byte[] iv = new byte[IV_LENGTH];
            System.arraycopy(encryptedWithIv, 0, iv, 0, iv.length);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);

            // 提取加密數據
            byte[] encryptedData = new byte[encryptedWithIv.length - IV_LENGTH];
            System.arraycopy(encryptedWithIv, IV_LENGTH, encryptedData, 0, encryptedData.length);

            cipher.init(Cipher.DECRYPT_MODE, key, gcmSpec);
            return new String(cipher.doFinal(encryptedData), StandardCharsets.UTF_8);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException e) {
            throw new IllegalStateException(e);
        }
    }
    //        public static void main (String[] args) throws Exception {
    //
    //            AttributeEncryptor a = new AttributeEncryptor();
    //
    //            System.out.println(a.convertToDatabaseColumn("黃奕誠"));
    //        }
}
