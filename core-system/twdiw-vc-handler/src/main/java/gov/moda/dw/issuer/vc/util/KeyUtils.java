package gov.moda.dw.issuer.vc.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * key operation
 *
 * @version 20240902
 */
public class KeyUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(KeyUtils.class);
    
    private static final String CIPHER_ALGORITHM = "AES/GCM/NoPadding";
    private static final int KEY_LENGTH = 32;
    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH = 16;
    
    private final static ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    public static ECKey generateP256(String kid) {

        try {
            // generate P-256
            return new ECKeyGenerator(Curve.P_256)
                .keyID(kid)
                .generate();

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return null;
    }
    
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
    
    public static String genIV() throws Exception{
		byte[] IV = new byte[IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(IV);
        
		return Base64.getUrlEncoder().encodeToString(IV);
	}
    
    public static String GCMEncrypt(String plaintext, String secret_string, String iv_b64) throws Exception{
		
		byte[] iv = new byte[IV_LENGTH];
		iv = Base64.getUrlDecoder().decode(iv_b64);
        
        byte[] secret_key = Base64.getUrlDecoder().decode(secret_string);
        SecretKeySpec key_spec = new SecretKeySpec(secret_key, "AES");
        GCMParameterSpec gcm_parameter_spec = new GCMParameterSpec(TAG_LENGTH * 8, iv);

        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key_spec, gcm_parameter_spec);

        byte[] cipher_text = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        LOGGER.info("[GCMEncrypt]: cipher: {}", convertBinaryToHexString(cipher_text));

        ByteBuffer byte_buffer = ByteBuffer.allocate(iv.length + cipher_text.length);
        byte_buffer.put(iv);
        byte_buffer.put(cipher_text);

        return convertBinaryToHexString(byte_buffer.array());
	}
	
	public static String GCMDecrypt(String cipher_text, String secret_string) throws Exception {

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
	
	public static boolean isPossiblyJwk(String issuerKey) throws Exception{
		try {
			Map<String, Object> issuerKeyMap = objectMapper.readValue(issuerKey, LinkedHashMap.class);
			if(!issuerKeyMap.containsKey("kty"))
				return false;
			String algo = (String)issuerKeyMap.get("kty");
			switch (algo) {
				case "RSA":
					return issuerKeyMap.containsKey("n") && issuerKeyMap.containsKey("e");
				case "EC":
					return issuerKeyMap.containsKey("crv") && issuerKeyMap.containsKey("x") && issuerKeyMap.containsKey("y");
				default:
					return false;
			}
		}catch (Exception e) {
			return false;
		}
	}
}
