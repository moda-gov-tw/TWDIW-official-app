package gov.moda.dw.manager.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum RuleType {
    ALL("all", "全選", "and"),

    PICK("pick", "任選", "or");

    @Getter
    private String code;

    @Getter
    private String name;

    @Getter
    private String enName;

    public static RuleType toRuleType(String code) {
        for (RuleType tmp : RuleType.values()) {
            if (tmp.getCode().equals(code)) {
                return tmp;
            }
        }
        return null;
    }
}
