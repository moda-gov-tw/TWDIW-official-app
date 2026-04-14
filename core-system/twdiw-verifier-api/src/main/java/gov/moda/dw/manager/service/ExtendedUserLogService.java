package gov.moda.dw.manager.service;

import java.util.Optional;
import gov.moda.dw.manager.domain.ExtendedUserLog;
import gov.moda.dw.manager.repository.ExtendedUserLogRepository;
import gov.moda.dw.manager.service.dto.ExtendedUserLogDTO;
import gov.moda.dw.manager.service.mapper.ExtendedUserLogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ExtendedUserLog}.
 */
@Service
@Transactional
public class ExtendedUserLogService {

  private final Logger log = LoggerFactory.getLogger(ExtendedUserLogService.class);

  private final ExtendedUserLogRepository extendedUserLogRepository;

  private final ExtendedUserLogMapper extendedUserLogMapper;

  public ExtendedUserLogService(ExtendedUserLogRepository extendedUserLogRepository, ExtendedUserLogMapper extendedUserLogMapper) {
    this.extendedUserLogRepository = extendedUserLogRepository;
    this.extendedUserLogMapper = extendedUserLogMapper;
  }

  /**
   * Save a extendedUserLog.
   *
   * @param extendedUserLogDTO the entity to save.
   * @return the persisted entity.
   */
  public ExtendedUserLogDTO save(ExtendedUserLogDTO extendedUserLogDTO) {
    log.debug("Request to save ExtendedUserLog : {}", extendedUserLogDTO);
    ExtendedUserLog extendedUserLog = extendedUserLogMapper.toEntity(extendedUserLogDTO);
    extendedUserLog = extendedUserLogRepository.save(extendedUserLog);
    return extendedUserLogMapper.toDto(extendedUserLog);
  }

  /**
   * Update a extendedUserLog.
   *
   * @param extendedUserLogDTO the entity to save.
   * @return the persisted entity.
   */
  public ExtendedUserLogDTO update(ExtendedUserLogDTO extendedUserLogDTO) {
    log.debug("Request to update ExtendedUserLog : {}", extendedUserLogDTO);
    ExtendedUserLog extendedUserLog = extendedUserLogMapper.toEntity(extendedUserLogDTO);
    extendedUserLog = extendedUserLogRepository.save(extendedUserLog);
    return extendedUserLogMapper.toDto(extendedUserLog);
  }

  /**
   * Partially update a extendedUserLog.
   *
   * @param extendedUserLogDTO the entity to update partially.
   * @return the persisted entity.
   */
  public Optional<ExtendedUserLogDTO> partialUpdate(ExtendedUserLogDTO extendedUserLogDTO) {
    log.debug("Request to partially update ExtendedUserLog : {}", extendedUserLogDTO);

    return extendedUserLogRepository
      .findById(extendedUserLogDTO.getId())
      .map(existingExtendedUserLog -> {
        extendedUserLogMapper.partialUpdate(existingExtendedUserLog, extendedUserLogDTO);

        return existingExtendedUserLog;
      })
      .map(extendedUserLogRepository::save)
      .map(extendedUserLogMapper::toDto);
  }

  /**
   * Get one extendedUserLog by id.
   *
   * @param id the id of the entity.
   * @return the entity.
   */
  @Transactional(readOnly = true)
  public Optional<ExtendedUserLogDTO> findOne(Long id) {
    log.debug("Request to get ExtendedUserLog : {}", id);
    return extendedUserLogRepository.findById(id).map(extendedUserLogMapper::toDto);
  }

  /**
   * Delete the extendedUserLog by id.
   *
   * @param id the id of the entity.
   */
  public void delete(Long id) {
    log.debug("Request to delete ExtendedUserLog : {}", id);
    extendedUserLogRepository.deleteById(id);
  }
}
