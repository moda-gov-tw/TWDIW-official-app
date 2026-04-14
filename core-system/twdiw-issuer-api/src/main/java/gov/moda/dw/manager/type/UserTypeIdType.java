package gov.moda.dw.manager.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum UserTypeIdType {
    DEFAULT("default", "一般使用者"),
    AP("Ap", "AP");

    @Getter
    private String code;

    @Getter
    private String name;

    public static UserTypeIdType toUserTypeIdType(String code) {
        for (UserTypeIdType tmp : UserTypeIdType.values()) {
            if (tmp.getCode().equals(code)) {
                return tmp;
            }
        }
        return null;
    }
}
