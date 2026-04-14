package gov.moda.dw.manager.service;

import gov.moda.dw.manager.domain.Org;
import gov.moda.dw.manager.repository.OrgRepository;
import gov.moda.dw.manager.service.dto.OrgDTO;
import gov.moda.dw.manager.service.mapper.OrgMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link gov.moda.dw.manager.domain.Org}.
 */
@Service
@Transactional
public class OrgService {
    private final Logger log = LoggerFactory.getLogger(OrgService.class);

    private final OrgRepository orgRepository;

    private final OrgMapper orgMapper;

    public OrgService(OrgRepository orgRepository, OrgMapper orgMapper) {
        this.orgRepository = orgRepository;
        this.orgMapper = orgMapper;
    }

    /**
     * Save a org.
     *
     * @param orgDTO the entity to save.
     * @return the persisted entity.
     */
    public OrgDTO save(OrgDTO orgDTO) {
        log.debug("Request to save Org : {}", orgDTO);
        Org org = orgMapper.toEntity(orgDTO);
        org = orgRepository.save(org);
        return orgMapper.toDto(org);
    }

    /**
     * Update a org.
     *
     * @param orgDTO the entity to save.
     * @return the persisted entity.
     */
    public OrgDTO update(OrgDTO orgDTO) {
        log.debug("Request to update Org : {}", orgDTO);
        Org org = orgMapper.toEntity(orgDTO);
        org = orgRepository.save(org);
        return orgMapper.toDto(org);
    }

    /**
     * Partially update a org.
     *
     * @param orgDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<OrgDTO> partialUpdate(OrgDTO orgDTO) {
        log.debug("Request to partially update Org : {}", orgDTO);

        return orgRepository
            .findById(orgDTO.getId())
            .map(existingOrg -> {
                orgMapper.partialUpdate(existingOrg, orgDTO);

                return existingOrg;
            })
            .map(orgRepository::save)
            .map(orgMapper::toDto);
    }

    /**
     * Get one org by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<OrgDTO> findOne(Long id) {
        log.debug("Request to get Org : {}", id);
        return orgRepository.findById(id).map(orgMapper::toDto);
    }

    /**
     * Delete the org by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Org : {}", id);
        orgRepository.deleteById(id);
    }
}
