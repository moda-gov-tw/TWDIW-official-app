package gov.moda.dw.manager.type;

import org.apache.commons.codec.binary.StringUtils;

public enum VcItemDataValidType {

    ACTIVE(0, "正常", "recovery"),
    SUSPENDED (1, "停用", "suspension"),
    REVOKED(2, "撤銷", "revocation"),
    INACTIVE(301, "INACTIVE", "inactive"),
    UNKNOWN (-1, "UNKNOWN", "unknown");

    private final Integer code;

    private final String validName;

    private final String action;

    VcItemDataValidType(Integer code, String validName, String action) {
        this.code = code;
        this.validName = validName;
        this.action = action;
    }

    public Integer getCode() {
        return code;
    }

    public String getValidName() {
        return validName;
    }

    public String getAction() {
        return action;
    }

    public static VcItemDataValidType getByValue(Integer value) {
        VcItemDataValidType[] values = VcItemDataValidType.values();

        for (VcItemDataValidType validType : values) {
            if (validType.getCode().equals(value)) {
                return validType;
            }
        }

        return UNKNOWN;
    }

    public static VcItemDataValidType getByValue(String value) {
        VcItemDataValidType[] values = VcItemDataValidType.values();

        for (VcItemDataValidType validType : values) {
            if (StringUtils.equals(String.valueOf(validType.getCode()), value)) {
                return validType;
            }
        }

        return UNKNOWN;
    }

    public static VcItemDataValidType getByName(String name) {
        VcItemDataValidType[] values = VcItemDataValidType.values();

        for (VcItemDataValidType validType : values) {
            if (StringUtils.equals(validType.name(), name)) {
                return validType;
            }
        }

        return UNKNOWN;
    }

    /**
     * 只允許 0, 1, 2 其餘回傳 UNKNOWN
     * 
     * @param code
     * @return
     */
    public static VcItemDataValidType getByCodeIgnoreInactiveAndUnknown(String code) {
        return switch (code) {
            case "0" -> ACTIVE;
            case "1" -> SUSPENDED;
            case "2" -> REVOKED;
            default -> UNKNOWN;
        };
    }

    /**
     * 只允許 recovery, suspension, revocation，其餘回傳 UNKNOWN
     * 
     * @param action
     * @return
     */
    public static VcItemDataValidType getByActionIgnoreInactiveAndUnknown(String action) {
        for (VcItemDataValidType validType : VcItemDataValidType.values()) {
            if ((validType == ACTIVE || validType == SUSPENDED || validType == REVOKED)
                    && validType.getAction().equalsIgnoreCase(action)) {
                return validType;
            }
        }

        return UNKNOWN;
    }

    /**
     * 只允許 ACTIVE, SUSPENDED, REVOKED，其餘回傳 UNKNOWN
     * 
     * @param name
     * @return
     */
    public static VcItemDataValidType getByNameIgnoreInactiveAndUnknown(String name) {
        for (VcItemDataValidType validType : VcItemDataValidType.values()) {
            if ((validType == ACTIVE || validType == SUSPENDED || validType == REVOKED)
                    && validType.name().equalsIgnoreCase(name)) {
                return validType;
            }
        }

        return UNKNOWN;
    }

}
