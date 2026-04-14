package gov.moda.dw.manager.util;

import gov.moda.dw.manager.web.rest.errors.AmsBadCredentialsException;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RSAUtils {

    public static final String CHARSET = "UTF-8";
    public static final String RSA_ALGORITHM = "RSA";

    public static final String RSA_PUBLIC_KEY = "publicKey";

    public static final String RSA_PRIVATE_KEY = "privateKey";

    public static final String TRANSFORMATION = "RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING";

    //    private static Map<String, String> keyPairMap = new HashMap<>();

    /**
     * 產生金鑰對x
     * @param keySize
     * @return
     */
    public static Map<String, String> createKeys(int keySize) {
        //為RSA演算法建立一個KeyPairGenerator物件
        KeyPairGenerator kpg;
        try {
            kpg = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            log.error("No such algorithm-->[" + RSA_ALGORITHM + "]");
            throw new IllegalArgumentException("No such algorithm-->[" + RSA_ALGORITHM + "]");
        }
        //初始化KeyPairGenerator物件,金鑰長度
        kpg.initialize(keySize);

        //生成密匙對
        KeyPair keyPair = kpg.generateKeyPair();
        //得到公鑰
        Key publicKey = keyPair.getPublic();
        String publicKeyStr = java.util.Base64.getEncoder().encodeToString(publicKey.getEncoded());
        //得到私鑰
        Key privateKey = keyPair.getPrivate();
        log.debug("私鑰格式: {} " + privateKey.getFormat());
        String privateKeyStr = java.util.Base64.getEncoder().encodeToString(privateKey.getEncoded());
        Map<String, String> keyPairMap = new HashMap<>();
        keyPairMap.put(RSA_PUBLIC_KEY, publicKeyStr);
        keyPairMap.put(RSA_PRIVATE_KEY, privateKeyStr);

        return keyPairMap;
    }

    /**
     * 得到公鑰
     *
     * @param publicKey 金鑰字串（經過base64編碼）
     * @throws Exception
     */
    public static RSAPublicKey getPublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //通過X509編碼的Key指令獲得公鑰物件
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(java.util.Base64.getDecoder().decode(publicKey));
        RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
        return key;
    }

    /**
     * 得到私鑰
     *
     * @param privateKey 金鑰字串（經過base64編碼）
     * @throws Exception
     */
    public static RSAPrivateKey getPrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //通過PKCS#8編碼的Key指令獲得私鑰物件
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(java.util.Base64.getDecoder().decode(privateKey));
        RSAPrivateKey key = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
        return key;
    }

    /**
     * 公鑰加密
     *
     * @param data
     * @param publicKey
     * @return
     */
    public static String publicEncrypt(String data, RSAPublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            OAEPParameterSpec oaepParameterSpec = new OAEPParameterSpec(
                "SHA-256",
                "MGF1",
                MGF1ParameterSpec.SHA256,
                PSource.PSpecified.DEFAULT
            );
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            //            return java.util.Base64.getEncoder()
            //                .encodeToString(
            //                    rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(Charset.defaultCharset()), publicKey.getModulus().bitLength())
            //                );
            return Base64.encodeBase64String(cipher.doFinal(data.getBytes(CHARSET)));
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new AmsBadCredentialsException("encryptFail", "解密字串[" + data + "]時遇到異常");
        }
    }

    /**
     * 私鑰解密
     *
     * @param data
     * @param privateKey
     * @return
     */
    public static String privateDecrypt(String data, RSAPrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            OAEPParameterSpec oaepParameterSpec = new OAEPParameterSpec(
                "SHA-256",
                "MGF1",
                MGF1ParameterSpec.SHA256,
                PSource.PSpecified.DEFAULT
            );
            cipher.init(Cipher.DECRYPT_MODE, privateKey, oaepParameterSpec);
            //            return new String(
            //                rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, java.util.Base64.getDecoder().decode(data), privateKey.getModulus().bitLength()),
            //                Charset.defaultCharset()
            //            );
            return new String(cipher.doFinal(java.util.Base64.getDecoder().decode(data)));
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new AmsBadCredentialsException("decryptFail", "解密字串[" + data + "]時遇到異常");
        }
    }

    /**
     * 私鑰加密
     *
     * @param data
     * @param privateKey
     * @return
     */
    public static String privateEncrypt(String data, RSAPrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            OAEPParameterSpec oaepParameterSpec = new OAEPParameterSpec(
                "SHA-256",
                "MGF1",
                MGF1ParameterSpec.SHA256,
                PSource.PSpecified.DEFAULT
            );
            cipher.init(Cipher.ENCRYPT_MODE, privateKey, oaepParameterSpec);
            //            return Base64.encodeBase64URLSafeString(
            //                rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(Charset.defaultCharset()), privateKey.getModulus().bitLength())
            //            );
            return Base64.encodeBase64String(cipher.doFinal(data.getBytes(CHARSET)));
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new AmsBadCredentialsException("encryptFail", "解密字串[" + data + "]時遇到異常");
        }
    }

    public static String privateDecrypt(String data, String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return privateDecrypt(data, getPrivateKey(privateKey));
    }

    /**
     * 公鑰解密
     *
     * @param data
     * @param publicKey
     * @return
     */
    public static String publicDecrypt(String data, RSAPublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            OAEPParameterSpec oaepParameterSpec = new OAEPParameterSpec(
                "SHA-256",
                "MGF1",
                MGF1ParameterSpec.SHA256,
                PSource.PSpecified.DEFAULT
            );
            cipher.init(Cipher.DECRYPT_MODE, publicKey, oaepParameterSpec);
            //            return new String(
            //                rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, java.util.Base64.getDecoder().decode(data), publicKey.getModulus().bitLength()),
            //                Charset.defaultCharset()
            //            );
            return new String(cipher.doFinal(java.util.Base64.getDecoder().decode(data)));
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new AmsBadCredentialsException("decryptFail", "解密字串[" + data + "]時遇到異常");
        }
    }
    //    private static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] datas, int keySize) {
    //        int maxBlock = 0;
    //        if (opmode == Cipher.DECRYPT_MODE) {
    //            maxBlock = keySize / 8;
    //        } else {
    //            maxBlock = keySize / 8 - 11;
    //        }
    //        ByteArrayOutputStream out = new ByteArrayOutputStream();
    //        int offSet = 0;
    //        byte[] buff;
    //        try {
    //            while (datas.length > offSet) {
    //                if (datas.length - offSet > maxBlock) {
    //                    buff = cipher.doFinal(datas, offSet, maxBlock);
    //                } else {
    //                    buff = cipher.doFinal(datas, offSet, datas.length - offSet);
    //                }
    //                out.write(buff, 0, buff.length);
    //                offSet += maxBlock;
    //            }
    //        } catch (Exception e) {
    //            throw new RuntimeException("加解密閥值為[" + maxBlock + "]的資料時發生異常", e);
    //        }
    //        byte[] resultDatas = out.toByteArray();
    //        IOUtils.closeQuietly(out);
    //        return resultDatas;
    //    }
}
