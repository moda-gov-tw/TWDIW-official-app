package gov.moda.dw.issuer.vc.util;

public class StringTrimUtils {

    public static String limit2000Size(String sourceStr) {
        return AmsStringUtils.trimString(sourceStr, 2000);
    }

    public static String limit1000Size(String sourceStr) {
        return AmsStringUtils.trimString(sourceStr, 1000);
    }

    public static String limit200Size(String sourceStr) {
        return AmsStringUtils.trimString(sourceStr, 200);
    }

    public static String limit10Size(String sourceStr) {
        return AmsStringUtils.trimString(sourceStr, 10);
    }

    public static String limit20Size(String sourceStr) {
        return AmsStringUtils.trimString(sourceStr, 20);
    }
}
