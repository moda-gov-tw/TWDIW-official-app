package gov.moda.dw.issuer.vc.vo;

import gov.moda.dw.issuer.vc.service.dto.ErrorResponseProperty;
import org.springframework.http.HttpStatus;

/**
 * vc exception
 *
 * @version 20240902
 */
public class VcException extends Exception implements ErrorResponseProperty {

    // basic
    public static final int UNKNOWN = 99999;

    // credential
    public static final int ERR_CRED_INVALID_CREDENTIAL_GENERATION_REQUEST = 61001;
    public static final int ERR_CRED_GENERATE_VC_ERROR = 61002;
    public static final int ERR_CRED_PREPARE_VC_ERROR = 61003;
    public static final int ERR_CRED_SIGN_VC_ERROR = 61004;
    public static final int ERR_CRED_VERIFY_VC_ERROR = 61005;
    public static final int ERR_CRED_INVALID_CREDENTIAL_ID = 61006;
    public static final int ERR_CRED_REVOKE_VC_ERROR = 61007;
    public static final int ERR_CRED_PUSH_NOTIFY_ERROR = 61008;
    public static final int ERR_CRED_PUSH_RETURN_ERROR = 61009;
    public static final int ERR_CRED_CREDENTIAL_NOT_FOUND = 61010;
    public static final int ERR_CRED_QUERY_VC_ERROR = 61011;
    public static final int ERR_CRED_INVALID_NONCE = 61012;
    public static final int ERR_CRED_DEMO_GET_DATA_ERROR = 61013;
    public static final int ERR_CRED_DEMO_RETURN_ERROR = 61014;
    public static final int ERR_CRED_INVALID_CREDENTIAL_TYPE = 61015;
    public static final int ERR_CRED_VC_DATA_SOURCE_NOT_SET = 61016;
    public static final int ERR_CRED_GET_TOKEN_ERROR = 61017;
    public static final int ERR_CRED_GET_TOKEN_RETURN_ERROR = 61018;
    public static final int ERR_CRED_TRANSFER_VC_ERROR = 61019;
    public static final int ERR_CRED_INVALID_CREDENTIAL_TRANSFER_REQUEST = 61020;
    public static final int ERR_CRED_CONNECT_VP_ERROR = 61021;
    public static final int ERR_CRED_VP_RETURN_ERROR = 61022;
    public static final int ERR_CRED_VP_RETURN_VCS_ERROR = 61023;
    public static final int ERR_CRED_VP_RETURN_TYPE_ERROR = 61024;
    public static final int ERR_CRED_CALL_CRED_DATA_SERVICE_ERROR = 61025;
    public static final int ERR_CRED_CRED_DATA_SERVICE_RETURN_ERROR = 61026;
    public static final int ERR_CRED_TRANSFER_VC_NOT_ALLOWED_ERROR = 61027;
    public static final int ERR_CRED_SIGN_IDT_ERROR = 61028;
    public static final int ERR_CRED_INVALID_CREDENTIAL_ISSUER_IDENTIFIER = 61029;
    public static final int ERR_CRED_INVALID_CREDENTIAL_SUBJECT = 61030;
    public static final int ERR_CRED_CONNECT_OIDVCI_ERROR = 61031;
    public static final int ERR_CRED_OIDVCI_RETURN_ERROR = 61032;
    public static final int ERR_CRED_GET_HOLDER_DATA_ERROR = 61033;
    public static final int ERR_CRED_INVALID_TRANSFER_DATA = 61034;
    public static final int ERR_CRED_INVALID_TRANSFER_DATA_CREDENTIAL = 61035;
    public static final int ERR_CRED_INVALID_TRANSFER_DATA_CREDENTIAL_SCHEMA = 61036;
    public static final int ERR_CRED_CALL_REVOKE_SERVICE_ERROR = 61037;
    public static final int ERR_CRED_REVOKE_SERVICE_RETURN_ERROR = 61038;
    public static final int ERR_CRED_VP_RESPONSE_CONVERT_ERROR = 61039;
    public static final int ERR_CRED_VP_INVALID_ERROR = 61040;
    public static final int ERR_CRED_TX_CODE_AND_VC_TRANSFER_BOTH_TRUE_ERROR = 61041;
    public static final int ERR_CRED_INVALID_EXPIRATION_DATE = 61042;
    public static final int ERR_CRED_INVALID_ISSUANCE_DATE = 61043;
    public static final int ERR_CRED_INVALID_ISSUANCE_DATE_FORMAT = 61044;
    public static final int ERR_CRED_INVALID_EXPIRATION_DATE_FORMAT = 61045;
    public static final int ERR_CRED_PARSE_DID_ERROR = 61046;
    public static final int ERR_CRED_INVALID_DID_FORMAT = 61047;
    public static final int ERR_CRED_REVOKED_CRED_CANNOT_BE_SUSPENDED_ERROR = 61048;
    public static final int ERR_CRED_REVOKED_CRED_CANNOT_BE_RECOVERED_ERROR = 61049;
    public static final int ERR_CRED_CREDENTIAL_STATUS_UNKNOWN_ERROR = 61050;
    public static final int ERR_CRED_SUSPEND_VC_ERROR = 61051;
    public static final int ERR_CRED_RECOVER_VC_ERROR = 61052;
    
    // credential data
    public static final int ERR_CRED_DATA_INVALID_CREDENTIAL_DATA_SETTING_REQUEST = 61301;
    public static final int ERR_CRED_DATA_CREDENTIAL_DATA_CONVERT_ERROR = 61302;
    public static final int ERR_CRED_DATA_INVALID_CREDENTIAL_TYPE = 61303;
    public static final int ERR_CRED_DATA_INVALID_ISSUER_METADATA = 61304;
    public static final int ERR_CRED_DATA_INVALID_VC_SCHEMA = 61305;
    public static final int ERR_CRED_DATA_FIELDS_IN_SCHEMA_AND_METADATA_NOT_IDENTICAL = 61306;
    public static final int ERR_CRED_DATA_FIELDS_IN_SCHEMA_AND_VC_DATA_NOT_IDENTICAL = 61307;
    public static final int ERR_CRED_DATA_INVALID_DATA_FIELD = 61308;
    
    // sequence
    public static final int ERR_SEQ_INVALID_SEQUENCE_SETTING_REQUEST = 61401;
    public static final int ERR_SEQ_INVALID_CREDENTIAL_TYPE = 61402;
    public static final int ERR_SEQ_INVALID_CREDENTIAL_ISSUER_IDENTIFIER = 61403;
    public static final int ERR_SEQ_INVALID_ISSUER_METADATA = 61404;
    public static final int ERR_SEQ_PARSE_ISSUER_METADATA_ERROR = 61405;
    public static final int ERR_SEQ_KEY_DUPLICATED_IN_ISSUER_METADATA = 61406;
    public static final int ERR_SEQ_INVALID_SEQUENCE_DELETING_REQUEST = 61407;
    public static final int ERR_SEQ_INVALID_FUNCTION_SWITCH_SETTING_REQUEST = 61408;
    public static final int ERR_SEQ_TX_CODE_AND_VC_TRANSFER_BOTH_TRUE_ERROR = 61409;
    public static final int ERR_SEQ_INVALID_ISSUER_METADATA_DATA_FIELD = 61410;
    
    // setting
    public static final int ERR_SETTING_INVALID_UPDATE_SETTING_REQUEST = 61501;

    // status list
    public static final int ERR_SL_GENERATE_STATUS_LIST_ERROR = 62001;
    public static final int ERR_SL_PREPARE_STATUS_LIST_ERROR = 62002;
    public static final int ERR_SL_SIGN_STATUS_LIST_ERROR = 62003;
    public static final int ERR_SL_VERIFY_STATUS_LIST_ERROR = 62004;
    public static final int ERR_SL_QUERY_STATUS_LIST_ERROR = 62005;
    public static final int ERR_SL_INPUT_STATUS_LIST_TYPE_ERROR = 62006;
    public static final int ERR_SL_INVALID_STATUS_LIST_OPERATION_REQUEST = 62007;
    public static final int ERR_SL_RETRY_ERROR = 62008;

    // DID
    public static final int ERR_DID_FRONTEND_GENERATE_DID_ERROR = 63001;
    public static final int ERR_DID_FRONTEND_REGISTER_DID_ERROR = 63002;
    public static final int ERR_DID_FRONTEND_REVIEW_DID_ERROR = 63003;
    public static final int ERR_DID_SIGN_JWT_ERROR = 63004;
    public static final int ERR_DID_PARSE_DID_FROM_DOCUMENT_ERROR = 63005;
    public static final int ERR_DID_REGISTER_DID_ERROR = 63009;
    public static final int ERR_DID_REGISTER_DID_REQUEST = 63010;
    public static final int ERR_DID_FRONTEND_GET_ISSUER_DID_INFO_ERROR = 63011;
    public static final int ERR_DID_FRONTEND_CREATE_DID_ERROR = 63012;

    // public information
    public static final int ERR_INFO_INVALID_CREDENTIAL_TYPE = 64001;
    public static final int ERR_INFO_INVALID_GROUP_NAME = 64002;
    public static final int ERR_INFO_STATUS_LIST_NOT_FOUND = 64003;
    public static final int ERR_INFO_INVALID_SCHEMA_NAME = 64004;
    public static final int ERR_INFO_SCHEMA_NOT_FOUND = 64005;
    public static final int ERR_INFO_PUBLIC_KEY_NOT_FOUND = 64006;

    // database
    public static final int ERR_DB_QUERY_ERROR = 68001;
    public static final int ERR_DB_INSERT_ERROR = 68002;
    public static final int ERR_DB_UPDATE_ERROR = 68003;
    // sequence
    public static final int ERR_DB_INVALID_SEQUENCE_NAME = 68004;
    public static final int ERR_DB_DELETE_ERROR = 68005;

    // system
    public static final int ERR_SYS_GENERATE_KEY_ERROR = 69001;
    public static final int ERR_SYS_GENERATE_SCHEMA_ERROR = 69002;
    public static final int ERR_SYS_RELOAD_SETTING_ERROR = 69003;
    public static final int ERR_SYS_NOT_REGISTER_DID_YET_ERROR = 69004;
    public static final int ERR_SYS_ISSUER_DID_NOT_VALID_ERROR = 69005;
    public static final int ERR_SYS_GET_PRELOAD_SETTING_ERROR = 69006;
    public static final int ERR_SYS_CHECK_SETTING_ERROR = 69007;
    public static final int ERR_SYS_UPDATE_SETTING_ERROR = 69008;
    public static final int ERR_SYS_NOT_SET_FUNCTION_SWITCH_YET_ERROR = 69009;
    public static final int ERR_SYS_NOT_SET_FRONTEND_ACCESS_TOKEN_YET_ERROR = 69010;
    public static final int ERR_SYS_NOT_SET_VC_KEY_ENC_ERROR = 69011;
    public static final int ERR_SYS_DECRYPT_CIPHER_ERROR = 69012;
    public static final int ERR_SYS_DECRYPT_RESULT_NULL_ERROR = 69013;
    public static final int ERR_SYS_CHECK_KEYS_VALUE_ERROR = 69014;
    public static final int ERR_SYS_ENCRYPT_ERROR = 69015;
    public static final int ERR_SYS_NOT_SET_KEY_YET_ERROR = 69016;
    public static final int ERR_SYS_INVALID_TIME_UNIT = 69017;
    public static final int ERR_SYS_INVALID_TIME_DURATION = 69018;

    private int code;
    private String message;

    /**
     * default constructor
     */
    public VcException() {
        super();
    }

    /**
     * create new exception with message.
     *
     * @param message error message
     */
    public VcException(final String message) {
        super(message);
    }

    /**
     * create new exception with message and cause.
     *
     * @param message error message
     * @param cause throwable cause
     */
    public VcException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * create new exception with code and message.
     *
     * @param code error code
     * @param message error message
     */
    public VcException(final int code, final String message) {
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
    public VcException(final int code, final String message, final Throwable cause) {
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

    @Override
    public String getResponseMessage(){
        return message;
    }

    @Override
    public int getResponseCode(){
        return code;
    }

    public HttpStatus toHttpStatus() {

        return switch (code) {

            // 400
            case ERR_CRED_INVALID_CREDENTIAL_GENERATION_REQUEST,
                 ERR_CRED_INVALID_CREDENTIAL_ID,
                 ERR_CRED_INVALID_NONCE,
                ERR_CRED_INVALID_CREDENTIAL_TYPE,
                ERR_INFO_INVALID_CREDENTIAL_TYPE,
                ERR_INFO_INVALID_GROUP_NAME,
                ERR_INFO_INVALID_SCHEMA_NAME,
                ERR_DB_INVALID_SEQUENCE_NAME,
                ERR_CRED_DATA_INVALID_CREDENTIAL_DATA_SETTING_REQUEST,
                ERR_SEQ_INVALID_SEQUENCE_SETTING_REQUEST,
                ERR_DID_REGISTER_DID_REQUEST,
                ERR_SEQ_INVALID_CREDENTIAL_TYPE,
                ERR_CRED_DATA_INVALID_CREDENTIAL_TYPE,
                ERR_SEQ_KEY_DUPLICATED_IN_ISSUER_METADATA,
                ERR_SL_INPUT_STATUS_LIST_TYPE_ERROR,
                ERR_SEQ_INVALID_SEQUENCE_DELETING_REQUEST,
                ERR_SEQ_INVALID_FUNCTION_SWITCH_SETTING_REQUEST,
                ERR_CRED_INVALID_CREDENTIAL_TRANSFER_REQUEST,
                ERR_CRED_DATA_INVALID_DATA_FIELD,
                ERR_SEQ_INVALID_ISSUER_METADATA_DATA_FIELD,
                ERR_CRED_INVALID_DID_FORMAT,
                ERR_CRED_REVOKED_CRED_CANNOT_BE_SUSPENDED_ERROR,
                ERR_CRED_REVOKED_CRED_CANNOT_BE_RECOVERED_ERROR -> HttpStatus.BAD_REQUEST;

//            // 403
//            case ERR_ACL_INVALID_API_KEY -> HttpStatus.FORBIDDEN;

            // 404
            case ERR_CRED_CREDENTIAL_NOT_FOUND,
                ERR_INFO_STATUS_LIST_NOT_FOUND,
                ERR_INFO_SCHEMA_NOT_FOUND,
                ERR_INFO_PUBLIC_KEY_NOT_FOUND -> HttpStatus.NOT_FOUND;

            // 500
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
