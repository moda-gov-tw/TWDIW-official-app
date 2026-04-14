package gov.moda.dw.manager.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum SysSymbol {
    COLON(":", "colon"),

    COMMA(",", "comma"),

    FILE_SEPARATOR("/", "File.separator");

    @Getter
    private String code;

    @Getter
    private String name;

    public static SysSymbol toSysSymbol(String code) {
        for (SysSymbol tmp : SysSymbol.values()) {
            if (tmp.getCode().equals(code)) {
                return tmp;
            }
        }
        return null;
    }
}
