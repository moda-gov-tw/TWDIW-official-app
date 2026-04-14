package gov.moda.dw.manager.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum OrgDataType {
    ISSUER("1", "發行端", "issuer"),
    VERIFIER("2", "驗證端", "verifier");

    @Getter
    private String type;

    @Getter
    private String twName;

    @Getter
    private String enName;

    public static OrgDataType toOrgDataType(String type) {
        for (OrgDataType tmp : OrgDataType.values()) {
            if (tmp.getType().equals(type)) {
                return tmp;
            }
        }
        return null;
    }

}
