package gov.moda.dw.issuer.vc.util;

public class AmsStringUtils {

  public static String trimString(String str, int length) {
    if (str == null) {
      return null;
    }
    return str.substring(0, Math.min(str.length(), length));
  }
}
