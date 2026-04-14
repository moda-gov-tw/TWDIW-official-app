package gov.moda.dw.manager.service;

import java.util.Optional;
import gov.moda.dw.manager.domain.LoginView;
import gov.moda.dw.manager.repository.LoginViewRepository;
import gov.moda.dw.manager.service.dto.LoginViewDTO;
import gov.moda.dw.manager.service.mapper.LoginViewMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link LoginView}.
 */
@Service
@Transactional
public class LoginViewService {

  private final Logger log = LoggerFactory.getLogger(LoginViewService.class);

  private final LoginViewRepository loginViewRepository;

  private final LoginViewMapper loginViewMapper;

  public LoginViewService(LoginViewRepository loginViewRepository, LoginViewMapper loginViewMapper) {
    this.loginViewRepository = loginViewRepository;
    this.loginViewMapper = loginViewMapper;
  }

  /**
   * Save a loginView.
   *
   * @param loginViewDTO the entity to save.
   * @return the persisted entity.
   */
  public LoginViewDTO save(LoginViewDTO loginViewDTO) {
    log.debug("Request to save LoginView : {}", loginViewDTO);
    LoginView loginView = loginViewMapper.toEntity(loginViewDTO);
    loginView = loginViewRepository.save(loginView);
    return loginViewMapper.toDto(loginView);
  }

  /**
   * Update a loginView.
   *
   * @param loginViewDTO the entity to save.
   * @return the persisted entity.
   */
  public LoginViewDTO update(LoginViewDTO loginViewDTO) {
    log.debug("Request to update LoginView : {}", loginViewDTO);
    LoginView loginView = loginViewMapper.toEntity(loginViewDTO);
    loginView = loginViewRepository.save(loginView);
    return loginViewMapper.toDto(loginView);
  }

  /**
   * Partially update a loginView.
   *
   * @param loginViewDTO the entity to update partially.
   * @return the persisted entity.
   */
  public Optional<LoginViewDTO> partialUpdate(LoginViewDTO loginViewDTO) {
    log.debug("Request to partially update LoginView : {}", loginViewDTO);

    return loginViewRepository
      .findById(loginViewDTO.getId())
      .map(existingLoginView -> {
        loginViewMapper.partialUpdate(existingLoginView, loginViewDTO);

        return existingLoginView;
      })
      .map(loginViewRepository::save)
      .map(loginViewMapper::toDto);
  }

  /**
   * Get one loginView by id.
   *
   * @param id the id of the entity.
   * @return the entity.
   */
  @Transactional(readOnly = true)
  public Optional<LoginViewDTO> findOne(Long id) {
    log.debug("Request to get LoginView : {}", id);
    return loginViewRepository.findById(id).map(loginViewMapper::toDto);
  }

  /**
   * Delete the loginView by id.
   *
   * @param id the id of the entity.
   */
  public void delete(Long id) {
    log.debug("Request to delete LoginView : {}", id);
    loginViewRepository.deleteById(id);
  }
}
