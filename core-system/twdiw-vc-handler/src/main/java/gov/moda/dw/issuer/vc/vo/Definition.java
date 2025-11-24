package gov.moda.dw.issuer.vc.vo;

import com.danubetech.verifiablecredentials.VerifiableCredential;

import java.util.Arrays;

/**
 * constant definition
 *
 * @version 20250526
 */
public final class Definition {

    public static final String CURRENT_VERSION = "ver.20250603.1";

    // [postgresql specific]
    // - use postgresql sequence, default sequence name is `seq_1` (need to set to database at initial stage)
    public static final String DEFAULT_SEQUENCE_NAME = "seq_1";

    // default JSON schema type
    // instead of `JsonSchemaValidator2018` defined in VCDM 1.1, use `JsonSchema` defined in VCDM 2.0
    public static final String DEFAULT_JSON_SCHEMA_TYPE = "JsonSchema";

    // VC
    public static final String VC_FIELD_NAME_CREDENTIAL_SCHEMA = "credentialSchema";

    // JWT
    public static final String JWT_HEADER_TYPE_VC_PLUS_SD_JWT = "vc+sd-jwt";
    public static final String JWT_PAYLOAD_FIELD_NONCE = "nonce";
    public static final String JWT_PAYLOAD_FIELD_CNF = "cnf";

    // "VerifiableCredential"
    public static final String DEFAULT_VERIFIABLE_CREDENTIAL_TYPE = VerifiableCredential.DEFAULT_JSONLD_TYPES[0];

    // default status list credential type
    public static final String DEFAULT_STATUS_LIST_CREDENTIAL_TYPE = "StatusList2021Credential";
    
    // status list operation type
    public static final String STATUS_LIST_GEN_VC = "genVC";
    public static final String STATUS_LIST_REVOKE_VC = "revokeVC";
    public static final String STATUS_LIST_RENEW = "renew";
    public static final String STATUS_LIST_SUSPEND_VC = "suspendVC";
    public static final String STATUS_LIST_RECOVER_VC = "recoverVC";

    // status list type
    public enum StatusListType {

        revocation ("r"),
        suspension ("s");

        private final String tag;

        StatusListType(String tag) {
            this.tag = tag;
        }

        public String getTag() {
            return tag;
        }

        public static StatusListType getInstance(String tag) {
            return Arrays.stream(values())
                .filter(e -> e.getTag().equalsIgnoreCase(tag))
                .findFirst()
                .orElse(null);
        }
        
        public static StatusListType getInstanceFromName(String type) {
            return Arrays.stream(values())
                .filter(e -> e.name().equalsIgnoreCase(type))
                .findFirst()
                .orElse(null);
        }
    }

    // push service
    public static final String PUSH_RETURN_CODE_SUCCESS = "0000";
    
    // frontend service, 1 = issuer
    public static final int FRONTEND_ORG_TYPE = 1;
}
