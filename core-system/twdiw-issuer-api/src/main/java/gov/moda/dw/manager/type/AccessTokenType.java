package gov.moda.dw.manager.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum AccessTokenType {
    UNLIMITED("unlimited", "無限期")
    //  ONE_TIME("one_time", "一次性"),

    //  LIMITED("limited", "有限期"),
    ;

    @Getter
    private String code;

    @Getter
    private String name;

    public static AccessTokenType toAccessTokenType(String code) {
        for (AccessTokenType tmp : AccessTokenType.values()) {
            if (tmp.getCode().equals(code)) {
                return tmp;
            }
        }
        return null;
    }
}
