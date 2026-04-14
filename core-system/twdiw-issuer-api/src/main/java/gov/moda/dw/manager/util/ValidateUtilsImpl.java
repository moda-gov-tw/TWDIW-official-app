package gov.moda.dw.manager.util;

import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

/**
 * @author AlexChang
 * @create 2024/08/09
 * @description
 */
@Component
public class ValidateUtilsImpl implements ValidateUtils {

    private static final String PHONE_REGEX = "^\\+?[1-9]\\d{1,14}$"; // Basic international phone number pattern
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final String CELL_PHONE_REGEX = "^\\+?\\d{10,15}$"; // Basic cell phone number pattern

    private final Pattern phonePattern = Pattern.compile(PHONE_REGEX);
    private final Pattern emailPattern = Pattern.compile(EMAIL_REGEX);
    private final Pattern cellPhonePattern = Pattern.compile(CELL_PHONE_REGEX);

    @Override
    public boolean isPhone(String s) {
        if (s == null) {
            return false;
        }
        return phonePattern.matcher(s).matches();
    }

    @Override
    public boolean isEmail(String s) {
        if (s == null) {
            return false;
        }
        return emailPattern.matcher(s).matches();
    }

    @Override
    public boolean isCellPhone(String s) {
        if (s == null) {
            return false;
        }
        return cellPhonePattern.matcher(s).matches();
    }
}
