package gov.moda.dw.manager.service;

import gov.moda.dw.manager.domain.VPVerifyResult;
import gov.moda.dw.manager.repository.VPVerifyResultRepository;
import gov.moda.dw.manager.service.dto.VPVerifyResultDTO;
import gov.moda.dw.manager.service.mapper.VPVerifyResultMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link gov.moda.dw.manager.domain.VPVerifyResult}.
 */
@Service
@Transactional
public class VPVerifyResultService {

    private static final Logger log = LoggerFactory.getLogger(VPVerifyResultService.class);

    private final VPVerifyResultRepository vPVerifyResultRepository;

    private final VPVerifyResultMapper vPVerifyResultMapper;

    public VPVerifyResultService(VPVerifyResultRepository vPVerifyResultRepository, VPVerifyResultMapper vPVerifyResultMapper) {
        this.vPVerifyResultRepository = vPVerifyResultRepository;
        this.vPVerifyResultMapper = vPVerifyResultMapper;
    }

    /**
     * Save a vPVerifyResult.
     *
     * @param vPVerifyResultDTO the entity to save.
     * @return the persisted entity.
     */
    public VPVerifyResultDTO save(VPVerifyResultDTO vPVerifyResultDTO) {
        log.debug("Request to save VPVerifyResult : {}", vPVerifyResultDTO);
        VPVerifyResult vPVerifyResult = vPVerifyResultMapper.toEntity(vPVerifyResultDTO);
        vPVerifyResult = vPVerifyResultRepository.save(vPVerifyResult);
        return vPVerifyResultMapper.toDto(vPVerifyResult);
    }

    /**
     * Update a vPVerifyResult.
     *
     * @param vPVerifyResultDTO the entity to save.
     * @return the persisted entity.
     */
    public VPVerifyResultDTO update(VPVerifyResultDTO vPVerifyResultDTO) {
        log.debug("Request to update VPVerifyResult : {}", vPVerifyResultDTO);
        VPVerifyResult vPVerifyResult = vPVerifyResultMapper.toEntity(vPVerifyResultDTO);
        vPVerifyResult = vPVerifyResultRepository.save(vPVerifyResult);
        return vPVerifyResultMapper.toDto(vPVerifyResult);
    }

    /**
     * Partially update a vPVerifyResult.
     *
     * @param vPVerifyResultDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<VPVerifyResultDTO> partialUpdate(VPVerifyResultDTO vPVerifyResultDTO) {
        log.debug("Request to partially update VPVerifyResult : {}", vPVerifyResultDTO);

        return vPVerifyResultRepository
            .findById(vPVerifyResultDTO.getId())
            .map(existingVPVerifyResult -> {
                vPVerifyResultMapper.partialUpdate(existingVPVerifyResult, vPVerifyResultDTO);

                return existingVPVerifyResult;
            })
            .map(vPVerifyResultRepository::save)
            .map(vPVerifyResultMapper::toDto);
    }

    /**
     * Get one vPVerifyResult by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<VPVerifyResultDTO> findOne(Long id) {
        log.debug("Request to get VPVerifyResult : {}", id);
        return vPVerifyResultRepository.findById(id).map(vPVerifyResultMapper::toDto);
    }

    /**
     * Delete the vPVerifyResult by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete VPVerifyResult : {}", id);
        vPVerifyResultRepository.deleteById(id);
    }
}
