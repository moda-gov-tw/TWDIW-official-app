package gov.moda.dw.issuer.oidvci.common;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CryptoHelper {

private static final Logger log = LogManager.getLogger(CryptoHelper.class);
	
	private static final Charset UTF_8 = StandardCharsets.UTF_8;
    private static final String CIPHER_ALGORITHM = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH = 16;
    private static final int IV_LENGTH = 12;
    private static final int KEY_LENGTH = 32;
    private static final int ITERATIONS = 65535;

	public static String convertBinaryToHexString(byte[] binaryData) {

		StringBuffer hexString = new StringBuffer();

		for (int i = 0; i < binaryData.length; i++) {
			String hex = Integer.toHexString(0xff & binaryData[i]);
			if (hex.length() == 1)
				hexString.append('0');
			hexString.append(hex);
		}

		return hexString.toString();
	}
	
	public static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
	
	public String genSecret() throws Exception{
		KeyGenerator key_gen = KeyGenerator.getInstance("AES");
		key_gen.init(KEY_LENGTH * 8);
		
		SecretKey key = key_gen.generateKey();
		byte[] key_bytes = key.getEncoded();
//		log.info("[genSecret]: {}", Base64.getUrlEncoder().encodeToString(key_bytes));
		return Base64.getUrlEncoder().encodeToString(key_bytes);
	}
	
	public String genIV() throws Exception{
		byte[] IV = new byte[IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(IV);
        
		return Base64.getUrlEncoder().encodeToString(IV);
	}
	
	public String GCMEncrypt(String plaintext, String secret_string, String iv_b64) throws Exception{	
		byte[] iv = new byte[IV_LENGTH];
		iv = Base64.getUrlDecoder().decode(iv_b64);
        
        byte[] secret_key = Base64.getUrlDecoder().decode(secret_string);
        SecretKeySpec key_spec = new SecretKeySpec(secret_key, "AES");
        GCMParameterSpec gcm_parameter_spec = new GCMParameterSpec(TAG_LENGTH * 8, iv);

        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key_spec, gcm_parameter_spec);

        byte[] cipher_text = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        log.info("cipher: {}", convertBinaryToHexString(cipher_text));
        ByteBuffer byte_buffer = ByteBuffer.allocate(iv.length + cipher_text.length);
        byte_buffer.put(iv);
        byte_buffer.put(cipher_text);
        return convertBinaryToHexString(byte_buffer.array());
	}
	
	public String GCMDecrypt(String cipher_text, String secret_string) throws Exception {
		byte[] encrypted = hexStringToByteArray(cipher_text);
        byte[] IV = Arrays.copyOfRange(encrypted, 0, IV_LENGTH);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);

        GCMParameterSpec gcm_parameter_spec = new GCMParameterSpec(TAG_LENGTH * 8, IV);
        byte[] secret_key = Base64.getUrlDecoder().decode(secret_string);
        SecretKeySpec key_spec = new SecretKeySpec(secret_key, "AES");
        cipher.init(Cipher.DECRYPT_MODE, key_spec, gcm_parameter_spec);

        byte[] plaintext = cipher.doFinal(Arrays.copyOfRange(encrypted, 12, encrypted.length));
        return new String(plaintext);
    }
}
