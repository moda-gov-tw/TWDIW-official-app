package gov.moda.dw.manager.service;

import java.util.Optional;
import gov.moda.dw.manager.domain.ResLog;
import gov.moda.dw.manager.repository.ResLogRepository;
import gov.moda.dw.manager.service.dto.ResLogDTO;
import gov.moda.dw.manager.service.mapper.ResLogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ResLog}.
 */
@Service
@Transactional
public class ResLogService {

  private final Logger log = LoggerFactory.getLogger(ResLogService.class);

  private final ResLogRepository resLogRepository;

  private final ResLogMapper resLogMapper;

  public ResLogService(ResLogRepository resLogRepository, ResLogMapper resLogMapper) {
    this.resLogRepository = resLogRepository;
    this.resLogMapper = resLogMapper;
  }

  /**
   * Save a resLog.
   *
   * @param resLogDTO the entity to save.
   * @return the persisted entity.
   */
  public ResLogDTO save(ResLogDTO resLogDTO) {
    log.debug("Request to save ResLog : {}", resLogDTO);
    ResLog resLog = resLogMapper.toEntity(resLogDTO);
    resLog = resLogRepository.save(resLog);
    return resLogMapper.toDto(resLog);
  }

  /**
   * Update a resLog.
   *
   * @param resLogDTO the entity to save.
   * @return the persisted entity.
   */
  public ResLogDTO update(ResLogDTO resLogDTO) {
    log.debug("Request to update ResLog : {}", resLogDTO);
    ResLog resLog = resLogMapper.toEntity(resLogDTO);
    resLog = resLogRepository.save(resLog);
    return resLogMapper.toDto(resLog);
  }

  /**
   * Partially update a resLog.
   *
   * @param resLogDTO the entity to update partially.
   * @return the persisted entity.
   */
  public Optional<ResLogDTO> partialUpdate(ResLogDTO resLogDTO) {
    log.debug("Request to partially update ResLog : {}", resLogDTO);

    return resLogRepository
      .findById(resLogDTO.getId())
      .map(existingResLog -> {
        resLogMapper.partialUpdate(existingResLog, resLogDTO);

        return existingResLog;
      })
      .map(resLogRepository::save)
      .map(resLogMapper::toDto);
  }

  /**
   * Get one resLog by id.
   *
   * @param id the id of the entity.
   * @return the entity.
   */
  @Transactional(readOnly = true)
  public Optional<ResLogDTO> findOne(Long id) {
    log.debug("Request to get ResLog : {}", id);
    return resLogRepository.findById(id).map(resLogMapper::toDto);
  }

  /**
   * Delete the resLog by id.
   *
   * @param id the id of the entity.
   */
  public void delete(Long id) {
    log.debug("Request to delete ResLog : {}", id);
    resLogRepository.deleteById(id);
  }
}
