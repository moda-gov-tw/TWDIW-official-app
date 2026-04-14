package gov.moda.dw.manager.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ResType {
    //Web => 網頁端使用的，web+api
    WEB("web", "網頁功能"),

    //Api => 給accessToken使用的
    API("api", "API"),

    INTERNAL_API("internal_api", "內部業務API"),

    //節點 => 選單結構
    Node("node", "節點");

    @Getter
    private String code;

    @Getter
    private String name;

    public static ResType toResType(String code) {
        for (ResType tmp : ResType.values()) {
            if (tmp.getCode().equals(code)) {
                return tmp;
            }
        }
        return null;
    }
}
