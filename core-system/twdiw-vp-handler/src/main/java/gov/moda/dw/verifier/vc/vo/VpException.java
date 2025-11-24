package gov.moda.dw.verifier.vc.vo;

import gov.moda.dw.verifier.vc.service.dto.ErrorResponseProperty;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.http.HttpStatus;

public class VpException extends Exception implements ErrorResponseProperty {

    // basic
    public static final int UNKNOWN = 99999;
    public static final int ERR_ILLEGAL_ARGUMENT = 70001;

    // presentation
    public static final int ERR_PRES_INVALID_PRESENTATION_VALIDATION_REQUEST = 71001;
    public static final int ERR_PRES_VALIDATE_VP_ERROR = 71002;
    public static final int ERR_PRES_VALIDATE_VP_CONTENT_ERROR = 71003;
    public static final int ERR_PRES_VALIDATE_VP_PROOF_ERROR = 71004;
    public static final int ERR_PRES_LACK_OF_HOLDER_PUBLIC_KEY = 71005;
    public static final int ERR_PRES_HOLDER_PUBLIC_KEY_INCONSISTENT = 71006;

    // credential
    public static final int ERR_CRED_VALIDATE_VC_CONTENT_ERROR = 72001;
    public static final int ERR_CRED_VALIDATE_VC_SCHEMA_ERROR = 72002;
    public static final int ERR_CRED_VALIDATE_VC_PROOF_ERROR = 72003;
    public static final int ERR_CRED_VALIDATE_VC_STATUS_ERROR = 72004;
    public static final int ERR_CRED_LACK_OF_ISSUER_PUBLIC_KEY = 72005;
    public static final int ERR_CRED_INVALID_ISSUER_DID_FORMAT = 72006;
    public static final int ERR_CRED_INVALID_ISSUER_DID_STATUS = 72007;
    public static final int ERR_CRED_LACK_OF_SUB = 72008;

    // status list
    public static final int ERR_SL_VALIDATE_STATUS_LIST_ERROR = 73001;
    public static final int ERR_SL_VALIDATE_STATUS_LIST_CONTENT_ERROR = 73002;
    public static final int ERR_SL_VALIDATE_STATUS_LIST_PROOF_ERROR = 73003;
    public static final int ERR_SL_LACK_OF_ISSUER_PUBLIC_KEY = 73004;

    // DID
    public static final int ERR_DID_FRONTEND_QUERY_DID_ERROR = 74001;

    // connect
    public static final int ERR_CONN_LOAD_ISSUER_STATUS_LIST_ERROR = 77001;
    public static final int ERR_CONN_LOAD_ISSUER_SCHEMA_ERROR = 77002;
    public static final int ERR_CONN_LOAD_ISSUER_PUBLIC_KEY_ERROR = 773003;
    public static final int ERR_CONN_INVALID_ISSUER_STATUS_LIST = 77004;
    public static final int ERR_CONN_INVALID_ISSUER_SCHEMA = 77005;
    public static final int ERR_CONN_INVALID_ISSUER_PUBLIC_KEY = 77006;
    public static final int ERR_CONN_NO_MATCHED_ISSUER_PUBLIC_KEY = 77007;

    // database
    public static final int ERR_DB_QUERY_ERROR = 78001;
    public static final int ERR_DB_INSERT_ERROR = 78002;
    public static final int ERR_DB_UPDATE_ERROR = 78003;

    private int code;
    private String message;

    /**
     * default constructor
     */
    public VpException() {
        super();
    }

    /**
     * create new exception with message.
     *
     * @param message error message
     */
    public VpException(final String message) {
        super(message);
    }

    /**
     * create new exception with message and cause.
     *
     * @param message error message
     * @param cause throwable cause
     */
    public VpException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * create new exception with code and message.
     *
     * @param code error code
     * @param message error message
     */
    public VpException(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * create new exception with code, message and cause.
     *
     * @param code error code
     * @param message error message
     * @param cause throwable cause
     */
    public VpException(final int code, final String message, final Throwable cause) {
        super("[" + code + "] " + message, cause);
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HttpStatus toHttpStatus() {

        return switch (code) {

            // 400
            case ERR_PRES_INVALID_PRESENTATION_VALIDATION_REQUEST -> HttpStatus.BAD_REQUEST;

//            // 403
//            case ERR_ACL_INVALID_API_KEY -> HttpStatus.FORBIDDEN;

            // 500
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }

    @Override
    public String getResponseMessage() {
        return Jsoup.clean(this.message, Safelist.none());
    }

    @Override
    public int getResponseCode() {
        return getCode();
    }
}
