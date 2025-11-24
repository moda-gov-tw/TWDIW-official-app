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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * credential sign task
 *
 * @version 20240902
 */
public class CredentialSignTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(CredentialSignTask.class);

    private final VerifiableCredential verifiableCredential;
    private final List<Disclosure> disclosureList;
    private final Map<String, Object> holderPublicKey;
    private final String issuerKey;
    private final URI publicKeysUri;

    private CredentialSignTask(Builder builder) {
        this.verifiableCredential = builder.verifiableCredential;
        this.disclosureList = builder.disclosureList;
        this.holderPublicKey = builder.holderPublicKey;
        this.issuerKey = builder.issuerKey;
        this.publicKeysUri = builder.publicKeysUri;
    }

    public String start() throws VcException {

        try {
            // format conversion (json -> jwt)
            JwtVerifiableCredential jwtVc = ToJwtConverter.toJwtVerifiableCredential(verifiableCredential);

            // prepare issuer's key
            ECKey ecKey = ECKey.parse(issuerKey);
            ECPrivateKey privateKey = ecKey.toECPrivateKey();
            String kid = ecKey.getKeyID();

            // construct JWT claim set
            // add nonce
            // add holder's public key (`key binding` defined in SD-JWT spec)
            JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder(jwtVc.getPayload());
            builder.claim(Definition.JWT_PAYLOAD_FIELD_NONCE, RandUtils.generateString(8));
            if (holderPublicKey != null && !holderPublicKey.isEmpty()) {
            	Map<String, Object> cnfClaim = new HashMap<>();
            	cnfClaim.put("jwk", holderPublicKey);
                builder.claim(Definition.JWT_PAYLOAD_FIELD_CNF, cnfClaim);
            }
            JWTClaimsSet jwtClaimsSet = builder.build();

            // sign credential
            ByteSigner byteSigner = new P_256_ES256_PrivateKeySigner(privateKey);
            String jws = signJwt(
                byteSigner,
                jwtClaimsSet,
                Definition.JWT_HEADER_TYPE_VC_PLUS_SD_JWT,
                publicKeysUri,
                kid);

            // append disclosure list
            return new SDJWT(jws, disclosureList).toString();

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new VcException(VcException.ERR_CRED_SIGN_VC_ERROR, "fail to sign credential");
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
        private List<Disclosure> disclosureList;
        private Map<String, Object> holderPublicKey;
        private String issuerKey;
        private URI publicKeysUri;

        public Builder() {
        }

        public Builder setVerifiableCredential(VerifiableCredential verifiableCredential) {
            this.verifiableCredential = verifiableCredential;
            return this;
        }

        public Builder setDisclosureList(List<Disclosure> disclosureList) {
            this.disclosureList = disclosureList;
            return this;
        }

        public Builder setHolderPublicKey(Map<String, Object> holderPublicKey) {
            this.holderPublicKey = holderPublicKey;
            return this;
        }

        public Builder setIssuerKey(String issuerKey) {
            this.issuerKey = issuerKey;
            return this;
        }

        public Builder setPublicKeysUri(URI publicKeysUri) {
            this.publicKeysUri = publicKeysUri;
            return this;
        }

        public CredentialSignTask build() {
            // TODO: check all required fields
            return new CredentialSignTask(this);
        }
    }
}
