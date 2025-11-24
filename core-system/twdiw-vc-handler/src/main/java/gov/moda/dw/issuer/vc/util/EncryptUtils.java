package gov.moda.dw.issuer.vc.util;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

@Slf4j
public class EncryptUtils {

  public static String gainSHA256(String input) {
    String toReturn = null;
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      digest.reset();
      digest.update(input.getBytes(StandardCharsets.UTF_8));
      toReturn = String.format("%064x", new BigInteger(1, digest.digest()));
    } catch (Exception e) {
      log.error("[EncryptUtils]input data gainSHA256 fails. :{}", ExceptionUtils.getStackTrace(e));
    }

    return toReturn;
  }

  public static String gainSHA512(String input) {
    String toReturn = null;
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-512");
      digest.reset();
      digest.update(input.getBytes(StandardCharsets.UTF_8));
      toReturn = String.format("%0128x", new BigInteger(1, digest.digest()));
    } catch (Exception e) {
      log.error("[EncryptUtils]input data gainSHA512 fails. :{}", ExceptionUtils.getStackTrace(e));
    }

    return toReturn;
  }

  public static String generateRandomString(int len) {
    return RandomStringUtils.randomNumeric(len);
  }

  public static void main(String[] args) {
    String[] userList = new String[] { "leo_lu", "admin", "navien", "a123456789", "system", "A147684589", "user" };
    for (String userName : userList) {
      log.info("{} sha512 result = {}", userName, EncryptUtils.gainSHA512(userName.toUpperCase()));
      log.info("{} sha256 result = {}", userName, EncryptUtils.gainSHA256(userName.toUpperCase()));
    }
    log.info("generateRandomString result = {}", generateRandomString(7));
  }
}
