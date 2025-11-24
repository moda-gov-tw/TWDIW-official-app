package gov.moda.dw.verifier.vc.task;

import com.authlete.sd.Disclosure;
import com.danubetech.keyformats.crypto.ByteVerifier;
import com.danubetech.keyformats.crypto.impl.P_256_ES256_PublicKeyVerifier;
import com.danubetech.verifiablecredentials.CredentialSubject;
import com.danubetech.verifiablecredentials.VerifiableCredential;
import com.danubetech.verifiablecredentials.credentialstatus.CredentialStatus;
import com.danubetech.verifiablecredentials.jwt.JwtVerifiableCredential;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jwt.JWTClaimsSet;
import foundation.identity.jsonld.JsonLDUtils;
import gov.moda.dw.verifier.vc.task.StatusListCheckTask.StatusListInfo;
import gov.moda.dw.verifier.vc.util.CredentialUtils;
import gov.moda.dw.verifier.vc.util.DidUtils;
import gov.moda.dw.verifier.vc.util.JsonUtils;
import gov.moda.dw.verifier.vc.vo.Definition;
import gov.moda.dw.verifier.vc.vo.VpException;
import java.net.URI;
import java.security.interfaces.ECPublicKey;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CredentialValidateTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(CredentialValidateTask.class);

    public ContentResult validateJwtContent(JwtVerifiableCredential jwtVc, List<Disclosure> disclosureList) throws VpException {

        try {
            boolean contentOK = true;

            JWSHeader jwsHeader = jwtVc.getJwsObject().getHeader();

            // check typ
            String typ = Optional.ofNullable(jwsHeader.getType())
                                 .map(JOSEObjectType::getType)
                                 .orElse(null);
            if (typ != null && !typ.isBlank()) {
                LOGGER.info("[check vc typ]: PASS");
            } else {
                LOGGER.info("[check vc typ]: FAIL -> invalid vc typ");
                contentOK = false;
            }

            // check jku
            URI jwkUrl = jwsHeader.getJWKURL();
            String jku = Optional.ofNullable(jwkUrl)
                                 .map(URI::toString)
                                 .orElse(null);
            if (jku != null && !jku.isBlank()) {
                LOGGER.info("[check vc jku]: PASS");
            } else {
                LOGGER.info("[check vc jku]: FAIL -> invalid vc jku");
                contentOK = false;
            }

            // check kid
            String kid = jwsHeader.getKeyID();
            if (kid != null && !kid.isBlank()) {
                LOGGER.info("[check vc kid]: PASS");
            } else {
                LOGGER.info("[check vc kid]: FAIL -> invalid vc kid");
                contentOK = false;
            }

            JWTClaimsSet jwtClaimsSet = jwtVc.getPayload();

            // check iss (issuer)
            String iss = jwtClaimsSet.getIssuer();
            if (iss != null && !iss.isBlank()) {
                LOGGER.info("[check vc iss]: PASS");
            } else {
                LOGGER.info("[check vc iss]: FAIL -> invalid vc iss");
                contentOK = false;
            }

            // check sub (subject)
            String sub = jwtClaimsSet.getSubject();
            if (sub != null && !sub.isBlank()) {
                LOGGER.info("[check vc sub]: PASS");
            } else {
                LOGGER.info("[check vc sub]: FAIL -> invalid vc sub");
                contentOK = false;
            }

            // check jti (jwt id)
            String jti = jwtClaimsSet.getJWTID();
            if (jti != null && !jti.isBlank()) {
                LOGGER.info("[check vc jti]: PASS");
            } else {
                LOGGER.info("[check vc jti]: FAIL -> invalid vc jti");
                contentOK = false;
            }

            // check nbf (issuance date)
            Date nbf = jwtClaimsSet.getNotBeforeTime();
            if (nbf != null && nbf.before(new Date())) {
                LOGGER.info("[check vc nbf]: PASS");
            } else {
                LOGGER.info("[check vc nbf]: FAIL -> invalid vc nbf");
                contentOK = false;
            }

            // check exp (expiration date)
            Date exp = jwtClaimsSet.getExpirationTime();
            if (exp != null && exp.after(new Date())) {
                LOGGER.info("[check vc exp]: PASS");
            } else {
                LOGGER.info("[check vc exp]: FAIL -> invalid vc exp");
                contentOK = false;
            }

            // check nonce
            String nonce = (String) jwtClaimsSet.getClaim(Definition.JWT_PAYLOAD_FIELD_NONCE);
            if (nonce != null && !nonce.isBlank()) {
                LOGGER.info("[check vc nonce]: PASS");
            } else {
                LOGGER.info("[check vc nonce]: FAIL -> invalid vc nonce");
                contentOK = false;
            }

            // check cnf (holder's public key)
            Map<String, Object> holderPublicKey = null;
            try {
                Map<String, Object> cnf = jwtClaimsSet.getJSONObjectClaim(Definition.JWT_PAYLOAD_FIELD_CNF);
                holderPublicKey = JsonUtils.objectToMap(cnf.get(Definition.JWT_PAYLOAD_FIELD_CNF_JWK));
                if (holderPublicKey != null && !holderPublicKey.isEmpty()) {
                    LOGGER.info("[check vc cnf]: PASS");
                } else {
                    LOGGER.info("[check vc cnf]: FAIL -> invalid vc cnf");
                    contentOK = false;
                }

                if (contentOK) {
                    if (validateCnfAndSub(holderPublicKey, sub)) {
                        LOGGER.info("[check vc cnf & sub]: PASS");
                    } else {
                        LOGGER.info("[check vc cnf & sub]: FAIL -> vc cnf and sub is not matched");
                        contentOK = false;
                    }
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                contentOK = false;
            }

            // check vc claim
            String schemaUrl = null;
            VerifiableCredential vc = null;
            List<StatusListInfo> statusListInfos = null;
            try {
                Map<String, Object> map = jwtClaimsSet.getJSONObjectClaim(Definition.JWT_PAYLOAD_FIELD_VC);
                if (map != null && !map.isEmpty()) {
                    LOGGER.info("[check vc claim]: PASS");

                    // reconstruct raw vc from sd-jwt+vc
                    vc = CredentialUtils.reconstructVc(jwtVc, disclosureList);

                    // check credential type
                    List<String> credentialTypes = vc.getTypes();
                    if (credentialTypes != null && credentialTypes.size() >= 2 &&
                        credentialTypes.contains(Definition.DEFAULT_VERIFIABLE_CREDENTIAL_TYPE)) {
                        LOGGER.info("[check credential type]: PASS");
                    } else {
                        LOGGER.info("[check credential type]: FAIL -> invalid credential type");
                        contentOK = false;
                    }

                    // check credential status
                    try {
                        // extract single statusList or multi statusList
                        statusListInfos = extractStatusList(vc);
                        LOGGER.info("[check credential status]: PASS");
                    } catch (Exception e) {
                        LOGGER.error("[check credential status]: FAIL -> invalid credential status, {}", e.getMessage());
                        contentOK = false;
                    }

                    // check credential schema
                    Map<String, Object> credentialSchema = JsonLDUtils.jsonLdGetJsonObject(vc.getJsonObject(), Definition.VC_FIELD_CREDENTIAL_SCHEMA);
                    schemaUrl = Optional.ofNullable(credentialSchema)
                                        .map(m -> (String) m.get("id"))
                                        .orElse(null);
                    if (schemaUrl != null) {
                        LOGGER.info("[check schema id]: PASS");
                    } else {
                        LOGGER.info("[check schema id]: FAIL -> invalid schema id");
                        contentOK = false;
                    }

                    // check credential subject
                    CredentialSubject credentialSubject = vc.getCredentialSubject();
                    Map<String, Object> claims = (credentialSubject != null) ? credentialSubject.getClaims() : null;
                    if (claims != null) {
                        LOGGER.info("[check credential subject]: PASS");
                    } else {
                        LOGGER.info("[check credential subject]: FAIL -> invalid credential subject");
                        contentOK = false;
                    }
                } else {
                    LOGGER.info("[check vc claim]: FAIL -> invalid vc claim");
                    contentOK = false;
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                contentOK = false;
            }

            return new ContentResult()
                .setContentOK(contentOK)
                .setJku(jku)
                .setKid(kid)
                .setHolderPublicKey(holderPublicKey)
                .setStatusListInfo(statusListInfos)
                .setSchemaUrl(schemaUrl)
                .setVc(vc);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new VpException(VpException.ERR_CRED_VALIDATE_VC_CONTENT_ERROR, "fail to validate vc content");
        }
    }

    public boolean validateJwtProof(JwtVerifiableCredential jwtVc, Map<String, Object> issuerPublicKey) throws VpException {

        boolean proofOK;

        try {
            if (issuerPublicKey == null || issuerPublicKey.isEmpty()) {
                throw new VpException(VpException.ERR_CRED_LACK_OF_ISSUER_PUBLIC_KEY, "lack of issuer's public key");
            }

            // prepare issuer's public key
            ECKey ecKey = ECKey.parse(issuerPublicKey);
            ECPublicKey publicKey = ecKey.toECPublicKey();

            // check vc proof
            ByteVerifier verifier = new P_256_ES256_PublicKeyVerifier(publicKey);
            proofOK = jwtVc.verify_P_256_ES256(verifier);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new VpException(VpException.ERR_CRED_VALIDATE_VC_PROOF_ERROR, "fail to validate vc proof");
        }

        return proofOK;
    }

    public boolean validateCnfAndSub(Map<String, Object> holderPublicKey, String sub) throws VpException, ParseException {
        Map<String, Object> publicKey = DidUtils.extractIssuerPublicKey(sub);
        JWK didPublicKeyJWK = JWK.parse(publicKey);
        JWK holderPublicKeyJWK = JWK.parse(holderPublicKey);
        return didPublicKeyJWK.getRequiredParams().equals(holderPublicKeyJWK.getRequiredParams());
    }

    public static List<StatusListInfo> extractStatusList(VerifiableCredential vc) throws VpException {
        Object credentialStatusValue = vc.getJsonObject().get(CredentialStatus.DEFAULT_JSONLD_PREDICATE);
        if (credentialStatusValue == null) {
            throw new VpException("parse vc error, can not get credentialStatus.");
        }
        JsonNode credentialStatus = JsonUtils.getJsonNode(credentialStatusValue);
        if (credentialStatus.isNull()) {
            throw new VpException("parse vc error, invalid credentialStatus json format.");
        }

        String statusListCredential;
        int statusListIndex;
        String statusPurpose;
        if (credentialStatus.isObject()) {
            statusListCredential = credentialStatus.get(Definition.VC_FIELD_STATUS_LIST_CREDENTIAL).asText("");
            statusListIndex = credentialStatus.get(Definition.VC_FIELD_STATUS_LIST_INDEX).asInt(-1);
            statusPurpose = credentialStatus.get(Definition.VC_FIELD_STATUS_PURPOSE).asText("");
            return List.of(new StatusListInfo(statusListCredential, statusListIndex, statusPurpose));
        } else if (credentialStatus.isArray()) {
            ArrayList<StatusListInfo> statusListInfos = new ArrayList<>();
            ArrayNode credentialStatusArray = (ArrayNode) credentialStatus;
            for (JsonNode node : credentialStatusArray) {
                statusListCredential = node.get(Definition.VC_FIELD_STATUS_LIST_CREDENTIAL).asText("");
                statusListIndex = node.get(Definition.VC_FIELD_STATUS_LIST_INDEX).asInt(-1);
                statusPurpose = node.get(Definition.VC_FIELD_STATUS_PURPOSE).asText("");
                statusListInfos.add(new StatusListInfo(statusListCredential, statusListIndex, statusPurpose));
            }
            return statusListInfos;
        } else {
            throw new VpException("parse vc error, invalid credentialStatus json format(not json or json array).");
        }
    }

    public static class ContentResult {

        private boolean contentOK;
        private String jku;
        private String kid;
        private Map<String, Object> holderPublicKey;
        private String schemaUrl;
        // raw vc
        private VerifiableCredential vc;
        private List<StatusListInfo> statusListInfo;

        public boolean isContentOK() {
            return contentOK;
        }

        public ContentResult setContentOK(boolean contentOK) {
            this.contentOK = contentOK;
            return this;
        }

        public String getJku() {
            return jku;
        }

        public ContentResult setJku(String jku) {
            this.jku = jku;
            return this;
        }

        public String getKid() {
            return kid;
        }

        public ContentResult setKid(String kid) {
            this.kid = kid;
            return this;
        }

        public Map<String, Object> getHolderPublicKey() {
            return holderPublicKey;
        }

        public ContentResult setHolderPublicKey(Map<String, Object> holderPublicKey) {
            this.holderPublicKey = holderPublicKey;
            return this;
        }

        public String getSchemaUrl() {
            return schemaUrl;
        }

        public ContentResult setSchemaUrl(String schemaUrl) {
            this.schemaUrl = schemaUrl;
            return this;
        }

        public VerifiableCredential getVc() {
            return vc;
        }

        public ContentResult setVc(VerifiableCredential vc) {
            this.vc = vc;
            return this;
        }

        public List<StatusListInfo> getStatusListInfo() {
            return statusListInfo;
        }

        public ContentResult setStatusListInfo(List<StatusListInfo> statusListInfo) {
            this.statusListInfo = statusListInfo;
            return this;
        }
    }
}
