package gov.moda.dw.manager.service;

import gov.moda.dw.manager.domain.Setting;

import java.util.Optional;

import gov.moda.dw.manager.repository.custom.CustomSettingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link gov.moda.dw.manager.domain.Setting}.
 */
@Service
@Transactional
public class SettingService {

    private static final Logger log = LoggerFactory.getLogger(SettingService.class);

    private final CustomSettingRepository settingRepository;

    public SettingService(CustomSettingRepository settingRepository) {
        this.settingRepository = settingRepository;
    }

    /**
     * Save a setting.
     *
     * @param setting the entity to save.
     * @return the persisted entity.
     */
    public Setting save(Setting setting) {
        log.debug("Request to save Setting : {}", setting);
        return settingRepository.save(setting);
    }

    /**
     * Update a setting.
     *
     * @param setting the entity to save.
     * @return the persisted entity.
     */
    public Setting update(Setting setting) {
        log.debug("Request to update Setting : {}", setting);
        return settingRepository.save(setting);
    }

    /**
     * Partially update a setting.
     *
     * @param setting the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Setting> partialUpdate(Setting setting) {
        log.debug("Request to partially update Setting : {}", setting);

        return settingRepository
            .findByPropName(setting.getPropName())
            .map(existingSetting -> {
                if (setting.getPropValue() != null) {
                    existingSetting.setPropValue(setting.getPropValue());
                }

                return existingSetting;
            })
            .map(settingRepository::save);
    }

    /**
     * Get one setting by propName.
     *
     * @param propName the propName of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Setting> findOne(String propName) {
        log.debug("Request to get Setting : {}", propName);
        return settingRepository.findByPropName(propName);
    }

    /**
     * Delete the setting by propName.
     *
     * @param propName the id of the entity.
     */
    public void delete(String propName) {
        log.debug("Request to delete Setting : {}", propName);
        settingRepository.deleteByPropName(propName);
    }
}
