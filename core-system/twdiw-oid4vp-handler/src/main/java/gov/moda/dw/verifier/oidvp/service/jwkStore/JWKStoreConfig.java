package gov.moda.dw.verifier.oidvp.service.jwkStore;

import gov.moda.dw.verifier.oidvp.dao.OidvpPropertyDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JWKStoreConfig {

    @Autowired
    OidvpPropertyDAO oidvpPropertyDAO;

    @Value("${DID_ENCRYPT_KEY:}")
    private String encryptKey;

    @Bean
    @ConditionalOnMissingBean(JWKStore.class)
    public JWKStore getDefaultJWKStore() {
        if (encryptKey == null || encryptKey.isEmpty()) {
            throw new IllegalArgumentException("'DID_ENCRYPT_KEY' must be specified");
        }
        if (oidvpPropertyDAO == null) {
            throw new IllegalArgumentException("oidvpPropertyDAO must not be null");
        }
        return new DefaultJWKStore(oidvpPropertyDAO, encryptKey);
    }
}
