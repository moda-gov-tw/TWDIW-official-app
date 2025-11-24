package gov.moda.dw.issuer.vc.task;

import com.danubetech.keyformats.crypto.ByteVerifier;
import com.danubetech.keyformats.crypto.impl.P_256_ES256_PublicKeyVerifier;
import com.danubetech.verifiablecredentials.VerifiableCredential;
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

/**
 * status list validate task
 *
 * @version 20240902
 */
public class StatusListValidateTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatusListValidateTask.class);

    private final String statusListCredentialType;
    private final String statusList;
    private final String statusListKey;

    private StatusListValidateTask(Builder builder) {
        this.statusListCredentialType = builder.statusListCredentialType;
        this.statusList = builder.statusList;
        this.statusListKey = builder.statusListKey;
    }

    public Result start() throws VcException {

        try {
            // prepare status list signing key
            ECKey ecKey = ECKey.parse(statusListKey);
            ECPublicKey publicKey = ecKey.toECPublicKey();

            // re-construct JWT verifiable credential
            JwtVerifiableCredential jwtVc = JwtVerifiableCredential.fromCompactSerialization(statusList);

            // validate vc proof
            ByteVerifier verifier = new P_256_ES256_PublicKeyVerifier(publicKey);
            boolean proofOK = validateJwtProof(verifier, jwtVc);
            LOGGER.info("validate vc proof = {}", proofOK);

            // validate vc content
            boolean contentOK = validateJwtContent(statusListCredentialType, jwtVc);
            LOGGER.info("validate vc content = {}", contentOK);

            return new Result().setProofOK(proofOK).setContentOK(contentOK);

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new VcException(VcException.ERR_SL_VERIFY_STATUS_LIST_ERROR, "fail to verify status list");
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
        if (credentialTypeList.contains(credentialType) && credentialTypeList.contains(Definition.DEFAULT_STATUS_LIST_CREDENTIAL_TYPE)) {
            LOGGER.info("{check credential type}: PASS");
        } else {
            LOGGER.info("{check credential type}: FAIL -> lack of credential type");
            contentOK = false;
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

        private String statusListCredentialType;
        private String statusList;
        private String statusListKey;

        public Builder() {
        }

        public Builder setStatusListCredentialType(String statusListCredentialType) {
            this.statusListCredentialType = statusListCredentialType;
            return this;
        }

        public Builder setStatusList(String statusList) {
            this.statusList = statusList;
            return this;
        }

        public Builder setStatusListKey(String statusListKey) {
			this.statusListKey = statusListKey;
			return this;
		}

		public StatusListValidateTask build() {
            // TODO: check all required fields
            return new StatusListValidateTask(this);
        }
    }
}
