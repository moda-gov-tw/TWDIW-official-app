package gov.moda.dw.issuer.vc.util;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtils {
  // W942XxmFeaI2qN5JhbU7cRahDUCepVvh1VUpRP4ob6I=
  public static final String IV = "apdormfuivlddedl";
  public static final String AES_GCM_NOPADDING = "AES/GCM/NoPadding";
  public static final int TAG_LEN = 96;

  public static String getSHA256(final String value) {
    final byte[] bytes = getSHA256bytes(value);
    return Base64.getUrlEncoder().encodeToString(bytes);
  }

  public static byte[] getSHA256bytes(final String value) {
    if (value == null) return null;
    try {
      final MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
      return messageDigest.digest(value.getBytes(StandardCharsets.UTF_8));
    } catch (final NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  public static String generateKey(final int n) throws NoSuchAlgorithmException {
    final KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
    keyGenerator.init(n);
    final SecretKey key = keyGenerator.generateKey();
    return Base64.getEncoder().encodeToString(key.getEncoded());
  }

  public static String encryptGCM(final String content, final String aad, final String key)
      throws InvalidAlgorithmParameterException,
          NoSuchPaddingException,
          IllegalBlockSizeException,
          NoSuchAlgorithmException,
          BadPaddingException,
          InvalidKeyException {
    final String auth = Optional.ofNullable(aad).orElse("");
    final byte[] iv = IV.getBytes(StandardCharsets.UTF_8);
    final byte[] _aad = auth.getBytes(StandardCharsets.UTF_8);

    final byte[] encrypted =
        encryptGCM(
            content.getBytes(StandardCharsets.UTF_8),
            getAESKeyByBase64(key).getEncoded(),
            iv,
            _aad,
            TAG_LEN);
    return Base64.getEncoder().encodeToString(encrypted);
  }

  public static String decryptGCM(final String content, final String aad, final String key)
      throws InvalidAlgorithmParameterException,
          NoSuchPaddingException,
          IllegalBlockSizeException,
          NoSuchAlgorithmException,
          BadPaddingException,
          InvalidKeyException {
    final String auth = Optional.ofNullable(aad).orElse("");
    final byte[] iv = IV.getBytes(StandardCharsets.UTF_8);
    final byte[] _aad = auth.getBytes(StandardCharsets.UTF_8);

    final byte[] cipherText = Base64.getDecoder().decode(content);

    final byte[] decrypted =
        decryptGCM(cipherText, getAESKeyByBase64(key).getEncoded(), iv, _aad, TAG_LEN);
    return new String(decrypted);
  }

  private static byte[] encryptGCM(
      final byte[] data, final byte[] key, final byte[] iv, final byte[] aad, final int tagLength)
      throws NoSuchPaddingException,
          NoSuchAlgorithmException,
          InvalidAlgorithmParameterException,
          InvalidKeyException,
          IllegalBlockSizeException,
          BadPaddingException {
    final Cipher cipher = Cipher.getInstance(AES_GCM_NOPADDING);
    cipher.init(
        Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new GCMParameterSpec(tagLength, iv));
    cipher.updateAAD(aad);
    return cipher.doFinal(data);
  }

  private static byte[] decryptGCM(
      final byte[] data, final byte[] key, final byte[] iv, final byte[] aad, final int tagLength)
      throws NoSuchPaddingException,
          NoSuchAlgorithmException,
          InvalidKeyException,
          BadPaddingException,
          IllegalBlockSizeException,
          InvalidAlgorithmParameterException {
    final Cipher cipher = Cipher.getInstance(AES_GCM_NOPADDING);
    cipher.init(
        Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new GCMParameterSpec(tagLength, iv));
    cipher.updateAAD(aad);
    return cipher.doFinal(data);
  }

  private static SecretKey getAESKeyByBase64(final String keyBase64) {
    final byte[] decoded = Base64.getDecoder().decode(keyBase64);
    return new SecretKeySpec(decoded, "AES");
  }
}
