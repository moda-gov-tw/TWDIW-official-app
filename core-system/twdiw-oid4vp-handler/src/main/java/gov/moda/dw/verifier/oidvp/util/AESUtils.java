package gov.moda.dw.verifier.oidvp.util;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESUtils {

    public static final String AES_GCM_NO_PADDING = "AES/GCM/NoPadding";
    public static final int TAG_LEN = 128;
    public static final int IV_LENGTH = 12;

    private static final SecureRandom secureRandom = new SecureRandom();

    public static byte[] generateAESKey(int keyLength) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(keyLength);
        SecretKey key = keyGenerator.generateKey();
        return key.getEncoded();
    }

    public static byte[] encryptGCM(byte[] key, String plaintext, byte[] associatedData) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_GCM_NO_PADDING);
        byte[] iv = new byte[IV_LENGTH];
        secureRandom.nextBytes(iv);
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new GCMParameterSpec(TAG_LEN, iv));
        if (associatedData != null) {
            cipher.updateAAD(associatedData);
        }

        byte[] cipherText = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + cipherText.length);
        byteBuffer.put(iv);
        byteBuffer.put(cipherText);

        return byteBuffer.array();
    }

    public static String decryptGCM(byte[] key, byte[] ciphertext, byte[] associatedData) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_GCM_NO_PADDING);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new GCMParameterSpec(TAG_LEN, ciphertext, 0, IV_LENGTH));
        if (associatedData != null) {
            cipher.updateAAD(associatedData);
        }

        byte[] plainText = cipher.doFinal(ciphertext, IV_LENGTH, ciphertext.length - IV_LENGTH);

        return new String(plainText, StandardCharsets.UTF_8);
    }
}
