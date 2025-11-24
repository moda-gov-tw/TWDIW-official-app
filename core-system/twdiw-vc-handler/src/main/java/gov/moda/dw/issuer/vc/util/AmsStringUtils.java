package gov.moda.dw.issuer.vc.util;

import java.util.Optional;
import java.util.regex.Pattern;

public class AmsStringUtils {

    public static String trimString(String str, int length) {
        if (str == null) {
            return null;
        }
        return str.substring(0, Math.min(str.length(), length));
    }
}
