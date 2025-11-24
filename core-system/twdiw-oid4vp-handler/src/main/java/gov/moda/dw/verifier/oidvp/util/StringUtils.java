package gov.moda.dw.verifier.oidvp.util;

public class StringUtils {
  
    public static String removeIllegalChars(String s) {
        if (s == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (isLegal(c)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static boolean isLegal(char c) {
        // https://tools.ietf.org/html/rfc6749#section-5.2
        //
        // Values for the "error" parameter MUST NOT include characters outside the
        // set %x20-21 / %x23-5B / %x5D-7E.
        //
        // Values for the "error_description" parameter MUST NOT include characters
        // outside the set %x20-21 / %x23-5B / %x5D-7E.
        if (c > 0x7f) {
            // Not ASCII
            return false;
        }
        return c >= 0x20 && c <= 0x21 || c >= 0x23 && c <= 0x5b || c >= 0x5d && c <= 0x7e;
    }
}
