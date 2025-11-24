package gov.moda.dw.issuer.vc.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * hash operation
 *
 * @version 20250428
 */
public class HashUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(HashUtils.class);
	
	public static String getSha256Hex(String input) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
	        byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
	
	        // 將 byte 陣列轉為 hex 字串
	        String hex = HexFormat.of().formatHex(hash);
	        return hex;
		}catch(Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}
}
