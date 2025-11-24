package gov.moda.dw.issuer.vc.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;

/**
 * random generator
 *
 * @version 20240902
 */
public class RandUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(RandUtils.class);

    // number (0-9) + alphabet (A-Z) => 36 characters
    private static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * generate random string, only contain number (0-9) and alphabet (A-Z)
     *
     * @param length the length of string
     * @return random string
     */
    public static String generateString(int length) {

        StringBuilder result = new StringBuilder(length);
        // for multi-thread environment
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < length; i++) {
            result.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }

        return result.toString();
    }
}
