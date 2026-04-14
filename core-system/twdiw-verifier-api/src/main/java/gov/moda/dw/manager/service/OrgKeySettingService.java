package gov.moda.dw.manager.service;

import gov.moda.dw.manager.domain.OrgKeySetting;
import gov.moda.dw.manager.repository.OrgKeySettingRepository;
import gov.moda.dw.manager.service.dto.OrgKeySettingDTO;
import gov.moda.dw.manager.service.mapper.OrgKeySettingMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link gov.moda.dw.manager.domain.OrgKeySetting}.
 */
@Service
@Transactional
public class OrgKeySettingService {

    private static final Logger LOG = LoggerFactory.getLogger(OrgKeySettingService.class);

    private final OrgKeySettingRepository orgKeySettingRepository;

    private final OrgKeySettingMapper orgKeySettingMapper;

    public OrgKeySettingService(OrgKeySettingRepository orgKeySettingRepository, OrgKeySettingMapper orgKeySettingMapper) {
        this.orgKeySettingRepository = orgKeySettingRepository;
        this.orgKeySettingMapper = orgKeySettingMapper;
    }

    /**
     * Save a orgKeySetting.
     *
     * @param orgKeySettingDTO the entity to save.
     * @return the persisted entity.
     */
    public OrgKeySettingDTO save(OrgKeySettingDTO orgKeySettingDTO) {
        LOG.debug("Request to save OrgKeySetting : {}", orgKeySettingDTO);
        OrgKeySetting orgKeySetting = orgKeySettingMapper.toEntity(orgKeySettingDTO);
        orgKeySetting = orgKeySettingRepository.save(orgKeySetting);
        return orgKeySettingMapper.toDto(orgKeySetting);
    }

    /**
     * Update a orgKeySetting.
     *
     * @param orgKeySettingDTO the entity to save.
     * @return the persisted entity.
     */
    public OrgKeySettingDTO update(OrgKeySettingDTO orgKeySettingDTO) {
        LOG.debug("Request to update OrgKeySetting : {}", orgKeySettingDTO);
        OrgKeySetting orgKeySetting = orgKeySettingMapper.toEntity(orgKeySettingDTO);
        orgKeySetting = orgKeySettingRepository.save(orgKeySetting);
        return orgKeySettingMapper.toDto(orgKeySetting);
    }

    /**
     * Partially update a orgKeySetting.
     *
     * @param orgKeySettingDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<OrgKeySettingDTO> partialUpdate(OrgKeySettingDTO orgKeySettingDTO) {
        LOG.debug("Request to partially update OrgKeySetting : {}", orgKeySettingDTO);

        return orgKeySettingRepository
            .findById(orgKeySettingDTO.getId())
            .map(existingOrgKeySetting -> {
                orgKeySettingMapper.partialUpdate(existingOrgKeySetting, orgKeySettingDTO);

                return existingOrgKeySetting;
            })
            .map(orgKeySettingRepository::save)
            .map(orgKeySettingMapper::toDto);
    }

    /**
     * Get one orgKeySetting by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<OrgKeySettingDTO> findOne(Long id) {
        LOG.debug("Request to get OrgKeySetting : {}", id);
        return orgKeySettingRepository.findById(id).map(orgKeySettingMapper::toDto);
    }

    /**
     * Delete the orgKeySetting by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete OrgKeySetting : {}", id);
        orgKeySettingRepository.deleteById(id);
    }
}
