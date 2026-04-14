package gov.moda.dw.manager.service;

import java.util.Optional;
import gov.moda.dw.manager.domain.LoginCount;
import gov.moda.dw.manager.repository.LoginCountRepository;
import gov.moda.dw.manager.service.dto.LoginCountDTO;
import gov.moda.dw.manager.service.mapper.LoginCountMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link LoginCount}.
 */
@Service
@Transactional
public class LoginCountService {

  private final Logger log = LoggerFactory.getLogger(LoginCountService.class);

  private final LoginCountRepository loginCountRepository;

  private final LoginCountMapper loginCountMapper;

  public LoginCountService(LoginCountRepository loginCountRepository, LoginCountMapper loginCountMapper) {
    this.loginCountRepository = loginCountRepository;
    this.loginCountMapper = loginCountMapper;
  }

  /**
   * Save a loginCount.
   *
   * @param loginCountDTO the entity to save.
   * @return the persisted entity.
   */
  public LoginCountDTO save(LoginCountDTO loginCountDTO) {
    log.debug("Request to save LoginCount : {}", loginCountDTO);
    LoginCount loginCount = loginCountMapper.toEntity(loginCountDTO);
    loginCount = loginCountRepository.save(loginCount);
    return loginCountMapper.toDto(loginCount);
  }

  /**
   * Update a loginCount.
   *
   * @param loginCountDTO the entity to save.
   * @return the persisted entity.
   */
  public LoginCountDTO update(LoginCountDTO loginCountDTO) {
    log.debug("Request to update LoginCount : {}", loginCountDTO);
    LoginCount loginCount = loginCountMapper.toEntity(loginCountDTO);
    loginCount = loginCountRepository.save(loginCount);
    return loginCountMapper.toDto(loginCount);
  }

  /**
   * Partially update a loginCount.
   *
   * @param loginCountDTO the entity to update partially.
   * @return the persisted entity.
   */
  public Optional<LoginCountDTO> partialUpdate(LoginCountDTO loginCountDTO) {
    log.debug("Request to partially update LoginCount : {}", loginCountDTO);

    return loginCountRepository
      .findById(loginCountDTO.getId())
      .map(existingLoginCount -> {
        loginCountMapper.partialUpdate(existingLoginCount, loginCountDTO);

        return existingLoginCount;
      })
      .map(loginCountRepository::save)
      .map(loginCountMapper::toDto);
  }

  /**
   * Get one loginCount by id.
   *
   * @param id the id of the entity.
   * @return the entity.
   */
  @Transactional(readOnly = true)
  public Optional<LoginCountDTO> findOne(Long id) {
    log.debug("Request to get LoginCount : {}", id);
    return loginCountRepository.findById(id).map(loginCountMapper::toDto);
  }

  /**
   * Delete the loginCount by id.
   *
   * @param id the id of the entity.
   */
  public void delete(Long id) {
    log.debug("Request to delete LoginCount : {}", id);
    loginCountRepository.deleteById(id);
  }
}
