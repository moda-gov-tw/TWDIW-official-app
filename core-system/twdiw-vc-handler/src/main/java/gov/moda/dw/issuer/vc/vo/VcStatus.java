package gov.moda.dw.issuer.vc.vo;

/**
 * enumeration of vc status
 *
 * @version 20240902
 */
public enum VcStatus {

    ACTIVE ("0"),
    SUSPENDED ("1"),
    REVOKED ("2"),
    UNKNOWN ("-1");

    private final String value;

    VcStatus(String value) {
        this.value = value;
    }

    public static VcStatus getByValue(String value) {

        VcStatus[] values = VcStatus.values();
        for (VcStatus vcStatus : values) {
            if (vcStatus.getValue().equalsIgnoreCase(value)) {
                return vcStatus;
            }
        }

        return UNKNOWN;
    }

    public boolean equals(VcStatus vcStatus) {

        if (vcStatus != null) {
            return value.equalsIgnoreCase(vcStatus.getValue());
        }

        return false;
    }

    public String getValue() {
        return value;
    }
}
