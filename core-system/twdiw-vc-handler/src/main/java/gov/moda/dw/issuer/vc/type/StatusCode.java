package gov.moda.dw.issuer.vc.type;

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
    ACCOUNT_INVALID_ACTIVATED("31111", "啟停狀態異常。"),
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
    ACCOUNT_NEW_BWD_ERROR("31125", "變更密碼失敗，請確認輸入的新密碼資訊，請包含一個大寫及小寫字母。"),
    ACCOUNT_ADMIN_USER_ERROR("31126", "ADMIN帳號異常。"),
    ACCOUNT_DEFAULT_ROLE_NOT_FOUND("31127", "建立帳號無法存入預設角色，請聯繫系統管理員。"),
    ACCOUNT_QUERY_DETAIL_ERROR("31128", "查詢帳號詳細資料發生錯誤。"),
    ACCOUNT_NOT_FOUND("31129", "查無此會員平台資料。"),

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
    ACCOUNT_SEND_MAIL_ERROR("35117", "寄送Email發生錯誤，請聯繫系統管理員。"),

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

    ROLE_DEFAULT_ROLE_CANNOT_BE_DISABLED("32109", "預設角色不可停用。"),

    ROLE_DEFAULT_ROLE_CANNOT_BE_DELETE("32110", "預設角色不可刪除。"),

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
    SYSPARAM_DELETE_ERROR("80207", "刪除系統參數設定發生錯誤");

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
