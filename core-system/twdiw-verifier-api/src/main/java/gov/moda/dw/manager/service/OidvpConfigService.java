package gov.moda.dw.manager.service;

import gov.moda.dw.manager.domain.OidvpConfig;
import gov.moda.dw.manager.repository.custom.CustomOidvpConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link OidvpConfig}.
 */
@Service
@Transactional
public class OidvpConfigService {

    private static final Logger log = LoggerFactory.getLogger(OidvpConfigService.class);

    private final CustomOidvpConfigRepository oidvpConfigRepository;

    public OidvpConfigService(CustomOidvpConfigRepository oidvpConfigRepository) {
        this.oidvpConfigRepository = oidvpConfigRepository;
    }

    /**
     * Save a oidvpConfig.
     *
     * @param oidvpConfig the entity to save.
     * @return the persisted entity.
     */
    public OidvpConfig save(OidvpConfig oidvpConfig) {
        log.debug("Request to save OidvpConfig : {}", oidvpConfig);
        return oidvpConfigRepository.save(oidvpConfig);
    }

    /**
     * Update a OidvpConfig.
     *
     * @param oidvpConfig the entity to save.
     * @return the persisted entity.
     */
    public OidvpConfig update(OidvpConfig oidvpConfig) {
        log.debug("Request to update OidvpConfig : {}", oidvpConfig);
        return oidvpConfigRepository.save(oidvpConfig);
    }

    /**
     * Partially update a oidvpConfig.
     *
     * @param oidvpConfig the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<OidvpConfig> partialUpdate(OidvpConfig oidvpConfig) {
        log.debug("Request to partially update OidvpConfig : {}", oidvpConfig);

        return oidvpConfigRepository
            .findByPropertyKey(oidvpConfig.getPropertyKey())
            .map(existingOidvpConfig -> {
                if (oidvpConfig.getPropertyValue() != null) {
                    existingOidvpConfig.setPropertyValue(oidvpConfig.getPropertyValue());
                }

                return existingOidvpConfig;
            })
            .map(oidvpConfigRepository::save);
    }

    /**
     * Get one oidvpConfig by propertyKey.
     *
     * @param propertyKey the propertyKey of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<OidvpConfig> findOne(String propertyKey) {
        log.debug("Request to get OidvpConfig : {}", propertyKey);
        return oidvpConfigRepository.findByPropertyKey(propertyKey);
    }

    /**
     * Delete the oidvpConfig by propertyKey.
     *
     * @param propertyKey the id of the entity.
     */
    public void delete(String propertyKey) {
        log.debug("Request to delete OidvpConfig : {}", propertyKey);
        oidvpConfigRepository.deleteByPropertyKey(propertyKey);
    }
}
