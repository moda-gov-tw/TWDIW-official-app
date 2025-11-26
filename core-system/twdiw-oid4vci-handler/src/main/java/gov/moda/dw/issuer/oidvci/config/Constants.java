package gov.moda.dw.issuer.oidvci.config;

/**
 * Application constants.
 */
public final class Constants {

    // Regex for acceptable logins
    public static final String LOGIN_REGEX = "^(?>[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*)|(?>[_.@A-Za-z0-9-]+)$";

    public static final String SYSTEM = "system";
    public static final String DEFAULT_LANGUAGE = "zh-tw";

    public static final String version = "20250915";

    public static final String DEFAULT_HTTP_ERROR_MESSAGE_FORMAT =
        "call %s HTTP error (http=%s, content=%s)";

    private Constants() {}
}
