package gov.moda.dw.manager.service;

import gov.moda.dw.manager.domain.VCItemField;
import gov.moda.dw.manager.repository.VCItemFieldRepository;
import gov.moda.dw.manager.repository.custom.CustomVCItemFieldRepository;
import gov.moda.dw.manager.service.dto.VCItemFieldDTO;
import gov.moda.dw.manager.service.mapper.VCItemFieldMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link gov.moda.dw.manager.domain.VCItemField}.
 */
@Service
@Transactional
public class VCItemFieldService {

    private static final Logger log = LoggerFactory.getLogger(VCItemFieldService.class);

    private final CustomVCItemFieldRepository vCItemFieldRepository;

    private final VCItemFieldMapper vCItemFieldMapper;

    public VCItemFieldService(CustomVCItemFieldRepository vCItemFieldRepository, VCItemFieldMapper vCItemFieldMapper) {
        this.vCItemFieldRepository = vCItemFieldRepository;
        this.vCItemFieldMapper = vCItemFieldMapper;
    }

    /**
     * Save a vCItemField.
     *
     * @param vCItemFieldDTO the entity to save.
     * @return the persisted entity.
     */
    public VCItemFieldDTO save(VCItemFieldDTO vCItemFieldDTO) {
        log.debug("Request to save VCItemField : {}", vCItemFieldDTO);
        VCItemField vCItemField = vCItemFieldMapper.toEntity(vCItemFieldDTO);
        vCItemField = vCItemFieldRepository.save(vCItemField);
        return vCItemFieldMapper.toDto(vCItemField);
    }

    /**
     * Update a vCItemField.
     *
     * @param vCItemFieldDTO the entity to save.
     * @return the persisted entity.
     */
    public VCItemFieldDTO update(VCItemFieldDTO vCItemFieldDTO) {
        log.debug("Request to update VCItemField : {}", vCItemFieldDTO);
        VCItemField vCItemField = vCItemFieldMapper.toEntity(vCItemFieldDTO);
        vCItemField = vCItemFieldRepository.save(vCItemField);
        return vCItemFieldMapper.toDto(vCItemField);
    }

    /**
     * Partially update a vCItemField.
     *
     * @param vCItemFieldDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<VCItemFieldDTO> partialUpdate(VCItemFieldDTO vCItemFieldDTO) {
        log.debug("Request to partially update VCItemField : {}", vCItemFieldDTO);

        return vCItemFieldRepository
            .findById(vCItemFieldDTO.getId())
            .map(existingVCItemField -> {
                vCItemFieldMapper.partialUpdate(existingVCItemField, vCItemFieldDTO);

                return existingVCItemField;
            })
            .map(vCItemFieldRepository::save)
            .map(vCItemFieldMapper::toDto);
    }

    /**
     * Get one vCItemField by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<VCItemFieldDTO> findOne(Long id) {
        log.debug("Request to get VCItemField : {}", id);
        return vCItemFieldRepository.findById(id).map(vCItemFieldMapper::toDto);
    }

    /**
     * Delete the vCItemField by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete VCItemField : {}", id);
        vCItemFieldRepository.deleteById(id);
    }
}
