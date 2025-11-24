package gov.moda.dw.verifier.oidvp.common;

import org.springframework.http.HttpStatus;

public class OidvpErrorCode {

    // code
    public static final int CODE_SUCCESS = 0;

    public static final int CODE_CALL_VP_SERVICE_ERROR = 2000;
    public static final int CODE_AUTHZ_RESPONSE_ERROR = 2001;
    public static final int CODE_INVALID_VP = 2002;
    public static final int CODE_CREDENTIAL_NUMBER_NOT_MATCH = 2003;
    public static final int CODE_VP_VALIDATE_FAIL = 2004;
    public static final int CODE_ID_NONCE_NOT_MATCH = 2005;
    public static final int CODE_VC_NOT_MATCH_PRESENTATION_DEFINITION = 2006;
    public static final int CODE_PS_NOT_MATCH_PD = 2007;
    public static final int CODE_VC_CLAIM_NOT_MATCH_FILTER = 2008;
    public static final int CODE_CLAIM_NOT_FOUND = 2009;
    public static final int CODE_INPUT_DESCRIPTORS_ID_NOT_UNIQUE = 2010;
    public static final int CODE_INVALID_PRESENTATION_DEFINITION = 2011;
    public static final int CODE_INVALID_PRESENTATION_SUBMISSION = 2012;
    public static final int CODE_VC_NOT_MATCH_PRESENTATION_SUBMISSION = 2013;
    public static final int CODE_INVALID_SUBMISSION_REQUIREMENT = 2014;
    public static final int CODE_INVALID_VP_FORMAT = 2015;
    public static final int CODE_INVALID_VC = 2016;
    public static final int CODE_INVALID_VC_FORMAT = 2017;
    public static final int CODE_VC_NOT_SUPPORT_LIMIT_DISCLOSURE = 2018;
    public static final int CODE_VP_RESPONSE_ERROR = 2019;

    public static final int CODE_BAD_OIDVP_PARAM = 3000;
    public static final int CODE_GET_METADATA_ERROR = 3001;
    public static final int CODE_GENERATE_KEY_ERROR = 3002;

    public static final int CODE_INVALID_PARAMETERS = 4001;
    public static final int CODE_RESULT_NOT_FOUND = 4002;
    public static final int CODE_INVALID_SESSION = 4003;

    public static final int CODE_SERVER_ERROR = 5000;
    public static final int CODE_DB_ERROR = 5001;
    public static final int CODE_SIGN_REQUEST_OBJECT_ERROR = 5002;
    public static final int CODE_GEN_QRCODE_ERROR = 5003;
    public static final int CODE_JSON_PROCESS_ERROR = 5004;
    public static final int CODE_VERIFY_FAIL = 5005;
    public static final int CODE_JWK_STORE_OPERATION_ERROR = 5006;
    public static final int CODE_OIDVP_CLIENT_ID_NOT_MATCH_ERROR = 5007;

    public static final int CODE_CALL_FRONTEND_GENERATE_DID_ERROR = 6001;
    public static final int CODE_CALL_FRONTEND_REGISTER_DID_ERROR = 6002;
    public static final int CODE_CALL_FRONTEND_REVIEW_DID_ERROR = 6003;
    public static final int CODE_PARSE_DID_FROM_DOCUMENT_ERROR = 6004;
    public static final int CODE_SIGN_DID_DOCUMENT_ERROR = 6005;
    public static final int CODE_GET_DID_ERROR = 6006;
    public static final int CODE_REGISTER_DID_ERROR = 6007;
    public static final int CODE_PARSE_DID_ERROR = 6008;
    public static final int CODE_CALL_FRONTEND_CREATE_DID_ERROR = 6009;

    public static final int CODE_CALL_VERIFIER_BUSINESS_CALLBACK_ERROR = 7001;
    public static final int CODE_VERIFIER_BUSINESS_CALLBACK_RESPONSE_ERROR = 7002;
    public static final int CODE_INVALID_CUSTOM_DATA = 7003;

    
    // message
    public static final String MSG_SUCCESS = "success";

    public static final String MSG_CALL_VP_SERVICE_ERROR = "call vp service error";
    public static final String MSG_AUTHZ_RESPONSE_ERROR = "authorization response return error";
    public static final String MSG_INVALID_VP = "invalid vp token";
    public static final String MSG_CREDENTIAL_NUMBER_NOT_MATCH = "the number of credential is not match to the presentation_submission";
    public static final String MSG_VP_VALIDATE_FAIL = "vp validate fail";
    public static final String MSG_ID_NONCE_NOT_MATCH = "client_id or nonce is not the expect value";
    public static final String MSG_VC_NOT_MATCH_PRESENTATION_DEFINITION = "vc is not meet the criteria in the presentation_definition";
    public static final String MSG_PS_NOT_MATCH_PD = "presentation_submission is not matched presentation_definition";
    public static final String MSG_VC_CLAIM_NOT_MATCH_FILTER = "vc claim is not matched the requirement of filter";
    public static final String MSG_CLAIM_NOT_FOUND = "can not find required claim in vc";
    public static final String MSG_INPUT_DESCRIPTORS_ID_NOT_UNIQUE = "input_descriptors's id is not unique";
    public static final String MSG_INVALID_PRESENTATION_DEFINITION = "invalid presentation_definition";
    public static final String MSG_INVALID_PRESENTATION_SUBMISSION = "invalid presentation_submission";
    public static final String MSG_VC_NOT_MATCH_PRESENTATION_SUBMISSION = "vc and presentation_submission do not match";
    public static final String MSG_INVALID_SUBMISSION_REQUIREMENT = "invalid submission requirement";
    public static final String MSG_INVALID_VP_FORMAT = "invalid vp format";
    public static final String MSG_INVALID_VC = "invalid vc";
    public static final String MSG_INVALID_VC_FORMAT = "invalid vc format";
    public static final String MSG_VC_NOT_SUPPORT_LIMIT_DISCLOSURE = "vc is not support limit_disclosure";
    public static final String MSG_VP_RESPONSE_ERROR = "vp response error";

    public static final String MSG_BAD_OIDVP_PARAM = "bad oidvp parameter";
    public static final String MSG_GET_METADATA_ERROR = "get metadata error";
    public static final String MSG_GENERATE_KEY_ERROR = "generate key error";

    public static final String MSG_INVALID_PARAMETERS = "invalid or missing request parameter";
    public static final String MSG_RESULT_NOT_FOUND = "result not found";
    public static final String MSG_INVALID_SESSION = "invalid session";

    public static final String MSG_SERVER_ERROR = "internal server error";
    public static final String MSG_DB_ERROR = "database error";
    public static final String MSG_SIGN_REQUEST_OBJECT_ERROR = "sign request object error";
    public static final String MSG_GEN_QRCODE_ERROR = "generate qr code error";
    public static final String MSG_JSON_PROCESS_ERROR = "json process error";
    public static final String MSG_VERIFY_FAIL = "verify fail";
    public static final String MSG_JWK_STORE_OPERATION_ERROR = "jwk store operation error";
    public static final String MSG_OIDVP_CLIENT_ID_NOT_MATCH_ERROR = "OIDVP client id not match";

    public static final String MSG_CALL_FRONTEND_GENERATE_DID_ERROR = "call frontend service generate DID fail";
    public static final String MSG_CALL_FRONTEND_REGISTER_DID_ERROR = "call frontend service register DID fail";
    public static final String MSG_CALL_FRONTEND_REVIEW_DID_ERROR = "call frontend service review DID fail";
    public static final String MSG_SIGN_DID_DOCUMENT_ERROR = "sign DID document error";
    public static final String MSG_PARSE_DID_FROM_DOCUMENT_ERROR = "parse did from document error";
    public static final String MSG_GET_DID_ERROR = "get DID error";
    public static final String MSG_REGISTER_DID_ERROR = "register did error";
    public static final String MSG_PARSE_DID_ERROR = "parse did error";
    public static final String MSG_CALL_FRONTEND_CREATE_DID_ERROR = "call frontend service create DID fail";

    public static final String MSG_CALL_VERIFIER_BUSINESS_CALLBACK_ERROR = "call verifier business callback fail";
    public static final String MSG_VERIFIER_BUSINESS_CALLBACK_RESPONSE_ERROR = "verifier business callback response error";
    public static final String MSG_INVALID_CUSTOM_DATA = "invalid custom data";

    public enum OidvpError {
        SUCCESS(CODE_SUCCESS, MSG_SUCCESS, HttpStatus.OK),

        BAD_OIDVP_PARAM(CODE_BAD_OIDVP_PARAM, MSG_BAD_OIDVP_PARAM, HttpStatus.INTERNAL_SERVER_ERROR),

        INVALID_PARAMETERS(CODE_INVALID_PARAMETERS, MSG_INVALID_PARAMETERS, HttpStatus.BAD_REQUEST),

        RESULT_NOT_FOUND(CODE_RESULT_NOT_FOUND, MSG_RESULT_NOT_FOUND, HttpStatus.NOT_FOUND),

        DB_ERROR(CODE_DB_ERROR, MSG_DB_ERROR, HttpStatus.INTERNAL_SERVER_ERROR),

        SIGN_REQUEST_OBJECT_ERROR(CODE_SIGN_REQUEST_OBJECT_ERROR, MSG_SIGN_REQUEST_OBJECT_ERROR, HttpStatus.INTERNAL_SERVER_ERROR),

        JSON_PROCESS_ERROR(CODE_JSON_PROCESS_ERROR, MSG_JSON_PROCESS_ERROR, HttpStatus.INTERNAL_SERVER_ERROR),

        GEN_QRCODE_ERROR(CODE_GEN_QRCODE_ERROR, MSG_GEN_QRCODE_ERROR, HttpStatus.INTERNAL_SERVER_ERROR),

        AUTHZ_RESPONSE_ERROR(CODE_AUTHZ_RESPONSE_ERROR, MSG_AUTHZ_RESPONSE_ERROR, HttpStatus.BAD_REQUEST),

        VP_VALIDATE_FAIL(CODE_VP_VALIDATE_FAIL, MSG_VP_VALIDATE_FAIL, HttpStatus.INTERNAL_SERVER_ERROR),

        INVALID_VP(CODE_INVALID_VP, MSG_INVALID_VP, HttpStatus.BAD_REQUEST),

        CREDENTIAL_NUMBER_NOT_MATCH(CODE_CREDENTIAL_NUMBER_NOT_MATCH, MSG_CREDENTIAL_NUMBER_NOT_MATCH, HttpStatus.BAD_REQUEST),

        ID_NONCE_NOT_MATCH(CODE_ID_NONCE_NOT_MATCH, MSG_ID_NONCE_NOT_MATCH, HttpStatus.BAD_REQUEST),

        VC_NOT_MATCH_PRESENTATION_DEFINITION(CODE_VC_NOT_MATCH_PRESENTATION_DEFINITION, MSG_VC_NOT_MATCH_PRESENTATION_DEFINITION, HttpStatus.BAD_REQUEST),

        PS_NOT_MATCH_PD(CODE_PS_NOT_MATCH_PD, MSG_PS_NOT_MATCH_PD, HttpStatus.BAD_REQUEST),

        VC_CLAIM_NOT_MATCH_FILTER(CODE_VC_CLAIM_NOT_MATCH_FILTER, MSG_VC_CLAIM_NOT_MATCH_FILTER, HttpStatus.BAD_REQUEST),

        CLAIM_NOT_FOUND(CODE_CLAIM_NOT_FOUND, MSG_CLAIM_NOT_FOUND, HttpStatus.BAD_REQUEST),

        INPUT_DESCRIPTORS_ID_NOT_UNIQUE(CODE_INPUT_DESCRIPTORS_ID_NOT_UNIQUE, MSG_INPUT_DESCRIPTORS_ID_NOT_UNIQUE, HttpStatus.BAD_REQUEST),

        INVALID_PRESENTATION_DEFINITION(CODE_INVALID_PRESENTATION_DEFINITION, MSG_INVALID_PRESENTATION_DEFINITION, HttpStatus.BAD_REQUEST),

        INVALID_PRESENTATION_SUBMISSION(CODE_INVALID_PRESENTATION_SUBMISSION, MSG_INVALID_PRESENTATION_SUBMISSION, HttpStatus.BAD_REQUEST),

        VC_NOT_MATCH_PRESENTATION_SUBMISSION(CODE_VC_NOT_MATCH_PRESENTATION_SUBMISSION, MSG_VC_NOT_MATCH_PRESENTATION_SUBMISSION, HttpStatus.BAD_REQUEST),

        INVALID_SUBMISSION_REQUIREMENT(CODE_INVALID_SUBMISSION_REQUIREMENT, MSG_INVALID_SUBMISSION_REQUIREMENT, HttpStatus.BAD_REQUEST),

        INVALID_VP_FORMAT(CODE_INVALID_VP_FORMAT, MSG_INVALID_VP_FORMAT, HttpStatus.BAD_REQUEST),

        INVALID_VC(CODE_INVALID_VC, MSG_INVALID_VC, HttpStatus.BAD_REQUEST),

        INVALID_VC_FORMAT(CODE_INVALID_VC_FORMAT, MSG_INVALID_VC_FORMAT, HttpStatus.BAD_REQUEST),

        VC_NOT_SUPPORT_LIMIT_DISCLOSURE(CODE_VC_NOT_SUPPORT_LIMIT_DISCLOSURE, MSG_VC_NOT_SUPPORT_LIMIT_DISCLOSURE, HttpStatus.BAD_REQUEST),

        INVALID_SESSION(CODE_INVALID_SESSION, MSG_INVALID_SESSION, HttpStatus.BAD_REQUEST),

        VP_RESPONSE_ERROR(CODE_VP_RESPONSE_ERROR, MSG_VP_RESPONSE_ERROR, HttpStatus.INTERNAL_SERVER_ERROR),

        VERIFY_FAIL(CODE_VERIFY_FAIL, MSG_VERIFY_FAIL, HttpStatus.INTERNAL_SERVER_ERROR),

        JWK_STORE_OPERATION_ERROR(CODE_JWK_STORE_OPERATION_ERROR, MSG_JWK_STORE_OPERATION_ERROR, HttpStatus.INTERNAL_SERVER_ERROR),

        CALL_FRONTEND_GENERATE_DID_ERROR(CODE_CALL_FRONTEND_GENERATE_DID_ERROR, MSG_CALL_FRONTEND_GENERATE_DID_ERROR, HttpStatus.INTERNAL_SERVER_ERROR),

        CALL_FRONTEND_REGISTER_DID_ERROR(CODE_CALL_FRONTEND_REGISTER_DID_ERROR, MSG_CALL_FRONTEND_REGISTER_DID_ERROR, HttpStatus.INTERNAL_SERVER_ERROR),

        CALL_FRONTEND_REVIEW_DID_ERROR(CODE_CALL_FRONTEND_REVIEW_DID_ERROR, MSG_CALL_FRONTEND_REVIEW_DID_ERROR, HttpStatus.INTERNAL_SERVER_ERROR),

        SIGN_DID_DOCUMENT_ERROR(CODE_SIGN_DID_DOCUMENT_ERROR, MSG_SIGN_DID_DOCUMENT_ERROR, HttpStatus.INTERNAL_SERVER_ERROR),

        PARSE_DID_FROM_DOCUMENT_ERROR(CODE_PARSE_DID_FROM_DOCUMENT_ERROR, MSG_PARSE_DID_FROM_DOCUMENT_ERROR, HttpStatus.INTERNAL_SERVER_ERROR),

        GET_DID_ERROR(CODE_GET_DID_ERROR, MSG_GET_DID_ERROR, HttpStatus.INTERNAL_SERVER_ERROR),

        GENERATE_KEY_ERROR(CODE_GENERATE_KEY_ERROR, MSG_GENERATE_KEY_ERROR, HttpStatus.INTERNAL_SERVER_ERROR),

        CALL_VP_SERVICE_ERROR(CODE_CALL_VP_SERVICE_ERROR, MSG_CALL_VP_SERVICE_ERROR, HttpStatus.INTERNAL_SERVER_ERROR),

        REGISTER_DID_ERROR(CODE_REGISTER_DID_ERROR, MSG_REGISTER_DID_ERROR, HttpStatus.INTERNAL_SERVER_ERROR),

        GET_METADATA_ERROR(CODE_GET_METADATA_ERROR, MSG_GET_METADATA_ERROR, HttpStatus.INTERNAL_SERVER_ERROR),

        PARSE_DID_ERROR(CODE_PARSE_DID_ERROR, MSG_PARSE_DID_ERROR, HttpStatus.INTERNAL_SERVER_ERROR),

        OIDVP_CLIENT_ID_NOT_MATCH_ERROR(CODE_OIDVP_CLIENT_ID_NOT_MATCH_ERROR, MSG_OIDVP_CLIENT_ID_NOT_MATCH_ERROR, HttpStatus.INTERNAL_SERVER_ERROR),

        CALL_FRONTEND_CREATE_DID_ERROR(CODE_CALL_FRONTEND_CREATE_DID_ERROR, MSG_CALL_FRONTEND_CREATE_DID_ERROR, HttpStatus.INTERNAL_SERVER_ERROR),

        CALL_VERIFIER_BUSINESS_CALLBACK_ERROR(CODE_CALL_VERIFIER_BUSINESS_CALLBACK_ERROR, MSG_CALL_VERIFIER_BUSINESS_CALLBACK_ERROR, HttpStatus.INTERNAL_SERVER_ERROR),

        VERIFIER_BUSINESS_CALLBACK_RESPONSE_ERROR(CODE_VERIFIER_BUSINESS_CALLBACK_RESPONSE_ERROR, MSG_VERIFIER_BUSINESS_CALLBACK_RESPONSE_ERROR, HttpStatus.INTERNAL_SERVER_ERROR),

        INVALID_CUSTOM_DATA(CODE_INVALID_CUSTOM_DATA, MSG_INVALID_CUSTOM_DATA, HttpStatus.BAD_REQUEST),

        SERVER_ERROR(CODE_SERVER_ERROR, MSG_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);


        private final int code;
        private final String msg;
        private final HttpStatus httpStatus;

        OidvpError(int code, String msg, HttpStatus httpStatus) {
            this.code = code;
            this.msg = msg;
            this.httpStatus = httpStatus;
        }

        public static OidvpError getOidvpError(Integer errorCode) {
            if (errorCode == null) {
                throw new IllegalArgumentException("error code can not be null");
            }
            for (OidvpError error : OidvpError.values()) {
                if (error.getCode() == errorCode) {
                    return error;
                }
            }
            throw new IllegalArgumentException("Unexpected errorCode: " + errorCode);
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }

        public HttpStatus getHttpStatus() {
            return httpStatus;
        }
    }
}
