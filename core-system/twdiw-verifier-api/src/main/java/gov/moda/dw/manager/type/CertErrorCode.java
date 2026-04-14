package gov.moda.dw.manager.type;

public enum CertErrorCode {
    VERIFY_MODULE_ERROR(501, "產生驗證模組錯誤"),
    SYSTEM_PARAM_ERROR(502, "設定系統參數錯誤"),
    VERIFY_POLICY_NOT_FOUND(503, "沒有對應的驗證原則"),

    CERTIFICATE_DISABLED(1011, "發行者憑證 (CA) 禁用"),
    CERTIFICATE_ISSUER_DISABLED(1012, "發行者憑證 (CA) 禁用"),
    CERTIFICATE_UNTRUSTED_ISSUER(1013, "憑證驗證錯誤，非 VA 信賴之 CA 核發憑證"),
    CERTIFICATE_MISSING_CAINFO(1014, "沒有設定憑證相關資訊 (CAINFO) "),
    CERTIFICATE_EXPIRED(1021, "憑證過期"),
    CERTIFICATE_NOT_VALID_YET(1022, "憑證尚未有效"),

    CERTIFICATE_REVOKED_UNKNOWN_REASON(1030, "憑證已經註銷，註銷原因未明"),
    CERTIFICATE_REVOKED_KEY_COMPROMISED(1031, "憑證已經註銷，註銷原因為憑證金鑰遭破解"),
    CERTIFICATE_REVOKED_KEY_COMPROMISED_CA(1032, "憑證已經註銷，註銷原因為 CA 憑證金鑰遭破解"),
    CERTIFICATE_REVOKED_IDENTITY_CHANGED(1033, "憑證已經註銷，註銷原因為用戶身分變更"),
    CERTIFICATE_REVOKED_REPLACED(1034, "憑證已經註銷，註銷原因為憑證已被另一張憑證取代 (憑證內容變更時使用)"),
    CERTIFICATE_REVOKED_NO_LONGER_NEEDED(1035, "憑證已經註銷，註銷原因為用戶不再需要使用憑證"),
    CERTIFICATE_REVOKED_SUSPENDED(1036, "憑證已經註銷，註銷原因為憑證被暫禁"),
    CERTIFICATE_REVOKED_AUTHORITY_REVOKED(1039, "憑證已經註銷，註銷原因為憑證授權的主體已經被撤銷"),
    CERTIFICATE_REVOKED_KEY_COMPROMISED_AC(1040, "憑證已經註銷，註銷原因為憑證金鑰遭破解 (僅用於屬性憑證)"),

    CRL_INFO_SEARCH_ERROR(1041, "搜尋可用的 CRL 資訊發生錯誤"),
    CRL_DISTRIBUTION_POINT_NOT_FOUND(1042, "資料庫中不存在 CRL 發佈點資訊"),
    CRL_DISTRIBUTION_POINT_DISABLED(1043, "資料庫中儲存的 CRL 發佈點資訊設定為不使用狀態"),
    CRL_EXPIRED(1044, "資料庫中儲存的 CRL 已過期"),
    CRL_NOT_DOWNLOADED(1045, "CRL 未被下載"),

    OCSP_REQUEST_FORMAT_ERROR(1051, "OCSP 請求格式錯誤"),
    OCSP_INTERNAL_ERROR(1052, "OCSP 內部處理發生錯誤"),
    OCSP_RETRY_LATER(1053, "OCSP 稍後重試"),
    OCSP_REQUEST_SIGNATURE_REQUIRED(1055, "OCSP 請求必須要簽章"),
    OCSP_NOT_AUTHORIZED(1056, "OCSP 尚未授權使用"),
    OCSP_RESPONSE_PARSING_ERROR(1057, "OCSP 回應解析發生錯誤"),
    OCSP_RESPONSE_VERIFICATION_ERROR(1058, "OCSP 回應驗證發生錯誤"),
    OCSP_RESPONSE_FORMAT_INVALID(1059, "OCSP 回應格式不符合"),
    OCSP_RESPONSE_CERTIFICATE_MISMATCH(1060, "OCSP 中的認證值不符"),
    OCSP_KEY_USAGE_ERROR(1061, "OCSP 使用的金鑰用途錯誤"),
    OCSP_CERTIFICATE_STATUS_UNKNOWN(1062, "OCSP 回應憑證狀態不明"),
    OCSP_CERTIFICATE_NOT_FOUND(1063, "找不到 OCSP 使用的憑證"),
    OCSP_URL_NOT_SPECIFIED(1064, "沒有指定 OCSP 的 URL"),

    CERTIFICATE_DN_IS_NULL(1070, "憑證的 DN 為 NULL"),
    REQUESTED_DN_IS_NULL(1071, "要求的 DN 為 NULL"),
    CERTIFICATE_MISSING_REQUIRED_DN(1072, "憑證沒有包含必要的 DN"),

    CERTIFICATE_UNEXPECTED_KEY_USAGE(1080, "憑證使用非預期的金鑰用途"),
    CERTIFICATE_KEY_USAGE_PARSE_ERROR(1081, "解析憑證的延伸金鑰用途欄位發生錯誤"),
    CERTIFICATE_MISSING_KEY_USAGE(1082, "憑證不存在需要的延伸金鑰用途"),

    CERTIFICATE_UNEXPECTED_POLICY(1091, "憑證使用非預期的憑證政策"),

    CERTIFICATE_OID_NOT_FOUND(1100, "憑證指定的 OID 物件不存在"),
    CERTIFICATE_SUBJECT_DIRECTORY_OID_MISSING(1101, "憑證找不到主體目錄屬性的 OID"),
    CERTIFICATE_SUBJECT_DIRECTORY_INVALID(1102, "憑證的主體目錄屬性不符合"),

    CERTIFICATE_SUBJECT_ALIAS_NOT_FOUND(1111, "找不到憑證的主體別名"),

    APPID_INFO_NOT_FOUND(1121, "找不到 APPID 資訊"),
    USER_ACCOUNT_NOT_FOUND(1122, "找不到使用者帳號"),
    USER_PASSWORD_INCORRECT(1123, "使用者的密碼不正確"),
    APPID_PARAMETER_MISSING(1124, "沒有指定 APPID 參數"),
    USER_ACCOUNT_PARAMETER_MISSING(1125, "沒有指定使用者帳號參數"),
    USER_PASSWORD_PARAMETER_MISSING(1126, "沒有指定使用者密碼參數"),

    PARAMETER_CLIENT_CERT_MISSING(1130, "未給定參數 ClientCert (HSM 對應之工商憑證)"),
    PARAMETER_KEYFILE_MISSING(1131, "未給定參數 KEYFILE (PFX 位置，僅在非 HSM -> PFX 會用到"),
    PARAMETER_PASSWORD_MISSING(1132, "未給定參數 PASSWORD (HSM 密碼錯誤"),
    PARAMETER_IP_MISSING(1133, "未給定參數 IP (ICS Verify Server IP)"),
    PARAMETER_PORT_MISSING(1134, "未給定參數 PORT (ICS Verify Server Port)"),
    PARAMETER_GCA_URL_MISSING(1135, "未給定參數 GCAURL (ICS Verify Server 用到的參數"),
    PARAMETER_SIGN_TYPE_MISSING(1136, "未給定參數 SIGNTYPE (HSM 簽章 algorithm)"),
    PARAMETER_PID_MISSING(1137, "未給定參數 PID (欲驗證之身分證字號)"),
    PARAMETER_INITIALIZATION_ERROR(1138, "初始化參數錯誤"),
    ICS_VERIFICATION_FAILED(1139, "ICS 驗證失敗，請參考” ICS 的 ErrorCode 整理 .docx"),
    ICS_OTHER_ERROR(1140, "驗證 ICS 發生其他錯誤"),

    PARAMETER_HSM_KEY_LABEL_MISSING(1141, "未給定參數 HSM key label"),
    HSM_LOAD_PRIVATE_KEY_FAILED(1142, "從 SafeNet HSM load private key 失敗"),
    PARAMETER_HSM_SLOT_MISSING(1143, "未給定參數 HSM slot"),
    HSM_SLOT_NOT_INTEGER(1144, "HSM slot 值非 integer"),
    HSM_ADD_PROVIDER_FAILED(1145, "Add SafeNet HSM Provider 失敗"),
    HSM_PASSWORD_DECRYPT_FAILED(1146, "SafeNet HSM 密碼解密失敗"),
    GROUP_CERT_TYPE_PARSE_FAILED(1147, "剖析 Group/CertType (在 SDA) 失敗"),

    PUBLIC_CA_CERTIFICATE_VALIDATION_FAILED(1148, "驗證 PublicCA 證券憑證失敗"),

    SIGNATURE_VERIFICATION_FAILED(2001, "驗簽章失敗 (非簽章格式資料或是簽章錯誤)"),
    SIGNATURE_CORE_UNEXPECTED_ERROR(2002, "驗章核心功能發生不預期錯誤"),
    CERTIFICATE_VALIDATION_RESULT_NULL(2003, "驗證憑證結果回傳 NULL 值"),
    JSON_PARSE_ERROR(2004, "剖析 JSON 格式錯誤"),
    INVALID_APP_ID_OR_TYPE_FORMAT(2005, "appID、 type有值 (非空白或 NULL值 )，且非 integer格式"),
    CERTIFICATE_DER_FORMAT_ERROR(2006, "只單驗 Certificate的時候傳入的 byte array 非 certificate der code格式"),
    CERTIFICATE_EXPIRED_AT_SIGNING_TIME(2007, "簽章時 (signing time) 的憑證已經過期"),
    TRANSACTION_LOG_DATABASE_ERROR(2008, "寫 Transaction Log 進入 database時，發生錯誤 (請查詢資料庫是否異常)"),
    CLIENT_IP_NOT_AUTHORIZED(2009, "Client端不具合法 IP (可登入 managers 設定 , 若設定 * 表示 any)"),
    CLIENT_IP_EXTRACTION_FAILED(2010, "從 http request 中，抓取 client 端 IP 失敗"),
    UNDEFINED_SIGNATURE_VERIFICATION_EXCEPTION(2099, "其他未定義之驗章異常發生"),

    APPINFO_NOT_FOUND(3011, "找不到 APPINFO 資訊"),
    RULEINFO_NOT_FOUND(3012, "找不到 RULEINFO 資訊"),
    VALIDATORINFO_NOT_FOUND(3013, "找不到 VALIDATORINFO 資訊"),
    CRLDOWNLOADINFO_NOT_FOUND(3014, "找不到 CRLDOWNLOADINFO 資訊"),
    CAINFO_NOT_FOUND(3015, "找不到 CAINFO 資訊"),
    CRLINFO_NOT_FOUND(3016, "找不到 CRLINFO 資訊"),
    SYSTEMINFO_NOT_FOUND(3017, "找不到 SYSTEMINFO 資訊"),
    TRANSACTIONINFO_NOT_FOUND(3018, "找不到 TRANSACTIONINFO 資訊"),
    USERINFO_NOT_FOUND(3019, "找不到 USERINFO 資訊"),

    APPINFO_SERVICE_NOT_FOUND(3021, "找不到 APPINFO 服務"),
    RULEINFO_SERVICE_NOT_FOUND(3022, "找不到 RULEINFO 服務"),
    VALIDATORINFO_SERVICE_NOT_FOUND(3023, "找不到 VALIDATORINFO 服務"),
    CRLDOWNLOADINFO_SERVICE_NOT_FOUND(3024, "找不到 CRLDOWNLOADINFO 服務"),
    CAINFO_SERVICE_NOT_FOUND(3025, "找不到 CAINFO 服務"),
    CRLINFO_SERVICE_NOT_FOUND(3026, "找不到 CRLINFO 服務"),
    SYSTEMINFO_SERVICE_NOT_FOUND(3027, "找不到 SYSTEMINFO 服務"),
    TRANSACTIONINFO_SERVICE_NOT_FOUND(3028, "找不到 TRANSACTIONINFO 服務"),
    USERINFO_SERVICE_NOT_FOUND  (3029, "找不到 USERINFO 服務"),

    CRL_PARSING_OR_VERIFICATION_ERROR  (5011, "CRL 解析或驗證發生錯誤"),
    CRL_DOWNLOAD_IO_ERROR  (5012, "CRL 下載 IO 錯誤 (例如無法連線 )"),
    CA_CERTIFICATE_PARSING_ERROR  (5013, "CA 憑證解析發生錯誤"),
    CRL_FILE_PATH_ERROR  (5014, "CRL 檔案路徑 (URI) 錯誤"),
    CRL_DOWNLOAD_DISABLED  (5015, "已禁止 CRL 下載"),
    CRL_DOWNLOAD_OTHER_ERROR  (5999, "CRL 下載其他錯誤"),

    XML_PARSING_ERROR  (7001, "Xml 格式錯誤 (剖析失敗)"),
    MISSING_XML_SIGNATURE_BLOCK  (7002, "不存在 Xml Signature Block"),
    XML_SIGNATURE_UNMARSHAL_FAILURE  (7003, "unmarshal the XMLSignature 失敗"),
    CORE_VALIDATE_XML_SIGNATURE_EXCEPTION  (7004, "執行 core validate Xml signature 發生 exception"),
    CORE_VALIDATE_XML_SIGNATURE_FAILURE  (7005, "core validate Xml signature 失敗"),
    VALIDATE_XML_SIGNATURE_EXCEPTION  (7006, "執行 validate xml signature 發生 exception"),
    VALIDATE_XML_SIGNATURE_FAILURE  (7007, "validate xml signature 失敗"),
    VALIDATE_XML_REFERENCE_DIGEST_EXCEPTION  (7008, "執行 validate Xml reference digest 發生 exception"),
    VALIDATE_XML_REFERENCE_DIGEST_FAILURE  (7009, "Validate Xml reference digest 失敗"),
    REGISTER_IDS_ERROR  (7010, "註冊 IDs 時，發生錯誤"),
    BASE64_DECODE_ERROR  (7011, "對 input 做 Base64 decode 失敗"),
    UNDEFINED_XML_SIGNATURE_VERIFICATION_ERROR  (7255, "其他未定義之 Xml Signature驗章異常發生"),

    CRL_CONTENT_NOT_FOUND  (8001, "找不到指定的 CRL 內容"),
    CRL_HAS_EXPIRED  (8002, "CRL 已逾期"),
    CRL_DOWNLOAD_STOPPED  (8003, "CRL 已停止下載"),
    CRL_DOWNLOAD_ERROR  (8004, "CRL 下載發生錯誤"),
    CRL_NEVER_DOWNLOADED  (8005, "CRL 從未下載"),
    CRLCENTER_NOT_INITIALIZED  (8006, "CRLCenter 未初始化"),


    UNKNOWN_ERROR(9999, "未知錯誤");

    private final int code;
    private final String message;

    CertErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static String getMessageByCode(int code) {
        for (CertErrorCode errorCode : values()) {
            if (errorCode.getCode() == code) {
                return errorCode.getMessage();
            }
        }
        return UNKNOWN_ERROR.getMessage();
    }
}
