package gov.moda.dw.manager.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum StatusCode {
    FAIL("-1", "FAIL"),

    SUCCESS("0", "SUCCESS"),

    REQUEST_MISSING_REQUIRED_PARAM("1XX01", "請求缺少必要參數"),

    INVALID_ACTIVATED("1XX02", "啟停狀態異常"),

    DATE_RANGE_ERROR("1XX03", "結束日期不可小於起始日期"),

    CURRENT_USER_LOGIN_NOT_FOUND("1XX04", "找不到當前登入的使用者"),
    CURRENT_USER_DO_NOT_MATCH("1xx05", "所請求之資料並非登入者本人之資料"),
    SID_ILLEGAL("1XX06", "驗證失敗，請重新登入"),

    CAPTCHA_NOT_VERIFY("1XX07", "圖形驗證碼未驗證"),
    OTP_TIMEOUT("1XX08", "OTP驗證碼超時"),
    TOKEN_IOEXCEPTION_FAIL("1XX09", "[TokenProvider.getAuthentication] IOException"),
    TOKEN_JSON_FAIL("1XX10", "[TokenProvider.makeJwtUserObject] JsonProcessingException"),
    REQUEST_PARAM_ERROR("1XX11", "傳入參數錯誤"),

    ORDER_TYPE_ORDER_PLATFORM_NEEDED("11101", "訂單所屬平台為必填欄位"),

    ORDER_TYPE_ORDER_TYPE_GUP_NEEDED("11102", "訂單類型群組為必填欄位"),

    ORDER_TYPE_ORDER_TYPE_NEEDED("11103", "訂單類型為必填欄位"),

    ORDER_TYPE_USER_TYPE_NEEDED("11104", "使用者類型為必填欄位"),

    ORDER_TYPE_PAY_PLATFORM_NEEDED("11105", "繳費平台為必填欄位"),

    ORDER_TYPE_PRODUCT_BS_CODE_NEEDED("11106", "訂單業務別代碼為必填欄位"),

    ORDER_TYPE_CHECKOUT_FUNC_NEEDED("11107", "結帳方式為必填欄位"),

    ORDER_TYPE_INV_OR_REC_TYPE_NEEDED("11108", "開立發票或收據類型為必填欄位"),

    ORDER_TYPE_STATUS_NEEDED("11109", "狀態為必填欄位"),

    ORDER_TYPE_CREATE_ERROR("11110", "新增訂單類型發生錯誤"),

    ORDER_TYPE_CREATE_ORDER_TYPE_ALREADY_USE("11111", "此訂單類型已存在，無法新增"),

    ORDER_TYPE_ORDER_TYPE_NOT_FOUND("11112", "查無此訂單類型"),
    ORDER_TYPE_UPDATE_ERROR("11113", "編輯訂單類型發生錯誤"),
    ORDER_TYPE_DELETE_ERROR_BY_ID_NOT_FOUND("11114", "查無此訂單類型，刪除失敗"),
    ORDER_TYPE_DELETE_ERROR("11115", "刪除訂單類型發生錯誤"),
    ORDER_TYPE_QUERY_DETAIL_ERROR("11116", "查詢訂單類型詳細資料發生錯誤"),
    ORDER_TYPE_ORDER_PLATFORM_NOT_EXIST("11117", "傳入的訂單所屬平台不存在"),
    ORDER_TYPE_ORDER_TYPE_GUP_NOT_EXIST("11118", "傳入的訂單類型群組不存在"),
    ORDER_TYPE_USER_TYPE_NOT_EXIST("11119", "傳入的使用者類型不存在"),
    ORDER_TYPE_PAY_PLATFORM_NOT_EXIST("11120", "傳入的繳費平台不存在"),
    ORDER_TYPE_CHECKOUT_FUNC_NOT_EXIST("11121", "傳入的結帳方式不存在"),
    ORDER_TYPE_INV_OR_REC_TYPE_NOT_EXIST("11122", "傳入的開立發票或收據類型不存在"),
    ORDER_TYPE_STATUS_NOT_EXIST("11123", "傳入的狀態類型不存在"),

    // Ams311w。
    ACCOUNT_ID_EXISTS("31101", "帳號ID不可有值。"),
    ACCOUNT_INVALID_LOGIN("31102", "帳號格式(信箱)有誤。"),
    ACCOUNT_INVALID_NAME("31103", "使用者名稱不符合格式。"),
    ACCOUNT_LOGIN_EXISTS("31104", "此帳號已被使用。"),
    ACCOUNT_EMAIL_EXISTS("31105", "此信箱已被使用。"),
    ACCOUNT_CREATE_EXCEPTION("31106", "建立帳號異常，請聯繫系統管理員。"),
    ACCOUNT_SEARCH_DB_EXCEPTION("31107", "查詢資料庫異常，請聯繫系統管理員。"),
    ACCOUNT_UPDATE_EXCEPTION("31108", "更新帳號異常，請聯繫系統管理員。"),
    ACCOUNT_NOT_EXISTS("31109", "此帳號不存在。"),
    SEARCH_BEGIN_TIME_ERROR("31112", "起始時間不正確。"),
    SEARCH_END_TIME_ERROR("31113", "結束時間不正確。"),
    ACCOUNT_GET_CUSTOM_ARGUMENT_EXCEPTION("31114", "自定義參數獲取異常。"),
    ACCOUNT_GET_ALL_ROLE_EXCEPTION("31115", "查詢所有角色清單異常，請聯繫系統管理員。"),
    ACCOUNT_GET_USER_ROLE_EXCEPTION("31116", "查詢帳號角色清單異常，請聯繫系統管理員。"),
    ACCOUNT_ID_NOT_EXISTS("31117", "帳號ID不可為空。"),
    ACCOUNT_LOGIN_NOT_EXISTS("31118", "帳號不可為空。"),
    ACCOUNT_UPDATE_USER_ROLES_EXCEPTION("31119", "更新帳號角色異常，請聯繫系統管理員。"),
    ACCOUNT_INVALID_PHONE("31120", "聯絡電話為必填欄位，請檢查格式是否正確。"),
    ACCOUNT_VALIDATE_KEY_EXCEPTION("31121", "驗證啟用及重置金鑰發生異常。"),
    ACCOUNT_VALIDATE_KEY_FAIL("31122", "無效連結，請確認驗證連結是否正確。"),
    ACCOUNT_VALIDATE_KEY_EXPIRED("31123", "無效連結，請注意，密碼重設要求的效期只有 24 小時。"),
    ACCOUNT_ACTIVATE_EXCEPTION("31124", "帳號啟用異常，請聯絡系統管理員。"),
    ACCOUNT_NEW_BWD_ERROR("31125", "變更密碼失敗，請確認輸入的新密碼資訊，請包含一個大寫和小寫字母以及一個特殊符號。"),
    ACCOUNT_LOGIN_USER_ERROR("31126", "登入帳號異常。"),
    ACCOUNT_DEFAULT_ROLE_NOT_FOUND("31127", "建立帳號無法存入預設角色，請聯繫系統管理員。"),
    ACCOUNT_QUERY_DETAIL_ERROR("31128", "查詢帳號詳細資料發生錯誤。"),
    ACCOUNT_NOT_FOUND("31129", "查無此帳號資料。"),
    ACCOUNT_INVALID_ORG_ID("31130", "組織欄位不符合格式。"),
    ACCOUNT_SEND_MAIL_ERROR("31131", "寄送Email發生錯誤，請聯繫系統管理員"),
    ACCOUNT_REACTIVATION_USER_ID_IS_NULL("31132", "重寄帳號啟用信異常，帳號ID不得為空"),
    ACCOUNT_REACTIVATION_LOGIN_NOT_FUND("31133", "重寄帳號啟用信異常，查詢不到啟用帳號"),
    ACCOUNT_REACTIVATION_INVALID_CONDITION("31134", "重寄帳號啟用信異常，此帳號不符合重新寄送條件"),
    ACCOUNT_DELETE_EXCEPTION("31135", "刪除帳號異常，請聯繫系統管理員。"),
    ACCOUNT_DELETE_USER_NOT_EXISTS("31136", "刪除帳號異常，帳號不存在。"),
    ACCOUNT_DELETE_LOGGED_IN_ACCOUNT("31137", "刪除帳號異常，不可刪除正在登入的帳號。"),
    ACCOUNT_DELETE_ADMIN_IN_ACCOUNT("31138", "刪除帳號異常，不可刪除admin。"),
    ACCOUNT_ADMIN_ROLE_QUERY_ERROR("31139", "查詢登入使用者帳號是否具備admin角色，查詢發生異常。"),
    ACCOUNT_INVALID_USER_TYPE_ID("31140", "帳號類型不符合規範。"),
    ACCOUNT_CREATE_NOT_ADMIN("31141", "建立帳號時使用者非管理員。"),
    ACCOUNT_CANT_CONFER_ROLE("31142", "請求的角色清單含有不可賦予的角色，角色賦予失敗"),

    // Ams341w
    API_TRACK_SEARCH_DB_EXCEPTION("35101", "查詢資料庫異常，請聯繫系統管理員。"),

    // Ams351w
    TOKEN_SEARCH_DB_EXCEPTION("35101", "查詢資料庫異常，請聯繫系統管理員。"),
    TOKEN_CREATE_EXCEPTION("35102", "建立AccessToken異常，請聯繫系統管理員。"),
    TOKEN_UPDATE_EXCEPTION("35103", "更新AccessToken異常，請聯繫系統管理員。"),
    TOKEN_INVALID_NAME("35104", "AccessToken名稱不符合格式。"),
    TOKEN_INVALID_ORG_ID("35105", "組織ID不符合格式。"),
    TOKEN_INVALID_ACTIVATED("35106", "啟停狀態異常。"),
    TOKEN_ADMIN_USER_ERROR("35107", "ADMIN帳號異常。"),
    TOKEN_INVALID_TOKEN("35108", "ACCESS_TOKEN不正確。"),
    TOKEN_ID_NOT_EXISTS("35109", "AccessToken ID不可為空。"),
    TOKEN_NOT_EXISTS("35110", "查無此AccessToken。"),
    TOKEN_DELETE_EXCEPTION("35111", "刪除AccessToken異常，請聯繫系統管理員。"),
    TOKEN_INVALID_ID("35112", "AccessTokenID，不符合格式。"),
    TOKEN_NOT_ADD_RES_BY_DISABLED("35113", "此AccessToken目前停用中，無法變更權限。"),
    TOKEN_DISABLED("35114", "AccessToken目前未啟用，請聯繫系統管理員。"),
    TOKEN_AC_TYPE_EXCEPTION("35115", "AccessTokenAcType異常。"),
    TOKEN_NOT_EXISTS_FOR_FILTER("35116", "AccessToken權限不足，請聯繫系統管理員。"),

    // Ams331w
    RES_NOT_EXISTS("33101", "此功能不存在。"),

    RES_UPDATE_STATUS_EXCEPTION("33102", "更新功能啟停狀態異常，請聯絡系統管理員。"),

    RES_SEARCH_EXCEPTION("33103", "查詢功能異常，請聯絡系統管理員。"),

    // Ams321w
    ROLE_SEARCH_EXCEPTION("32101", "查詢角色異常，請聯絡系統管理員。"),

    ROLE_NOT_EXISTS("32102", "此角色不存在。"),

    ROLE_ROLEID_EXIST("32103", "角色代碼已存在。"),

    ROLE_DELETE_ERROR("32104", "刪除角色異常，請聯絡系統管理員。"),

    ROLE_SEARCH_RES_ERROR("32105", "查詢功能資訊異常，請聯絡系統管理員。"),

    ROLE_IS_DISABLE("32106", "角色已停用。"),

    ROLE_CONFER_RES_ERROR("32107", "授權功能異常，請聯絡系統管理員。"),

    ROLE_GET_TREE_ERROR("32108", "取得角色授權功能內容失敗。"),

    ROLE_DEFAULT_ROLE_AND_ADMIN_CANNOT_BE_DISABLED("32109", "預設角色與admin不可停用。"),

    ROLE_DEFAULT_ROLE_AND_ADMIN_CANNOT_BE_DELETE("32110", "預設角色與admin不可刪除。"),

    ORG_DEFAULT_ORG_CANNOT_BE_EDIT("32111", "預設組織不可修改。"),

    ORG_DEFAULT_ORG_CANNOT_BE_DELETE("32112", "預設組織不可刪除。"),

    ORG_NOT_EXISTS("32113", "此組織不存在。"),

    ORG_ORGID_EXIST("32114", "組織代號已存在。"),

    ORG_ORG_HAS_VP_SCHEMA_CANNOT_BE_DELETE("32115", "該組織有建立VP模板，無法刪除該組織。"),

    ORG_ORGENNAME_EXIST("32116", "組織英文名稱已存在。"),

    ORG_ORGID_CANNOT_BE_EDIT("32117", "組織編號不可修改。"),

    ORG_ORGENNAME_CANNOT_BE_EDIT("32118", "組織英文名稱不可修改。"),

    ORG_PARAM_INVALID("32119", "組織相關欄位不符合規則。"),
    
    ORG_DID_ORG_MISMATCH("32120", "目前登入的組織非該 DID 的註冊組織，無法操作此功能。"),

    ORG_CANNOT_BE_DELETE("32120", "該組織不可刪除。"),

    // Ams312w
    ACCOUNTCHANGE_SEARCH_EXCEPTION("31201", "查詢帳號異動清單異常，請聯絡系統管理員。"),

    // Ams322w
    ROLECHANGE_SEARCH_EXCEPTION("32201", "查詢角色異動清單異常，請聯絡系統管理員。"),

    // Ams332w
    RESCHANGE_SEARCH_EXCEPTION("33201", "查詢功能異動清單異常，請聯絡系統管理員。"),

    // Mail
    MAIL_TEMPLATE_NOT_EXISTS("M01", "mailTemplate不存在，請聯繫系統管理員。"),

    MAIL_TEMPLATE_KEYWORD_COUNT_EXCEPTION("M02", "Message關鍵字數量比對異常，請聯繫系統管理員。"),

    MAIL_UNKNOWN_ERROR("M03", "發送郵件時發生未知錯誤，請聯繫系統管理員。"),

    MAIL_SERVER_UNAVAILABLE("M04", "郵件伺服器當前不可用，請稍後再試。"),

    MAIL_CONTENT_ERROR("M05", "郵件內文取得異常，請聯繫系統管理員。"),

    //Virtual Account
    CREATE_VIRTUAL_ACCOUNT_ERROR("701001", "建立虛擬帳號失敗，請聯繫系統管理員"),
    CANCEL_VIRTUAL_ACCOUNT_ERROR("701002", "註銷虛擬帳號失敗，請聯繫系統管理員"),

    // ApiTrackManagement
    API_TRACK_SEARCH_SERVICE_ID_NOT_NULL("34101", "查詢時serviceId不得為空。"),

    // Ams803w
    PRODUCT_NOT_EXISTS("80301", "此商品不存在。"),
    PRODUCT_CODE_EXIST("80302", "商品代碼已存在。"),
    PRODUCT_FUTURE_COLUMN_NEED_WRITE_ALL("80303", "預計調整商品單價(含審查處理費)、預計調整審查處理費、預計啟用時間缺一不可。"),
    PRODUCT_SEARCH_EXCEPTION("80304", "查詢商品異常，請聯絡系統管理員。"),
    PRODUCT_FUTURE_START_TIME_MUST_AFTER_TODAY("80305", "預計啟用時間只可選擇當天(含)之後日期。"),

    // Ams801w
    MEMBER_PLATFORM_CREATE_ERROR("80101", "新增會員平台資料發生錯誤。"),
    MEMBER_PLATFORM_CODE_NEEDED("80102", "會員平台代碼為必填欄位。"),
    MEMBER_PLATFORM_CREATE_CODE_ALREADY_USE("80103", "此會員平台代碼已存在，無法新增。"),
    MEMBER_PLATFORM_NAME_NEEDED("80104", "會員平台名稱為必填欄位。"),
    MEMBER_PLATFORM_CREATE_NAME_ALREADY_USE("80105", "此會員平台名稱已存在，無法新增。"),
    MEMBER_PLATFORM_STATUS_NEEDED("80106", "會員平台狀態為必填欄位。"),
    MEMBER_PLATFORM_ADMIN_USER_ERROR("80107", "登入帳號查詢異常。"),
    MEMBER_PLATFORM_ID_NEEDED("80108", "會員平台辨識碼為必填欄位。"),
    MEMBER_PLATFORM_NOT_FOUND("80109", "查無此會員平台資料。"),
    MEMBER_PLATFORM_UPDATE_ERROR("80110", "編輯會員平台資料發生錯誤。"),
    MEMBER_PLATFORM_DELETE_ERROR_BY_ID_NOT_FOUND("80111", "查無此會員平台資料，刪除失敗。"),
    MEMBER_PLATFORM_DELETE_ERROR("80112", "刪除會員平台資料發生錯誤。"),
    MEMBER_PLATFORM_QUERY_DETAIL_ERROR("80113", "查詢會員平台詳細資料發生錯誤。"),
    MEMBER_PLATFORM_UPDATE_CODE_NOT_EXISTS("80114", "此會員平台資料不存在，無法更新。"),
    MEMBER_PLATFORM_UPDATE_STATUS_ERROR("80115", "編輯會員平台資料啟停狀態發生錯誤。"),
    MEMBER_PLATFORM_INVALID_EMAIL("80116", "信箱格式不正確。"),
    MEMBER_PLATFORM_INVALID_TEL("80117", "連絡電話格式不正確。"),

    // Ams202w
    STAMP_RECORD_ADMIN_USER_ERROR("20201", "登入帳號查詢異常。"),
    STAMP_RECORD_SAVE_IMAGE_ERROR("20202", "上傳檔案失敗。"),
    STAMP_RECORD_CREATE_ERROR("20203", "新增收據印章發生錯誤。"),
    STAMP_RECORD_NAME_NEEDED("20204", "印章名稱為必填欄位。"),
    STAMP_RECORD_CREATE_NAME_ALREADY_USE("20205", "此印章名稱已存在，無法新增。"),
    STAMP_RECORD_STATUS_NEEDED("20206", "印章啟停狀態為必填欄位。"),
    STAMP_RECORD_CREATE_FILE_SIZE_EXCEEDED("20207", "檔案大小超過上限，無法新增。"),
    STAMP_RECORD_FILE_NEEDED("20208", "必須上傳印章圖像檔。"),
    STAMP_RECORD_FILE_EXTENSION_ERROR("20209", "無法正確取得檔案副檔名，或者副檔名不符合規範。"),
    STAMP_RECORD_CREATE_FILE_DIMENSION_EXCEEDED("20210", "檔案長寬不符合規範，無法新增。"),
    STAMP_RECORD_UPDATE_ID_NOT_EXISTS("20211", "此收據印章資料不存在，無法更新。"),
    STAMP_RECORD_UPDATE_STATUS_ERROR("20212", "編輯收據印章資料啟停狀態發生錯誤。"),
    STAMP_RECORD_DELETE_ERROR("20213", "刪除收據印章資料發生錯誤。"),
    STAMP_RECORD_DELETE_ERROR_BY_ID_NOT_FOUND("20214", "查無此收據印章資料，刪除失敗。"),
    STAMP_RECORD_NOT_FOUND("20215", "查無此收據印章資料。"),
    STAMP_RECORD_QUERY_DETAIL_ERROR("20216", "查詢收據印章詳細資料發生錯誤。"),
    STAMP_RECORD_IMAGE_NOT_FOUND("20215", "查無此收據印章檔案。"),
    STAMP_RECORD_UPDATE_NAME_ALREADY_USE("20216", "此印章名稱已存在，無法編輯。"),
    STAMP_RECORD_ID_NEEDED("20217", "印章編號不得為空。"),
    STAMP_RECORD_UPDATE_ERROR("20218", "編輯收據印章資料發生錯誤。"),
    STAMP_RECORD_FILE_NAME_ALPHANUMERIC_ONLY("20219", "印章檔案名稱只能包含英數字。"),

    // Ams802w
    SYSPARAM_CODE_EXIST("80201", "參數代碼已存在。"),
    SYSPARAM_NOT_EXISTS("80202", "此系統參數不存在。"),
    SYSPARAM_QUERY_DETAIL_ERROR("80203", "查詢系統參數詳細資料發生錯誤"),
    SYSPARAM_UPDATE_ERROR("80204", "編輯系統參數設定發生錯誤"),
    SYSPARAM_UPDATE_STATUS_ERROR("80205", "編輯系統參數設定啟停狀態發生錯誤"),
    SYSPARAM_DELETE_ERROR_BY_ID_NOT_FOUND("80206", "查無此系統參數設定，刪除失敗"),
    SYSPARAM_DELETE_ERROR("80207", "刪除系統參數設定發生錯誤"),

    // Modadw101w
    DID_REGISTER_ERROR("10101", "註冊DID失敗"),
    DID_CREATE_ORG_ERROR("10102", "組織建立失敗"),
    DID_USER_NOT_FOUND("10103", "找不到登入使用者"),
    DID_EXTENDED_USER_NOT_FOUND("10104", "找不到對應的使用者帳號"),
    DID_UPDATE_ORG_ERROR("10104", "更新使用者組織失敗"),
    DID_REGISTER_EXCEPTION("10105", "註冊DID異常，請聯絡系統管理員"),
    DID_FIELD_VALIDATION_ERROR("10106", "欄位驗證失敗"),
    DID_ENV_CHECK_FAIL("10107", "環境檢測失敗"),
    DID_IVPAS_VERIFY_ERROR("10108", "DID檢驗未通過"),
    DID_IVPAS_VERIFY_EXCEPTION("10109", "DID檢驗服務內部錯誤，請聯絡系統管理員"),
    DID_CERT_VERIFY_ERROR("10110", "憑證驗簽失敗"),
    DID_CERT_VERIFY_EXCEPTION("10111", "憑證驗證服務異常，請聯絡系統管理員"),
    DID_VERIFY_EXCEPTION("10112", "驗證服務異常，請聯絡系統管理員"),
    DID_INIT_FIND_ISSUER_ERROR("10113", "查詢DID上鏈資訊失敗"),
    DID_INIT_FIND_DATASOURCE_ERROR("10114", "查詢vc資料來源種類失敗"),
    DID_INIT_FIND_ACCESSTOKEN_ERROR("10115", "查詢access token失敗"),
    DID_INIT_FIND_USERORG_ERROR("10116", "查詢使用者對應組織失敗"),
    DID_INIT_EXCEPTION("10117", "DID頁面取得初始化資料異常，請聯絡系統管理員"),
    DID_UPDATE_ACCESSTOKEN_ERROR("10118", "更新access token失敗"),

    // Ams303w
    LOGIN_IS_PASS_WORD_VALID_LOGIN_IS_NULL("30301", "查詢登入密碼是否有效發生錯誤，參數帳號不得為空"),
    LOGIN_IS_PASS_WORD_VALID_BWD_RULE_VALUE_ERROR("30302", "查詢登入密碼是否有效發生錯誤，密碼規則無法取得正確參數值"),
    LOGIN_IS_PASS_WORD_VALID_PASSWORD_EXPIRED("30303", "使用者密碼已過期"),
    LOGIN_IS_PASS_WORD_VALID_EXCEPTION("30304", "查詢登入密碼是否有效發生錯誤"),
    LOGIN_IS_PASS_WORD_VALID_NO_HISTORY_RECORD("30305", "找不到密碼更改歷史紀錄"),
    LOGIN_IS_PASS_WORD_VALID_RULE_NOT_FOUND("30306", "查詢登入密碼是否有效，找不到密碼規則(最長效期)"),

    // DWVP-01-101 - 驗證端產生授權請求QR Code
    DWVP_REF_NOT_VALID("10101", "欄位檢核有誤：ref 格式錯誤"),
    DWVP_DOMAIN_URI_NOT_FOUND("10103", "查無 domain-uri"),
    DWVP_GENERATOR_QRCODE_ERROR("10104", "產生 Qr Code 失敗"),
    DWVP_REF_NOT_FOUND("10105", "欄位檢核有誤：ref 為必填"),
    DWVP_TRANSACTION_ID_NOT_FOUND("10106", "欄位檢核有誤：transactionId 為必填"),

    // DWVP-01-201 - 驗證端查詢VP展示驗證結果
    DWVP_QUERY_VP_RESULT_ERROR("20101", "查詢驗證結果失敗"),

    // Dwvp101i - VP驗證端模板相關資料
    DWVP_VPUID_NOT_EXISTS("10101", "欄位檢核有誤：vpUid 為必填"),
    DWVP_VP_ITEM_NOT_FOUND("10102", "查無模板資料"),
    DWVP_VPUID_NOT_VALID("10103", "欄位檢核有誤：vpUid 格式錯誤"),
    DWVP_INTERNAL_SERVER_ERROR("59999", "內部系統發生錯誤，請洽系統管理人員"),

    // Dwvp102i - 提供 VP 驗證端 offline 相關資料
    DWVP_VP_ITEM_OFFLINE_NOT_FOUND("10201", "查無 offline 模板資料"),

    // Dwvp301i - VP 驗證結果通道處理 callback 驗證端端服務
    DWVP_TRANSACTIONID_NOT_FOUND("30101", "欄位檢核有誤：transactionId 為必填"),
    DWVP_CALLBACK_URL_NOT_EXISTS("30102", "查無 Callback URL"),
    DWVP_CALLBACK_REQUEST_FAILED("30103", "Callback URL 請求失敗"),

    // Dwvp401i - 取得於 offline 模式的自主揭露 qrcode
    DWVP_FIELD_INFO_PARSE_ERROR("40101", "資料欄位格式錯誤"),

    // Dwvp402i - 取得加密資料 QR Code
    DWVP_JWT_PAYLOAD_PARSE_ERROR("40201", "JWT Payload 格式錯誤"),
    DWVP_JWT_NOT_FOUND("40202", "欄位檢核有誤：jwt 為必填"),
    DWVP_VP_VERIFY_RESULT_NOT_FOUND("40203", "此組交易序號查無資料"),
    DWVP_JWT_TIMEOUT("40204", "JWT 已超過可驗證時間"),
    DWVP_ORG_KEY_SETTING_MULTIPLE_ACTIVED_ERROR("40205", "金鑰資料錯誤：多組啟用"),
    DWVP_ORG_KEY_SETTING_NOT_FOUND("40206", "查無金鑰資料"),
    DWVP_JWT_NOT_VALID("40207", "JWT 驗簽失敗"),
    DWVP_GENERATOR_ENCRYPT_QRCODE_CONTENT("40208", "產生加密 Qr Code 內容失敗"),
    DWVP_VERIFY_RESULT_PARSE_ERROR("40209", "取得驗證結果失敗"),
    DWVP_CUSTOM_FIELD_NAME_NOT_VALID("40210", "欄位檢核有誤：自定義欄位名稱為必填"),
    DWVP_GENERATOR_QRCODE_CONTENT("40211", "產生 Qr Code 內容失敗"),

     // Dwvp403i - 設定金鑰訊息
    DWVP_KEY_ID_NOT_EXISTS("40301", "欄位檢核有誤：組織金鑰代碼為必填"),
    DWVP_PUBLIC_KEY_NOT_EXISTS("40302", "欄位檢核有誤：公鑰為必填"),
    DWVP_PUBLIC_KEY_NOT_VALID("40303", "欄位檢核有誤：公鑰格式不正確"), 
    DWVP_PRIVATE_KEY_NOT_VALID("40304", "欄位檢核有誤：私鑰格式不正確"), 
    DWVP_TOTP_KEY_NOT_EXISTS("40305", "欄位檢核有誤：TOTP 金鑰為必填"),
    DWVP_TOTP_KEY_NOT_VALID("40306", "欄位檢核有誤：TOTP 金鑰格式不正確"),
    DWVP_HMAC_KEY_NOT_EXISTS("40307", "欄位檢核有誤：HMAC 金鑰為必填"),
    DWVP_HMAC_KEY_NOT_VALID("40308", "欄位檢核有誤：HMAC 金鑰格式不正確"),
    DWVP_IS_ACTIVE_NOT_EXISTS("40309", "欄位檢核有誤：啟用狀態為必填"),
    DWVP_IS_PRESENT("40310", "欄位檢核有誤：組織金鑰代碼已存在"),
    DWVP_DID_NOT_REGISTERED("40311", "尚未註冊 DID"),

    // Dwvp404i - 解密資料
    DWVP_PRIVATE_KEY_BLANK("40401", "查無私鑰"),
    DWVP_TAG_IS_REQUIRED("40402", "欄位檢核有誤：t 為必填"),
    DWVP_KEY_ID_IS_REQUIRED("40403", "欄位檢核有誤：k 為必填"),
    DWVP_DATA_IS_REQUIRED("40404", "欄位檢核有誤：d 為必填"),
    DWVP_DECRYPTION_FAILED("40405", "解密失敗：缺少必要欄位（totp）"),
    DWVP_TOTP_VERIFICATION_FAILED("40406", "TOTP 驗證失敗：TOTP 碼無效或已過期"),
    DWVP_HMAC_VERIFICATION_FAILED("40407", "HMAC 驗證失敗：資料完整性檢查失敗"),
    DWVP_HMAC_IS_REQUIRED("40408", "欄位檢核有誤：h 為必填");


    @Getter
    private final String code;

    @Getter
    private final String msg;

    public static StatusCode toStatusCode(String code) {
        for (StatusCode tmp : StatusCode.values()) {
            if (tmp.getCode().equals(code)) {
                return tmp;
            }
        }
        return null;
    }
}
