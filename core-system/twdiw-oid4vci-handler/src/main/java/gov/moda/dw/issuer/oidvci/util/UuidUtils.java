package gov.moda.dw.issuer.oidvci.util;

import org.apache.commons.text.RandomStringGenerator;

/** 基於 base64 編碼縮短後的 uuid */
public class UuidUtils {

    private static final String DATE_FORMAT = "yyyyMMddHHmmss";

    public static String randomWithDateAndId() {
        return randomWithDateAndId(7);
    }

    public static String randomWithDateAndId(int length) {
        return DateUtils.getDate(DATE_FORMAT) + "-" + getRandomString(length);
    }

    public static String getRandomString(int length) {
        char[][] pairs = { { 'a', 'z' }, { 'A', 'Z' }, { '0', '9' } };

        return new RandomStringGenerator.Builder().withinRange(pairs).build().generate(length);
    }
}
