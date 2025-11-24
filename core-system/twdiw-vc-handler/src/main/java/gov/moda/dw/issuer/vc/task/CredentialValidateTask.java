package gov.moda.dw.issuer.vc.task;

import com.authlete.sd.SDJWT;
import com.danubetech.keyformats.crypto.ByteVerifier;
import com.danubetech.keyformats.crypto.impl.P_256_ES256_PublicKeyVerifier;
import com.danubetech.verifiablecredentials.VerifiableCredential;
import com.danubetech.verifiablecredentials.credentialstatus.CredentialStatus;
import com.danubetech.verifiablecredentials.jwt.FromJwtConverter;
import com.danubetech.verifiablecredentials.jwt.JwtVerifiableCredential;
import com.nimbusds.jose.jwk.ECKey;
import gov.moda.dw.issuer.vc.util.JsonUtils;
import gov.moda.dw.issuer.vc.vo.Definition;
import gov.moda.dw.issuer.vc.vo.VcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.interfaces.ECPublicKey;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * credential validate task
 *
 * @version 20240902
 */
public class CredentialValidateTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(CredentialValidateTask.class);

    private final String credentialType;
    private final String credential;
    private final String issuerKey;

    private CredentialValidateTask(Builder builder) {
        this.credentialType = builder.credentialType;
        this.credential = builder.credential;
        this.issuerKey = builder.issuerKey;
    }

    public Result start() throws VcException {

        try {
            // prepare issuer's public key
            ECKey ecKey = ECKey.parse(issuerKey);
            ECPublicKey publicKey = ecKey.toECPublicKey();

            // re-construct JWT verifiable credential
            SDJWT sdJwt = SDJWT.parse(credential);
            String credentialJwt = sdJwt.getCredentialJwt();
            JwtVerifiableCredential jwtVc = JwtVerifiableCredential.fromCompactSerialization(credentialJwt);

            // validate vc proof
            ByteVerifier verifier = new P_256_ES256_PublicKeyVerifier(publicKey);
            boolean proofOK = validateJwtProof(verifier, jwtVc);
            LOGGER.info("validate vc proof = {}", proofOK);

            // validate vc content
            boolean contentOK = validateJwtContent(credentialType, jwtVc);
            LOGGER.info("validate vc content = {}", contentOK);

            return new Result().setProofOK(proofOK).setContentOK(contentOK);

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new VcException(VcException.ERR_CRED_VERIFY_VC_ERROR, "fail to verify credential");
        }
    }

    private boolean validateJwtProof(ByteVerifier verifier, JwtVerifiableCredential jwtVc) throws Exception {

        boolean proofOK = false;

        if (verifier != null) {
            if (verifier instanceof P_256_ES256_PublicKeyVerifier) {
                proofOK = jwtVc.verify_P_256_ES256(verifier);
            } else {
                throw new Exception("unsupported verifier");
            }
        }

        return proofOK;
    }

    private boolean validateJwtContent(String credentialType, JwtVerifiableCredential jwtVc) {

        boolean contentOK = true;

        VerifiableCredential vc = FromJwtConverter.fromJwtVerifiableCredential(jwtVc);

        // check issuer
        String issuerId = vc.getIssuer().toString();
        if (issuerId != null && !issuerId.isBlank()) {
            LOGGER.info("{check issuer id}: PASS");
        } else {
            LOGGER.info("{check issuer id}: FAIL -> invalid issuer id");
            contentOK = false;
        }

        // check subject identifier
        String subjectId = vc.getCredentialSubject().getId().toString();
        if (subjectId != null && !subjectId.isBlank()) {
            LOGGER.info("{check subject id}: PASS");
        } else {
            LOGGER.info("{check subject id}: FAIL -> invalid credential subject id");
            contentOK = false;
        }

        // check credential type
        List<String> credentialTypeList = vc.getTypes();
        if (credentialTypeList.contains(credentialType) && credentialTypeList.contains(Definition.DEFAULT_VERIFIABLE_CREDENTIAL_TYPE)) {
            LOGGER.info("{check credential type}: PASS");
        } else {
            LOGGER.info("{check credential type}: FAIL -> lack of credential type");
            contentOK = false;
        }

        // check credential status
        List<Map<String, Object>> statusLists = (List<Map<String, Object>>) vc.getJsonObject().get("credentialStatus");
        if(statusLists == null || statusLists.isEmpty() || statusLists.size() != 2) {
        	LOGGER.info("{check status list id}: FAIL -> status lists is null or empty or invalid");
            contentOK = false;
        } else {
        	for (Map<String, Object> statusMap : statusLists) {       		
        	    CredentialStatus cs = CredentialStatus.fromMap(statusMap);
        	    String statusListId = cs.getId().toString();
        	    if (statusListId != null && !statusListId.isBlank()) {
        	    	LOGGER.info("{check status list id}: {}", statusListId);
                    LOGGER.info("{check status list id}: PASS");
                } else {
                    LOGGER.info("{check status list id}: FAIL -> invalid status list id");
                    contentOK = false;
                }
        	}
        }

        // check issuance date
        Date issuanceDate = vc.getIssuanceDate();
        if (issuanceDate != null && issuanceDate.before(new Date())) {
            LOGGER.info("{check issuance date}: PASS");
        } else {
            LOGGER.info("{check issuance date}: FAIL -> invalid issuance date ({})", issuanceDate);
            contentOK = false;
        }

        // check expiration date
        Date expirationDate = vc.getExpirationDate();
        if (expirationDate != null && expirationDate.after(new Date())) {
            LOGGER.info("{check expiration date}: PASS");
        } else {
            LOGGER.info("{check expiration date}: FAIL -> credential expired ({})", expirationDate);
            contentOK = false;
        }

        return contentOK;
    }

    /**
     * check result
     *
     * @version 20240902
     */
    public static class Result {

        private boolean proofOK = false;
        private boolean contentOK = false;

        public boolean isProofOK() {
            return proofOK;
        }

        public Result setProofOK(boolean proofOK) {
            this.proofOK = proofOK;
            return this;
        }

        public boolean isContentOK() {
            return contentOK;
        }

        public Result setContentOK(boolean contentOK) {
            this.contentOK = contentOK;
            return this;
        }

        public String toString() {
            return JsonUtils.voToJs(this);
        }
    }

    public static class Builder {

        private String credentialType;
        private String credential;
        private String issuerKey;

        public Builder() {
        }

        public Builder setCredentialType(String credentialType) {
            this.credentialType = credentialType;
            return this;
        }

        public Builder setCredential(String credential) {
            this.credential = credential;
            return this;
        }

        public Builder setIssuerKey(String issuerKey) {
            this.issuerKey = issuerKey;
            return this;
        }

        public CredentialValidateTask build() {
            // TODO: check all required fields
            return new CredentialValidateTask(this);
        }
    }
}
