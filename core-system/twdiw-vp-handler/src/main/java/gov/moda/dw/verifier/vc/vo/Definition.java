package gov.moda.dw.verifier.vc.vo;

import com.danubetech.verifiablecredentials.VerifiableCredential;

public class Definition {

    public static final String CURRENT_VERSION = "ver.20250916.1";

    // JWT
    public static final String JWT_HEADER_TYPE_VC_PLUS_SD_JWT = "vc+sd-jwt";
    public static final String JWT_PAYLOAD_FIELD_VP = "vp";
    public static final String JWT_PAYLOAD_FIELD_VC = "vc";
    public static final String JWT_PAYLOAD_FIELD_NONCE = "nonce";
    public static final String JWT_PAYLOAD_FIELD_CNF = "cnf";
    public static final String JWT_PAYLOAD_FIELD_CNF_JWK = "jwk";

    // "verifiableCredential"
    public static final String VP_FIELD_VC = VerifiableCredential.DEFAULT_JSONLD_PREDICATE;

    // "VerifiableCredential"
    public static final String DEFAULT_VERIFIABLE_CREDENTIAL_TYPE = VerifiableCredential.DEFAULT_JSONLD_TYPES[0];

    // default status list credential type
    public static final String DEFAULT_STATUS_LIST_CREDENTIAL_TYPE = "StatusList2021Credential";

    // vc
    public static final String VC_FIELD_STATUS_LIST_CREDENTIAL = "statusListCredential";
    public static final String VC_FIELD_STATUS_LIST_INDEX = "statusListIndex";
    public static final String VC_FIELD_CREDENTIAL_SCHEMA = "credentialSchema";
    public static final String VC_FIELD_STATUS_PURPOSE = "statusPurpose";

    // status list
    public static final String STATUS_LIST_FIELD_ENCODED_LIST = "encodedList";
    public static final String STATUS_LIST_FIELD_STATUS_PURPOSE = "statusPurpose";

    // issuer DID status
    // 0: on chain not yet
    // 1: effective
    // 2: revoked
    // 3: fail to qualify
    public static final int ISSUER_DID_STATUS_EFFECTIVE = 1;

}
