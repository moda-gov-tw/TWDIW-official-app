package gov.moda.dw.verifier.oidvp.service.did;

import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import gov.moda.dw.verifier.oidvp.common.OidvpErrorCode.OidvpError;
import gov.moda.dw.verifier.oidvp.config.CacheManagerConfig;
import gov.moda.dw.verifier.oidvp.dao.OidvpPropertyDAO;
import gov.moda.dw.verifier.oidvp.model.errors.JWKStoreOperationException;
import gov.moda.dw.verifier.oidvp.model.errors.OidvpException;
import gov.moda.dw.verifier.oidvp.model.errors.OidvpRuntimeException;
import gov.moda.dw.verifier.oidvp.service.jwkStore.JWKStore;
import gov.moda.dw.verifier.oidvp.util.DIDUtils;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CacheConfig(cacheManager = CacheManagerConfig.DID_CACHE_MANAGER, cacheNames = CacheManagerConfig.DID_CACHE_NAME)
public class DIDEntity {

    private static final Logger LOGGER = LoggerFactory.getLogger(DIDEntity.class);
    public static final String DID_CACHE_KEY_VALUE = "#root.caches[0].name+'_did'";
    public static final String DID_KEY_CACHE_KEY_VALUE = "#root.caches[0].name+'_didKey'";
    public static final String VERIFIER_DID_PREDICATE = "verifier.did";
    public static final String VERIFIER_DID_KEY_ALIAS = "verifier.did.key";

    private final OidvpPropertyDAO oidvpPropertyDAO;
    private final JWKStore jwkStore;

    public DIDEntity(JWKStore jwkStore, OidvpPropertyDAO oidvpPropertyDAO) throws SQLException, OidvpException {
        this.jwkStore = jwkStore;
        this.oidvpPropertyDAO = oidvpPropertyDAO;
        ECKey didKey = null;
        String did = null;
        try {
            didKey = getDIDKey();
        } catch (NoSuchElementException ignored) {
        }
        try {
            did = getDID();
        } catch (NoSuchElementException ignored) {
        }

        if (did != null && didKey != null) {
            if (!isDIDMatched(did, didKey)) {
                throw new IllegalStateException("existed did is not matched to the registered did key.");
            }
        } else if (did == null && didKey != null) {
            LOGGER.error("WARNING - The pair of did and did key is incomplete, missing did.");
//            throw new IllegalStateException("the pair of did and did key is incomplete, missing did.");
        } else if (did != null) {
            LOGGER.error("WARNING - The pair of did and did key is incomplete, missing did key.");
//            throw new IllegalStateException("the pair of did and did key is incomplete, missing did key.");
        }
    }


    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {@CacheEvict(key = DID_CACHE_KEY_VALUE), @CacheEvict(key = DID_KEY_CACHE_KEY_VALUE)})
    public void saveDidAndDidKey(String did, ECKey didKey) throws SQLException, JWKStoreOperationException {
        saveDID(did);
        saveDIDKey(didKey);
    }

    @Cacheable(key = DID_CACHE_KEY_VALUE)
    public String getDID() throws NoSuchElementException, SQLException {
        return oidvpPropertyDAO.getPropertyByKey(VERIFIER_DID_PREDICATE).getValue();
    }

    @Cacheable(key = DID_KEY_CACHE_KEY_VALUE)
    public ECKey getDIDKey() throws JWKStoreOperationException, NoSuchElementException {
        JWK jwk = jwkStore.getJWK(VERIFIER_DID_KEY_ALIAS);
        if (jwk == null) {
            throw new NoSuchElementException("DID key is not exist");
        }
        if (jwk instanceof ECKey didKey) {
            return didKey;
        } else {
            throw new OidvpRuntimeException(OidvpError.GET_DID_ERROR, "did key from jwk store is not ECKey.");
        }
    }

    private void saveDID(String did) throws SQLException {
        oidvpPropertyDAO.saveProperty(VERIFIER_DID_PREDICATE, did);
    }

    private void saveDIDKey(ECKey didKey) throws JWKStoreOperationException {
        jwkStore.saveJWK(VERIFIER_DID_KEY_ALIAS, didKey);
    }

    private boolean isDIDMatched(String did, ECKey didKey) throws OidvpException {
        JWK jwk = DIDUtils.extractPublicKeyFromDID(did);
        return jwk.getRequiredParams().equals(didKey.getRequiredParams());
    }
}
