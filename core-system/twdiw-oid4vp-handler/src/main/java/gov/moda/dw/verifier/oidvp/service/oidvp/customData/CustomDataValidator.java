package gov.moda.dw.verifier.oidvp.service.oidvp.customData;

import com.fasterxml.jackson.databind.JsonNode;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.factories.DefaultJWSVerifierFactory;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jwt.SignedJWT;
import gov.moda.dw.verifier.oidvp.model.errors.OidvpException;
import gov.moda.dw.verifier.oidvp.util.DIDUtils;
import gov.moda.dw.verifier.oidvp.util.JsonUtils;
import java.text.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomDataValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomDataValidator.class);
    public static final String CUSTOM_DATA_PREDICATE = "customData";

    public static CustomDataValidateResult validate(String holderDID, SignedJWT customDataJWT) {
        if (holderDID == null) {
            throw new IllegalArgumentException("holderDidKey is null");
        }
        if (customDataJWT == null) {
            throw new IllegalArgumentException("CustomDataJWT is null");
        }

        try {
            JWK jwk;
            JWSVerifier jwsVerifier;
            try {
                jwk = DIDUtils.extractPublicKeyFromDID(holderDID);
                jwsVerifier = new DefaultJWSVerifierFactory().createJWSVerifier(customDataJWT.getHeader(), jwk.toECKey().toECPublicKey());
            } catch (OidvpException e) {
                LOGGER.error("get public key from holderDid error", e);
                return CustomDataValidateResult.fail("get public key from holderDid error");
            } catch (JOSEException e) {
                LOGGER.error("create JWSVerifier error", e);
                return CustomDataValidateResult.fail("create JWSVerifier error");
            }

            try {
                boolean verified = customDataJWT.verify(jwsVerifier);
                if (verified) {
                    Object claim = customDataJWT.getJWTClaimsSet().getClaim(CUSTOM_DATA_PREDICATE);
                    JsonNode customData = JsonUtils.getJsonNode(claim);
                    return CustomDataValidateResult.success(new CustomData(customData));
                } else {
                    return CustomDataValidateResult.fail("CustomData signature is invalid");
                }
            } catch (JOSEException e) {
                LOGGER.error("CustomData jwt signature validate error", e);
                return CustomDataValidateResult.fail("CustomData jwt signature validate error");
            } catch (ParseException e) {
                LOGGER.error("parse CustomData jwt payload error", e);
                return CustomDataValidateResult.fail("parse CustomData jwt payload error");
            }
        } catch (Exception e) {
            LOGGER.error("CustomDataValidate unexpected error, {}", e.getMessage(), e);
            return CustomDataValidateResult.fail("CustomDataValidate occur unexpected error");
        }
    }


    public record CustomDataValidateResult(boolean validateResult, String errorMessage, CustomData customData) {

        public static CustomDataValidateResult success(CustomData customData) {
            return new CustomDataValidateResult(true, "validateResult", customData);
        }

        public static CustomDataValidateResult fail(String errorMessage) {
            return new CustomDataValidateResult(false, errorMessage, null);
        }
    }
}
