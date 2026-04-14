package gov.moda.dw.manager.repository.custom;

import gov.moda.dw.manager.domain.OidvpConfig;
import gov.moda.dw.manager.repository.OidvpConfigRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomOidvpConfigRepository extends OidvpConfigRepository {
    Optional<OidvpConfig> findByPropertyKey(String propertyKey);

    void deleteByPropertyKey(String propertyKey);

    boolean existsByPropertyKey(String propertyKey);
}
