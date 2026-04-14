package gov.moda.dw.manager.service.custom.common;

import java.security.PublicKey;
import java.text.ParseException;

import org.springframework.stereotype.Service;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jwt.SignedJWT;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DidKeyService {

    /**
     * 解析 JWT
     * 
     * @param jwt
     * @return
     */
    public SignedJWT parseSignedJWT(String jwt) {
        try {
            log.info("解析 JWT...");
            return SignedJWT.parse(jwt);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid jwt parse");
        }
    }

    /**
     * 驗證 JWT
     * 
     * @param jwt
     * @param publicKey
     * @return
     */
    public boolean verify(String jwt, PublicKey publicKey) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(jwt);
            log.info("創建 JWS 驗證器...");
            JWSVerifier verifier = new ECDSAVerifier((java.security.interfaces.ECPublicKey) publicKey);
            log.info("驗證 JWT...");
            boolean isValid = signedJWT.verify(verifier);
            if (isValid) {
                log.info("JWT 簽章驗證成功！");
            } else {
                log.info("""
                        JWT 簽章驗證失敗！
                        可能的原因：
                        - 簽章格式不正確
                        - 公鑰與私鑰不匹配
                        - JWT 被修改過
                        """);
            }
            return isValid;
        } catch (Exception e) {
            log.warn("Invalid jwt verify : {}", e.getMessage());
            throw new IllegalArgumentException("Invalid jwt verify");
        }
    }

    /**
     * 從 DID Key 提取公鑰
     */
    public PublicKey extractPublicKeyFromDid(String didKey) throws Exception {
        log.info("從 DID Key 提取公鑰...");
        if (!didKey.startsWith("did:key:z")) {
            throw new IllegalArgumentException("Invalid did:key format");
        }

        String base58 = didKey.substring("did:key:z".length());
        byte[] prefixedBytes = decodeBase58(base58);

        // 檢查 multicodec prefix: 0xD1, 0xD6, 0x03
        if (prefixedBytes.length >= 3 && 
            (prefixedBytes[0] & 0xFF) == 0xD1 && 
            (prefixedBytes[1] & 0xFF) == 0xD6 && 
            (prefixedBytes[2] & 0xFF) == 0x03) {

            // 移除 prefix，只取公鑰部分
            byte[] pubBytes = new byte[prefixedBytes.length - 3];
            System.arraycopy(prefixedBytes, 3, pubBytes, 0, pubBytes.length);

            // 檢查是否為 JWK 格式
            String pubJson = new String(pubBytes);
            if (pubJson.startsWith("{")) {
                // JWK 格式，轉換為 ECKey
                JWK jwk = JWK.parse(pubJson);
                if (jwk instanceof ECKey) {
                    return ((ECKey) jwk).toPublicKey();
                } else {
                    throw new IllegalArgumentException("JWK is not an EC key");
                }
            } else {
                // X.509 格式
                java.security.spec.X509EncodedKeySpec spec = new java.security.spec.X509EncodedKeySpec(pubBytes);
                java.security.KeyFactory kf = java.security.KeyFactory.getInstance("EC", "BC");
                return kf.generatePublic(spec);
            }
        } else {
            throw new IllegalArgumentException("Unsupported multicodec prefix");
        }
    }

    /**
     * Base58 解碼
     */
    private static byte[] decodeBase58(String input) {
        if (input.length() == 0) {
            return new byte[0];
        }

        final String BASE58_ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";

        // Convert from base-58 to base-256
        int[] digits = new int[input.length()];
        for (int i = 0; i < input.length(); i++) {
            int c = input.charAt(i);
            int digit = BASE58_ALPHABET.indexOf(c);
            if (digit == -1) {
                throw new IllegalArgumentException("Invalid Base58 character: " + c);
            }
            digits[i] = digit;
        }

        // Convert to base-256
        int[] base256Digits = new int[input.length()];
        int base256DigitsLength = 0;

        for (int i = 0; i < digits.length; i++) {
            int carry = digits[i];
            for (int j = 0; j < base256DigitsLength; j++) {
                carry += base256Digits[j] * 58;
                base256Digits[j] = carry % 256;
                carry /= 256;
            }
            while (carry > 0) {
                base256Digits[base256DigitsLength++] = carry % 256;
                carry /= 256;
            }
        }

        // Convert to bytes
        byte[] result = new byte[base256DigitsLength];
        for (int i = 0; i < base256DigitsLength; i++) {
            result[i] = (byte) base256Digits[base256DigitsLength - 1 - i];
        }

        // Add leading zeros
        int leadingZeros = 0;
        for (int i = 0; i < input.length() && input.charAt(i) == '1'; i++) {
            leadingZeros++;
        }

        if (leadingZeros > 0) {
            byte[] resultWithZeros = new byte[result.length + leadingZeros];
            System.arraycopy(result, 0, resultWithZeros, leadingZeros, result.length);
            return resultWithZeros;
        }

        return result;
    }

}