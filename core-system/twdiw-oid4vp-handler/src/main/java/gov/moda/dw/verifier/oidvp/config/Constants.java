package gov.moda.dw.verifier.oidvp.config;

/**
 * Application constants.
 */
public final class Constants {

    // Regex for acceptable logins
    public static final String LOGIN_REGEX = "^(?>[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*)|(?>[_.@A-Za-z0-9-]+)$";

    public static final String SYSTEM = "system";
    public static final String DEFAULT_LANGUAGE = "zh-tw";
    public static final String DEFAULT_ZONE_ID = "Asia/Taipei";

    // oidvp service version
    public static final String version = "20250829.2";

    private Constants() {}
}
