package gov.moda.dw.verifier.oidvp.presentationExchange.inputDescriptor;

import com.fasterxml.jackson.annotation.JsonValue;

public enum FormatRegistry {
    JWT_VC("jwt_vc", FormatRegistryType.VC),

    JWT_VP("jwt_vp", FormatRegistryType.VP),

    JWT_VC_JSON("jwt_vc_json", FormatRegistryType.VC),

    JWT_VP_JSON("jwt_vp_json", FormatRegistryType.VP),

    LDP_VC("ldp_vc", FormatRegistryType.VC),

    LDP_VP("ldp_vp", FormatRegistryType.VP),

    VC_SD_JWT("vc+sd-jwt", FormatRegistryType.SD);


    private final String value;
    private final FormatRegistryType type;

    FormatRegistry(String value, FormatRegistryType type) {
        this.value = value;
        this.type = type;
    }

    public FormatRegistryType getType() {
        return type;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public enum FormatRegistryType {
        VC,
        VP,
        SD
    }
}
