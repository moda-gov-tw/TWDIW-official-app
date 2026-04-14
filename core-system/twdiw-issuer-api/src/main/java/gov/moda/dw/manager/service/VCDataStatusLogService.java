package gov.moda.dw.manager.service;

import gov.moda.dw.manager.domain.VCDataStatusLog;
import gov.moda.dw.manager.repository.VCDataStatusLogRepository;
import gov.moda.dw.manager.service.dto.VCDataStatusLogDTO;
import gov.moda.dw.manager.service.mapper.VCDataStatusLogMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing
 * {@link gov.moda.dw.manager.domain.VCDataStatusLog}.
 */
@Service
@Transactional
public class VCDataStatusLogService {

    private static final Logger LOG = LoggerFactory.getLogger(VCDataStatusLogService.class);

    private final VCDataStatusLogRepository vCDataStatusLogRepository;

    private final VCDataStatusLogMapper vCDataStatusLogMapper;

    public VCDataStatusLogService(VCDataStatusLogRepository vCDataStatusLogRepository,
            VCDataStatusLogMapper vCDataStatusLogMapper) {
        this.vCDataStatusLogRepository = vCDataStatusLogRepository;
        this.vCDataStatusLogMapper = vCDataStatusLogMapper;
    }

    /**
     * Save a vCDataStatusLog.
     *
     * @param vCDataStatusLogDTO the entity to save.
     * @return the persisted entity.
     */
    public VCDataStatusLogDTO save(VCDataStatusLogDTO vCDataStatusLogDTO) {
        LOG.debug("Request to save VCDataStatusLog : {}", vCDataStatusLogDTO);
        VCDataStatusLog vCDataStatusLog = vCDataStatusLogMapper.toEntity(vCDataStatusLogDTO);
        vCDataStatusLog = vCDataStatusLogRepository.save(vCDataStatusLog);
        return vCDataStatusLogMapper.toDto(vCDataStatusLog);
    }

    /**
     * Update a vCDataStatusLog.
     *
     * @param vCDataStatusLogDTO the entity to save.
     * @return the persisted entity.
     */
    public VCDataStatusLogDTO update(VCDataStatusLogDTO vCDataStatusLogDTO) {
        LOG.debug("Request to update VCDataStatusLog : {}", vCDataStatusLogDTO);
        VCDataStatusLog vCDataStatusLog = vCDataStatusLogMapper.toEntity(vCDataStatusLogDTO);
        vCDataStatusLog = vCDataStatusLogRepository.save(vCDataStatusLog);
        return vCDataStatusLogMapper.toDto(vCDataStatusLog);
    }

    /**
     * Partially update a vCDataStatusLog.
     *
     * @param vCDataStatusLogDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<VCDataStatusLogDTO> partialUpdate(VCDataStatusLogDTO vCDataStatusLogDTO) {
        LOG.debug("Request to partially update VCDataStatusLog : {}", vCDataStatusLogDTO);

        return vCDataStatusLogRepository.findById(vCDataStatusLogDTO.getId()).map(existingVCDataStatusLog -> {
            vCDataStatusLogMapper.partialUpdate(existingVCDataStatusLog, vCDataStatusLogDTO);

            return existingVCDataStatusLog;
        }).map(vCDataStatusLogRepository::save).map(vCDataStatusLogMapper::toDto);
    }

    /**
     * Get one vCDataStatusLog by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<VCDataStatusLogDTO> findOne(Long id) {
        LOG.debug("Request to get VCDataStatusLog : {}", id);
        return vCDataStatusLogRepository.findById(id).map(vCDataStatusLogMapper::toDto);
    }

    /**
     * Delete the vCDataStatusLog by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete VCDataStatusLog : {}", id);
        vCDataStatusLogRepository.deleteById(id);
    }
}
