package gov.moda.dw.verifier.oidvp.util;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.factories.DefaultJWSSignerFactory;
import com.nimbusds.jose.crypto.impl.ECDSA;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JWKUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(JWKUtils.class);

    public static JWKSet buildJWKSet(JWK... jwks) {
        List<JWK> jwkList = Arrays.asList(jwks);
        return new JWKSet(jwkList);
    }

    public static ECKey getDefaultECKey(String kid) {
        try {
            return new ECKeyGenerator(Curve.P_256).keyID(kid).generate();
        } catch (JOSEException e) {
            LOGGER.error("generate ECKey error: {}", e.getMessage(), e);
            return null;
        }
    }

    public static JWSSigner getJWSSigner(JWK jwk) throws JOSEException {
        DefaultJWSSignerFactory jwsSignerFactory = new DefaultJWSSignerFactory();
        return jwsSignerFactory.createJWSSigner(jwk);
    }

    public static JWSSigner getJWSSigner(JWK jwk, JWSAlgorithm algorithm) throws JOSEException {
        DefaultJWSSignerFactory jwsSignerFactory = new DefaultJWSSignerFactory();
        return jwsSignerFactory.createJWSSigner(jwk, algorithm);
    }

    public static JWSAlgorithm getDefaultJWSAlgorithm(JWK jwk) throws JOSEException {
        if (jwk instanceof ECKey) {
            return ECDSA.resolveAlgorithm(jwk.toECKey().getCurve());
        } else if (jwk instanceof RSAKey) {
            return JWSAlgorithm.RS256;
        } else if (jwk instanceof OctetSequenceKey) {
            return JWSAlgorithm.HS256;
        } else if (jwk instanceof OctetKeyPair) {
            return JWSAlgorithm.EdDSA;
        } else {
            throw new IllegalArgumentException("unsupported key type for jws");
        }
    }
}
