package gov.moda.dw.issuer.vc.task;

import com.authlete.sd.Disclosure;
import com.authlete.sd.SDJWT;
import com.danubetech.keyformats.crypto.ByteSigner;
import com.danubetech.keyformats.crypto.impl.P_256_ES256_PrivateKeySigner;
import com.danubetech.verifiablecredentials.VerifiableCredential;
import com.danubetech.verifiablecredentials.jwt.JwtVerifiableCredential;
import com.danubetech.verifiablecredentials.jwt.ToJwtConverter;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import gov.moda.dw.issuer.vc.util.RandUtils;
import gov.moda.dw.issuer.vc.vo.Definition;
import gov.moda.dw.issuer.vc.vo.VcException;
import info.weboftrust.ldsignatures.adapter.JWSSignerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.security.interfaces.ECPrivateKey;
import java.util.List;
import java.util.Map;

/**
 * status list sign task
 *
 * @version 20240902
 */
public class StatusListSignTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatusListSignTask.class);

    private final VerifiableCredential verifiableCredential;
    private final String statusListKey;
    private final URI publicKeysUri;

    private StatusListSignTask(Builder builder) {
        this.verifiableCredential = builder.verifiableCredential;
        this.statusListKey = builder.statusListKey;
        this.publicKeysUri = builder.publicKeysUri;
    }

    public String start() throws VcException {

        try {
            // format conversion (json -> jwt)
            JwtVerifiableCredential jwtVc = ToJwtConverter.toJwtVerifiableCredential(verifiableCredential);

            // prepare status list signing key
            ECKey ecKey = ECKey.parse(statusListKey);
            ECPrivateKey privateKey = ecKey.toECPrivateKey();
            String kid = ecKey.getKeyID();

            // construct JWT claim set
            // add nonce
            JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder(jwtVc.getPayload())
                .claim(Definition.JWT_PAYLOAD_FIELD_NONCE, RandUtils.generateString(8))
                .build();

            // sign credential
            ByteSigner byteSigner = new P_256_ES256_PrivateKeySigner(privateKey);
            return signJwt(
                byteSigner,
                jwtClaimsSet,
                null,
                publicKeysUri,
                kid);

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new VcException(VcException.ERR_SL_SIGN_STATUS_LIST_ERROR, "fail to sign status list");
        }
    }

    private String signJwt(
        ByteSigner byteSigner,
        JWTClaimsSet jwtClaimsSet,
        String jwtHeaderType,
        URI publicKeyUrl,
        String kid) throws Exception {

        JWSHeader.Builder jwsHeaderBuilder;
        JWSSigner jwsSigner;

        // only support P-256
        if (byteSigner instanceof P_256_ES256_PrivateKeySigner) {
            jwsHeaderBuilder = new JWSHeader.Builder(JWSAlgorithm.ES256);
            jwsSigner = new JWSSignerAdapter(byteSigner, JWSAlgorithm.ES256);
        } else {
            throw new Exception("unsupported signer");
        }

        // JWT header - typ
        if (jwtHeaderType == null || jwtHeaderType.isBlank()) {
            jwsHeaderBuilder.type(JOSEObjectType.JWT);
        } else {
            jwsHeaderBuilder.type(new JOSEObjectType(jwtHeaderType));
        }

        // JWT header - kid
        if (kid != null && !kid.isBlank()) {
            jwsHeaderBuilder.keyID(kid);
        }

        // JWT header - jku
        if (publicKeyUrl != null) {
            jwsHeaderBuilder.jwkURL(publicKeyUrl);
        }

        JWSHeader jwsHeader = jwsHeaderBuilder.build();

        if (jwtClaimsSet == null) {
            throw new Exception("invalid JWT claims set");
        }

        // construct JWS object, then sign
        SignedJWT signedJWT = new SignedJWT(jwsHeader, jwtClaimsSet);
        signedJWT.sign(jwsSigner);

        return signedJWT.serialize();
    }

    public static class Builder {

        private VerifiableCredential verifiableCredential;
        private String statusListKey;
        private URI publicKeysUri;

        public Builder() {
        }

        public Builder setVerifiableCredential(VerifiableCredential verifiableCredential) {
            this.verifiableCredential = verifiableCredential;
            return this;
        }

        public Builder setStatusListKey(String statusListKey) {
			this.statusListKey = statusListKey;
			return this;
		}

		public Builder setPublicKeysUri(URI publicKeysUri) {
            this.publicKeysUri = publicKeysUri;
            return this;
        }

        public StatusListSignTask build() {
            // TODO: check all required fields
            return new StatusListSignTask(this);
        }
    }
}
