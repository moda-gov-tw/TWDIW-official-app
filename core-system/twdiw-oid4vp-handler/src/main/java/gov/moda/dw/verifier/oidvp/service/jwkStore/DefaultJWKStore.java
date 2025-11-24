package gov.moda.dw.verifier.oidvp.service.jwkStore;

import com.nimbusds.jose.jwk.JWK;
import gov.moda.dw.verifier.oidvp.dao.OidvpPropertyDAO;
import gov.moda.dw.verifier.oidvp.domain.OidvpPropertyJpa;
import gov.moda.dw.verifier.oidvp.model.errors.BadOidvpParamException;
import gov.moda.dw.verifier.oidvp.model.errors.JWKStoreOperationException;
import gov.moda.dw.verifier.oidvp.util.AESUtils;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Base64;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * store encrypted jwk in oidvp database
 */
public class DefaultJWKStore implements JWKStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultJWKStore.class);

    public static final String JWK_PREDICATE = "jwk.private.";

    private final OidvpPropertyDAO oidvpPropertyDAO;
    private final String encryptKey;

    public DefaultJWKStore(OidvpPropertyDAO oidvpPropertyDAO, String encryptKey) {
        this.oidvpPropertyDAO = Objects.requireNonNull(oidvpPropertyDAO);
        if (encryptKey == null) {
            throw new IllegalArgumentException("encryptKey must not be null.");
        } else if (encryptKey.length() != 32) {
            throw new IllegalArgumentException("invalid encryptKey length, encryptKey's length must be 32.");
        } else {
            this.encryptKey = encryptKey;
        }
        initCheck();
    }

    @Override
    public void saveJWK(String keyId, JWK jwk) throws JWKStoreOperationException {
        if (keyId == null) {
            throw new BadOidvpParamException("save jwk error: keyId must not be null");
        }
        if (jwk == null) {
            throw new BadOidvpParamException("save jwk error: jwk must not be null");
        }
        try {
            String encryptedJWK = encryptJWK(jwk);
            oidvpPropertyDAO.saveProperty(getPropertyKey(keyId), encryptedJWK);
        } catch (SQLException e) {
            throw new JWKStoreOperationException("save jwk error:" + e.getMessage(), e);
        }
    }

    @Override
    public JWK getJWK(String keyId) throws JWKStoreOperationException, NoSuchElementException {
        if (keyId == null) {
            throw new BadOidvpParamException("get jwk error: keyID must not be null");
        }
        try {
            OidvpPropertyJpa propertyJpa = oidvpPropertyDAO.getPropertyByKey(getPropertyKey(keyId));
            String decryptedJWK = decryptJWK(propertyJpa.getValue());
            return JWK.parse(decryptedJWK);
        } catch (SQLException | ParseException e) {
            throw new JWKStoreOperationException("get jwk error:" + e.getMessage(), e);
        }
    }

    @Override
    public void removeJWK(String keyId) throws JWKStoreOperationException {
        if (keyId == null) {
            throw new BadOidvpParamException("remove jwk error: keyID must not be null");
        }
        try {
            oidvpPropertyDAO.deletePropertyByKey(getPropertyKey(keyId));
        } catch (SQLException e) {
            throw new JWKStoreOperationException("remove jwk error:" + e.getMessage(), e);
        }
    }

    private String getPropertyKey(String keyId) {
        return JWK_PREDICATE.concat(keyId);
    }

    private String encryptJWK(JWK jwk) throws JWKStoreOperationException {
        try {
            byte[] encrypted = AESUtils.encryptGCM(encryptKey.getBytes(StandardCharsets.UTF_8), jwk.toJSONString(), null);
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new JWKStoreOperationException("encrypt jwk key error, " + e.getMessage(), e);
        }
    }

    private String decryptJWK(String encryptedBase64String) throws JWKStoreOperationException {
        try {
            byte[] decoded = Base64.getDecoder().decode(encryptedBase64String);
            return AESUtils.decryptGCM(encryptKey.getBytes(StandardCharsets.UTF_8), decoded, null);
        } catch (Exception e) {
            throw new JWKStoreOperationException("decrypt jwk key error, " + e.getMessage(), e);
        }
    }

    private void initCheck() {
        List<OidvpPropertyJpa> properties;
        try {
            properties = oidvpPropertyDAO.getProperties();
        } catch (SQLException e) {
            throw new IllegalStateException("check existed jwk error: query db error, " + e.getMessage(), e);
        }

        for (OidvpPropertyJpa property : properties) {
            if (property.getKey().startsWith(JWK_PREDICATE)) {
                String jwkValue = property.getValue();
                if (jwkValue.startsWith("{") && jwkValue.contains("\"kty\":")) {
                    try {
                        saveJWK(property.getKey().substring(JWK_PREDICATE.length()), JWK.parse(jwkValue));
                    } catch (Exception e) {
                        throw new IllegalStateException("check existed jwk error: encrypted existed raw jwk error, " + e.getMessage(), e);
                    }
                } else {
                    try {
                        String decryptedJWK = decryptJWK(jwkValue);
                        JWK.parse(decryptedJWK);
                    } catch (Exception e) {
                        throw new IllegalStateException("check existed jwk error: parse existed encrypted jwk error, " + e.getMessage(), e);
                    }
                }
            }
        }
        LOGGER.info("DefaultJWKStore: check existed jwk...ok");
    }
}
