package gov.moda.dw.verifier.oidvp.service.metadata.jwk;

import com.nimbusds.jose.jwk.JWK;
import java.util.List;

/**
 * for metadata
 */
public interface PublicJWKProvider {

    /**
     * get list of jwk which could be got by metadata and jwks_uri
     *
     * @return list of public jwk
     */
    List<JWK> getPublicJWKs();
}
