package gov.moda.dw.manager.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum OrgType {
    DEFAULT_ORG("default", "預設組織");

    @Getter
    private String code;

    @Getter
    private String name;

    public static OrgType toOrgType(String code) {
        for (OrgType tmp : OrgType.values()) {
            if (tmp.getCode().equals(code)) {
                return tmp;
            }
        }
        return null;
    }
}
