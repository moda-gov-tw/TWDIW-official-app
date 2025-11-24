package gov.moda.dw.verifier.vc.task;

import com.danubetech.keyformats.crypto.ByteVerifier;
import com.danubetech.keyformats.crypto.impl.P_256_ES256_PublicKeyVerifier;
import com.danubetech.verifiablecredentials.jwt.JwtVerifiablePresentation;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jwt.JWTClaimsSet;
import gov.moda.dw.verifier.vc.util.DateUtils;
import gov.moda.dw.verifier.vc.vo.Definition;
import gov.moda.dw.verifier.vc.vo.VpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.interfaces.ECPublicKey;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class PresentationValidateTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(PresentationValidateTask.class);

    public ContentResult validateJwtContent(JwtVerifiablePresentation jwtVp) throws VpException {

        try {
            boolean contentOK = true;

            JWTClaimsSet jwtClaimsSet = jwtVp.getPayload();

            // check iss (issuer)
            String iss = jwtClaimsSet.getIssuer();
            if (iss != null && !iss.isBlank()) {
                LOGGER.info("[check vp iss]: PASS");
            } else {
                LOGGER.info("[check vp iss]: FAIL -> invalid vp iss");
                contentOK = false;
            }

            // check aud (audience)
            List<String> audience = jwtClaimsSet.getAudience();
            String clientId = null;
            if (audience != null && !audience.isEmpty() && audience.get(0) != null && !audience.get(0).isBlank()) {
                LOGGER.info("[check vp aud]: PASS");
                clientId = audience.get(0);
            } else {
                LOGGER.info("[check vp aud]: FAIL -> invalid vp audience");
                contentOK = false;
            }

            // check jti (jwt id)
            String jti = jwtClaimsSet.getJWTID();
            if (jti != null && !jti.isBlank()) {
                LOGGER.info("[check vp jti]: PASS");
            } else {
                LOGGER.info("[check vp jti]: FAIL -> invalid vp jti");
                contentOK = false;
            }

            // check nbf (issuance date)
            Date nbf = jwtClaimsSet.getNotBeforeTime();
            // tolerance: 30 seconds
            LocalDateTime ldt = DateUtils.calculate(LocalDateTime.now(), DateUtils.TimeUnit.SECOND, 30);
            Date after = DateUtils.toDate(ldt);
            LOGGER.info("nbf = {}, after = {}", nbf, after);
            if (nbf != null) {
                LOGGER.info("nbf check = {}", nbf.before(after));
            }

            if (nbf != null && nbf.before(after)) {
                LOGGER.info("[check vp nbf]: PASS");
            } else {
                LOGGER.info("[check vp nbf]: FAIL -> invalid vp nbf");
                contentOK = false;
            }

            // check exp (expiration date)
            Date exp = jwtClaimsSet.getExpirationTime();
            if (exp != null && exp.after(new Date())) {
                LOGGER.info("[check vp exp]: PASS");
            } else {
                LOGGER.info("[check vp exp]: FAIL -> invalid vp exp");
                contentOK = false;
            }

            // check nonce
            String nonce = (String) jwtClaimsSet.getClaim(Definition.JWT_PAYLOAD_FIELD_NONCE);
            if (nonce != null && !nonce.isBlank()) {
                LOGGER.info("[check vp nonce]: PASS");
            } else {
                LOGGER.info("[check vp nonce]: FAIL -> invalid vp nonce");
                contentOK = false;
            }

            // check vp claim
            List<String> vcList = new ArrayList<>();
            try {
                Map<String, Object> claim = jwtClaimsSet.getJSONObjectClaim(Definition.JWT_PAYLOAD_FIELD_VP);
                if (claim != null && !claim.isEmpty()) {

                    LOGGER.info("[check vp claim]: PASS");

                    // check existence of vc
                    if (claim.containsKey(Definition.VP_FIELD_VC)) {
                        LOGGER.info("[check vp contain vc]: PASS");
                        Object object = claim.get(Definition.VP_FIELD_VC);
                        if (object instanceof List<?> vcs) {
                            for (Object obj : vcs) {
                                // only supported JWT VC
                                vcList.add((String) obj);
                            }
                        }

                    } else {
                        LOGGER.info("[check vp contain vc]: FAIL -> vp DOES NOT contain any vc(s)");
                        contentOK = false;
                    }

                } else {
                    LOGGER.info("[check vp claim]: FAIL -> invalid vp claim");
                    contentOK = false;
                }

            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                contentOK = false;
            }

            return new ContentResult()
                .setContentOK(contentOK)
                .setClientId(clientId)
                .setNonce(nonce)
                .setVcList(vcList);

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new VpException(VpException.ERR_PRES_VALIDATE_VP_CONTENT_ERROR, "fail to validate vp content");
        }
    }

    public boolean validateJwtProof(JwtVerifiablePresentation jwtVp, Map<String, Object> holderPublicKey) throws VpException {

        boolean proofOK;

        try {
            if (holderPublicKey == null || holderPublicKey.isEmpty()) {
                throw new VpException(VpException.ERR_PRES_LACK_OF_HOLDER_PUBLIC_KEY, "lack of holder's public key in vc");
            }

            // TODO: support `holder's public key in vp` or `get holder's public key from frontend service`

            // prepare holder's public key
            ECKey ecKey = ECKey.parse(holderPublicKey);
            ECPublicKey publicKey = ecKey.toECPublicKey();

            // check vp proof
            ByteVerifier verifier = new P_256_ES256_PublicKeyVerifier(publicKey);
            proofOK = jwtVp.verify_P_256_ES256(verifier);

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new VpException(VpException.ERR_PRES_VALIDATE_VP_PROOF_ERROR, "fail to validate vp proof");
        }

        return proofOK;
    }

    public static class ContentResult {

        private boolean contentOK;
        private String clientId;
        private String nonce;
        private List<String> vcList;

        public boolean isContentOK() {
            return contentOK;
        }

        public ContentResult setContentOK(boolean contentOK) {
            this.contentOK = contentOK;
            return this;
        }

        public String getClientId() {
            return clientId;
        }

        public ContentResult setClientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public String getNonce() {
            return nonce;
        }

        public ContentResult setNonce(String nonce) {
            this.nonce = nonce;
            return this;
        }

        public List<String> getVcList() {
            return vcList;
        }

        public ContentResult setVcList(List<String> vcList) {
            this.vcList = vcList;
            return this;
        }
    }
}
