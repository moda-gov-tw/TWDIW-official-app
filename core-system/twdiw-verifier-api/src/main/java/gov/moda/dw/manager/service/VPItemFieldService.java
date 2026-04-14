package gov.moda.dw.manager.service;

import gov.moda.dw.manager.domain.VPItemField;
import gov.moda.dw.manager.repository.VPItemFieldRepository;
import gov.moda.dw.manager.service.dto.VPItemFieldDTO;
import gov.moda.dw.manager.service.mapper.VPItemFieldMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link gov.moda.dw.manager.domain.VPItemField}.
 */
@Service
@Transactional
public class VPItemFieldService {

    private static final Logger log = LoggerFactory.getLogger(VPItemFieldService.class);

    private final VPItemFieldRepository vPItemFieldRepository;

    private final VPItemFieldMapper vPItemFieldMapper;

    public VPItemFieldService(VPItemFieldRepository vPItemFieldRepository, VPItemFieldMapper vPItemFieldMapper) {
        this.vPItemFieldRepository = vPItemFieldRepository;
        this.vPItemFieldMapper = vPItemFieldMapper;
    }

    /**
     * Save a vPItemField.
     *
     * @param vPItemFieldDTO the entity to save.
     * @return the persisted entity.
     */
    public VPItemFieldDTO save(VPItemFieldDTO vPItemFieldDTO) {
        log.debug("Request to save VPItemField : {}", vPItemFieldDTO);
        VPItemField vPItemField = vPItemFieldMapper.toEntity(vPItemFieldDTO);
        vPItemField = vPItemFieldRepository.save(vPItemField);
        return vPItemFieldMapper.toDto(vPItemField);
    }

    /**
     * Update a vPItemField.
     *
     * @param vPItemFieldDTO the entity to save.
     * @return the persisted entity.
     */
    public VPItemFieldDTO update(VPItemFieldDTO vPItemFieldDTO) {
        log.debug("Request to update VPItemField : {}", vPItemFieldDTO);
        VPItemField vPItemField = vPItemFieldMapper.toEntity(vPItemFieldDTO);
        vPItemField = vPItemFieldRepository.save(vPItemField);
        return vPItemFieldMapper.toDto(vPItemField);
    }

    /**
     * Partially update a vPItemField.
     *
     * @param vPItemFieldDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<VPItemFieldDTO> partialUpdate(VPItemFieldDTO vPItemFieldDTO) {
        log.debug("Request to partially update VPItemField : {}", vPItemFieldDTO);

        return vPItemFieldRepository
            .findById(vPItemFieldDTO.getId())
            .map(existingVPItemField -> {
                vPItemFieldMapper.partialUpdate(existingVPItemField, vPItemFieldDTO);

                return existingVPItemField;
            })
            .map(vPItemFieldRepository::save)
            .map(vPItemFieldMapper::toDto);
    }

    /**
     * Get one vPItemField by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<VPItemFieldDTO> findOne(Long id) {
        log.debug("Request to get VPItemField : {}", id);
        return vPItemFieldRepository.findById(id).map(vPItemFieldMapper::toDto);
    }

    /**
     * Delete the vPItemField by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete VPItemField : {}", id);
        vPItemFieldRepository.deleteById(id);
    }
}
