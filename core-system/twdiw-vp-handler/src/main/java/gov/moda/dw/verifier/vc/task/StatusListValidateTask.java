package gov.moda.dw.verifier.vc.task;

import com.danubetech.keyformats.crypto.ByteVerifier;
import com.danubetech.keyformats.crypto.impl.P_256_ES256_PublicKeyVerifier;
import com.danubetech.verifiablecredentials.VerifiableCredential;
import com.danubetech.verifiablecredentials.jwt.JwtVerifiableCredential;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jwt.JWTClaimsSet;
import gov.moda.dw.verifier.vc.vo.Definition;
import gov.moda.dw.verifier.vc.vo.VpException;
import java.net.URI;
import java.security.interfaces.ECPublicKey;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatusListValidateTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatusListValidateTask.class);

    public ContentResult validateJwtContent(JwtVerifiableCredential jwtVc) throws VpException {

        try {
            boolean contentOK = true;

            JWSHeader jwsHeader = jwtVc.getJwsObject().getHeader();

            // check typ
            String typ = Optional.ofNullable(jwsHeader.getType())
                                 .map(JOSEObjectType::getType)
                                 .orElse(null);
            if (typ != null && !typ.isBlank()) {
                LOGGER.info("[check status list typ]: PASS");
            } else {
                LOGGER.info("[check status list typ]: FAIL -> invalid status list typ");
                contentOK = false;
            }

            // check jku
            URI jwkUrl = jwsHeader.getJWKURL();
            String jku = Optional.ofNullable(jwkUrl)
                                 .map(URI::toString)
                                 .orElse(null);
            if (jku != null && !jku.isBlank()) {
                LOGGER.info("[check status list jku]: PASS");
            } else {
                LOGGER.info("[check status list jku]: FAIL -> invalid status list jku");
                contentOK = false;
            }

            // check kid
            String kid = jwsHeader.getKeyID();
            if (kid != null && !kid.isBlank()) {
                LOGGER.info("[check status list kid]: PASS");
            } else {
                LOGGER.info("[check status list kid]: FAIL -> invalid status list kid");
                contentOK = false;
            }

            JWTClaimsSet jwtClaimsSet = jwtVc.getPayload();

            // check iss (issuer)
            String iss = jwtClaimsSet.getIssuer();
            if (iss != null && !iss.isBlank()) {
                LOGGER.info("[check status list iss]: PASS");
            } else {
                LOGGER.info("[check status list iss]: FAIL -> invalid status list iss");
                contentOK = false;
            }

            // check sub (subject)
            String sub = jwtClaimsSet.getSubject();
            if (sub != null && !sub.isBlank()) {
                LOGGER.info("[check status list sub]: PASS");
            } else {
                LOGGER.info("[check status list sub]: FAIL -> invalid status list sub");
                contentOK = false;
            }

            // check jti (jwt id)
            String jti = jwtClaimsSet.getJWTID();
            if (jti != null && !jti.isBlank()) {
                LOGGER.info("[check status list jti]: PASS");
            } else {
                LOGGER.info("[check status list jti]: FAIL -> invalid status list jti");
                contentOK = false;
            }

            // check nbf (issuance date)
            Date nbf = jwtClaimsSet.getNotBeforeTime();
            if (nbf != null && nbf.before(new Date())) {
                LOGGER.info("[check status list nbf]: PASS");
            } else {
                LOGGER.info("[check status list nbf]: FAIL -> invalid status list nbf");
                contentOK = false;
            }

            // check exp (expiration date)
            Date exp = jwtClaimsSet.getExpirationTime();
            if (exp != null && exp.after(new Date())) {
                LOGGER.info("[check status list exp]: PASS");
            } else {
                LOGGER.info("[check status list exp]: FAIL -> invalid status list exp");
                contentOK = false;
            }

            // check nonce
            String nonce = (String) jwtClaimsSet.getClaim(Definition.JWT_PAYLOAD_FIELD_NONCE);
            if (nonce != null && !nonce.isBlank()) {
                LOGGER.info("[check status list nonce]: PASS");
            } else {
                LOGGER.info("[check status list nonce]: FAIL -> invalid vc nonce");
                contentOK = false;
            }

            // check vc claim
            String credentialType = null;
            String encodedList = null;
            String statusPurpose = "";
            try {
                Map<String, Object> map = jwtClaimsSet.getJSONObjectClaim(Definition.JWT_PAYLOAD_FIELD_VC);
                if (map != null && !map.isEmpty()) {
                    LOGGER.info("[check status list claim]: PASS");

                    VerifiableCredential vc = VerifiableCredential.fromMap(map);

                    // check credential type
                    List<String> credentialTypes = vc.getTypes();
                    if (credentialTypes != null && credentialTypes.size() >= 2 &&
                        credentialTypes.contains(Definition.DEFAULT_STATUS_LIST_CREDENTIAL_TYPE)) {
                        LOGGER.info("[check status list credential type]: PASS");
                        credentialType = credentialTypes.get(1);
                    } else {
                        LOGGER.info("[check status list credential type]: FAIL -> invalid credential type");
                        contentOK = false;
                    }

                    // check credential subject
                    Map<String, Object> claims = (vc.getCredentialSubject() != null) ? vc.getCredentialSubject().getClaims() : null;
                    encodedList = Optional.ofNullable(claims)
                        .map(c -> (String) c.get(Definition.STATUS_LIST_FIELD_ENCODED_LIST))
                        .orElse(null);
                    if (encodedList != null && !encodedList.isEmpty()) {
                        LOGGER.info("[check status list encoded list]: PASS");
                    } else {
                        LOGGER.info("[check status list encoded list]: FAIL -> invalid status list encoded list");
                        contentOK = false;
                    }

                    // get statusPurpose
                    statusPurpose = Optional.ofNullable(claims)
                                            .map(c -> (String) c.get(Definition.STATUS_LIST_FIELD_STATUS_PURPOSE))
                                            .orElse("");
                } else {
                    LOGGER.info("[check status list claim]: FAIL -> invalid status list claim");
                    contentOK = false;
                }

            } catch (Exception e) {
                LOGGER.error(e.getMessage());
                contentOK = false;
            }

            return new ContentResult()
                .setContentOK(contentOK)
                .setEncodedList(encodedList)
                .setStatusPurpose(statusPurpose)
                .setJku(jku)
                .setKid(kid);

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new VpException(VpException.ERR_SL_VALIDATE_STATUS_LIST_CONTENT_ERROR, "fail to validate status list content");
        }
    }

    public boolean validateJwtProof(JwtVerifiableCredential jwtVc, Map<String, Object> issuerPublicKey) throws VpException {

        boolean proofOK;

        try {
            if (issuerPublicKey == null || issuerPublicKey.isEmpty()) {
                throw new VpException(VpException.ERR_SL_LACK_OF_ISSUER_PUBLIC_KEY, "lack of issuer's public key");
            }

            // prepare issuer's public key
            ECKey ecKey = ECKey.parse(issuerPublicKey);
            ECPublicKey publicKey = ecKey.toECPublicKey();

            // check status list proof
            ByteVerifier verifier = new P_256_ES256_PublicKeyVerifier(publicKey);
            proofOK = jwtVc.verify_P_256_ES256(verifier);

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new VpException(VpException.ERR_SL_VALIDATE_STATUS_LIST_PROOF_ERROR, "fail to validate status list proof");
        }

        return proofOK;
    }

    public static class ContentResult {

        private boolean contentOK;
        private String encodedList;
        private String jku;
        private String kid;
        private String statusPurpose;

        public boolean isContentOK() {
            return contentOK;
        }

        public ContentResult setContentOK(boolean contentOK) {
            this.contentOK = contentOK;
            return this;
        }

        public String getEncodedList() {
            return encodedList;
        }

        public ContentResult setEncodedList(String encodedList) {
            this.encodedList = encodedList;
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

        public String getStatusPurpose() {
            return statusPurpose;
        }

        public ContentResult setStatusPurpose(String statusPurpose) {
            this.statusPurpose = statusPurpose;
            return this;
        }
    }
}
