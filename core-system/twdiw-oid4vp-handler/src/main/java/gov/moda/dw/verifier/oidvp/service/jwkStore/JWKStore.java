package gov.moda.dw.verifier.oidvp.service.jwkStore;

import com.nimbusds.jose.jwk.JWK;
import gov.moda.dw.verifier.oidvp.model.errors.JWKStoreOperationException;

public interface JWKStore {

    /**
     * save jwk for private using in jwk store. (ex. request object singing key)
     *
     * @param keyId key id for which jwk saved in jwk store
     * @param jwk   jwk
     * @throws JWKStoreOperationException operation error
     */
    void saveJWK(String keyId, JWK jwk) throws JWKStoreOperationException;

    /**
     * get jwk by {@code keyId} which is store in jwk store for private using
     *
     * @param keyId key id for which jwk saved in jwk store
     * @return jwk
     * @throws JWKStoreOperationException operation error
     */
    JWK getJWK(String keyId) throws JWKStoreOperationException;

    /**
     * remove jwk from jwk store
     *
     * @param keyId The ID of the JWK to be removed
     * @throws JWKStoreOperationException operation error
     */
    void removeJWK(String keyId) throws JWKStoreOperationException;
}
