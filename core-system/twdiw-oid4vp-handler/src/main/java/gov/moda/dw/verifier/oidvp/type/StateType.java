package gov.moda.dw.verifier.oidvp.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum StateType {
    ENABLED("1", "啟用", "enabled"),

    DISABLED("0", "停用", "disabled");

    @Getter
    private String code;

    @Getter
    private String name;

    @Getter
    private String enName;

    public static StateType toStateType(String code) {
        for (StateType tmp : StateType.values()) {
            if (tmp.getCode().equals(code)) {
                return tmp;
            }
        }
        return null;
    }
}
