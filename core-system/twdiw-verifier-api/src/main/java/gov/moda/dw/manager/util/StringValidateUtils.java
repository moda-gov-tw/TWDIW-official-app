package gov.moda.dw.manager.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringValidateUtils {

    public static boolean isValidIdString(String input) {
        String regex = "^[a-zA-Z0-9$@_-]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }
}
