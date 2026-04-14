package gov.moda.dw.manager.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum AuthorityAction {
    //角色
    ROLE_DISABLED("ROLE_DISABLED", "角色停用"),
    ROLE_ENABLE("ROLE_ENABLE", "角色啟用"),
    ROLE_DELETE("ROLE_DELETE", "角色刪除"),
    ROLE_CONFER_RES("ROLE_CONFER_RES", "角色授予功能"),
    ROLE_SEARCH_RES("ROLE_SEARCH_REL", "查詢角色已授予的功能"),

    //組織
    ORG_DISABLED("ORG_DISABLED", "組織停用"),
    ORG_ENABLE("ORG_ENABLE", "組織啟用"),
    ORG_DELETE("ORG_DELETE", "組織刪除"),

    //功能
    RES_DISABLED("RES_DISABLED", "功能停用"),
    RES_ENABLED("RES_ENABLED", "功能啟用"),

    //帳號
    ACCOUNT_SET_DEFAULT_ROLE("ACCOUNT_SET_DEFAULT_ROLE", "設定預設角色"),
    ACCOUNT_CONFER_ROLE("ACCOUNT_CONFER_ROLE", "帳號授予角色"),
    ACCOUNT_SEARCH_ROLE("ACCOUNT_SEARCH_ROLE", "查詢帳號已授予的角色"),
    ACCOUNT_DELETE_ROLE("ACCOUNT_DELETE_ROLE", "刪除與帳號關聯角色紀錄"),

    //AccessToken
    ACCESS_TOKEN_CREATE("ACCESS_TOKEN_CREATE", "AccessToken建立"),
    ACCESS_TOKEN_UPDATE("ACCESS_TOKEN_UPDATE", "AccessToken更新"),
    ACCESS_TOKEN_UPDATE_RES("ACCESS_TOKEN_UPDATE_RES", "AccessToken綁定功能變更"),
    ACCESS_TOKEN_CHANGE_ACTIVATED("ACCESS_TOKEN_CHANGE_ACTIVATED", "AccessToken變更啟停"),
    ACCESS_TOKEN_DELETE("ACCESS_TOKEN_DELETE", "刪除AccessToken");

    @Getter
    private String code;

    @Getter
    private String name;

    public static AuthorityAction toStateType(String code) {
        for (AuthorityAction tmp : AuthorityAction.values()) {
            if (tmp.getCode().equals(code)) {
                return tmp;
            }
        }
        return null;
    }
}
