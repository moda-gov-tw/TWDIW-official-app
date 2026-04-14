package gov.moda.dw.manager.service;

import java.util.Optional;
import gov.moda.dw.manager.domain.RoleLog;
import gov.moda.dw.manager.repository.RoleLogRepository;
import gov.moda.dw.manager.service.dto.RoleLogDTO;
import gov.moda.dw.manager.service.mapper.RoleLogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link RoleLog}.
 */
@Service
@Transactional
public class RoleLogService {

  private final Logger log = LoggerFactory.getLogger(RoleLogService.class);

  private final RoleLogRepository roleLogRepository;

  private final RoleLogMapper roleLogMapper;

  public RoleLogService(RoleLogRepository roleLogRepository, RoleLogMapper roleLogMapper) {
    this.roleLogRepository = roleLogRepository;
    this.roleLogMapper = roleLogMapper;
  }

  /**
   * Save a roleLog.
   *
   * @param roleLogDTO the entity to save.
   * @return the persisted entity.
   */
  public RoleLogDTO save(RoleLogDTO roleLogDTO) {
    log.debug("Request to save RoleLog : {}", roleLogDTO);
    RoleLog roleLog = roleLogMapper.toEntity(roleLogDTO);
    roleLog = roleLogRepository.save(roleLog);
    return roleLogMapper.toDto(roleLog);
  }

  /**
   * Update a roleLog.
   *
   * @param roleLogDTO the entity to save.
   * @return the persisted entity.
   */
  public RoleLogDTO update(RoleLogDTO roleLogDTO) {
    log.debug("Request to update RoleLog : {}", roleLogDTO);
    RoleLog roleLog = roleLogMapper.toEntity(roleLogDTO);
    roleLog = roleLogRepository.save(roleLog);
    return roleLogMapper.toDto(roleLog);
  }

  /**
   * Partially update a roleLog.
   *
   * @param roleLogDTO the entity to update partially.
   * @return the persisted entity.
   */
  public Optional<RoleLogDTO> partialUpdate(RoleLogDTO roleLogDTO) {
    log.debug("Request to partially update RoleLog : {}", roleLogDTO);

    return roleLogRepository
      .findById(roleLogDTO.getId())
      .map(existingRoleLog -> {
        roleLogMapper.partialUpdate(existingRoleLog, roleLogDTO);

        return existingRoleLog;
      })
      .map(roleLogRepository::save)
      .map(roleLogMapper::toDto);
  }

  /**
   * Get one roleLog by id.
   *
   * @param id the id of the entity.
   * @return the entity.
   */
  @Transactional(readOnly = true)
  public Optional<RoleLogDTO> findOne(Long id) {
    log.debug("Request to get RoleLog : {}", id);
    return roleLogRepository.findById(id).map(roleLogMapper::toDto);
  }

  /**
   * Delete the roleLog by id.
   *
   * @param id the id of the entity.
   */
  public void delete(Long id) {
    log.debug("Request to delete RoleLog : {}", id);
    roleLogRepository.deleteById(id);
  }
}
