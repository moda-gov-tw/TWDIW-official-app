package gov.moda.dw.manager.service;

import java.util.Optional;
import gov.moda.dw.manager.domain.AuthObj;
import gov.moda.dw.manager.repository.AuthObjRepository;
import gov.moda.dw.manager.service.dto.AuthObjDTO;
import gov.moda.dw.manager.service.mapper.AuthObjMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link AuthObj}.
 */
@Service
@Transactional
public class AuthObjService {

  private final Logger log = LoggerFactory.getLogger(AuthObjService.class);

  private final AuthObjRepository authObjRepository;

  private final AuthObjMapper authObjMapper;

  public AuthObjService(AuthObjRepository authObjRepository, AuthObjMapper authObjMapper) {
    this.authObjRepository = authObjRepository;
    this.authObjMapper = authObjMapper;
  }

  /**
   * Save a authObj.
   *
   * @param authObjDTO the entity to save.
   * @return the persisted entity.
   */
  public AuthObjDTO save(AuthObjDTO authObjDTO) {
    log.debug("Request to save AuthObj : {}", authObjDTO);
    AuthObj authObj = authObjMapper.toEntity(authObjDTO);
    authObj = authObjRepository.save(authObj);
    return authObjMapper.toDto(authObj);
  }

  /**
   * Update a authObj.
   *
   * @param authObjDTO the entity to save.
   * @return the persisted entity.
   */
  public AuthObjDTO update(AuthObjDTO authObjDTO) {
    log.debug("Request to update AuthObj : {}", authObjDTO);
    AuthObj authObj = authObjMapper.toEntity(authObjDTO);
    authObj = authObjRepository.save(authObj);
    return authObjMapper.toDto(authObj);
  }

  /**
   * Partially update a authObj.
   *
   * @param authObjDTO the entity to update partially.
   * @return the persisted entity.
   */
  public Optional<AuthObjDTO> partialUpdate(AuthObjDTO authObjDTO) {
    log.debug("Request to partially update AuthObj : {}", authObjDTO);

    return authObjRepository
      .findById(authObjDTO.getId())
      .map(existingAuthObj -> {
        authObjMapper.partialUpdate(existingAuthObj, authObjDTO);

        return existingAuthObj;
      })
      .map(authObjRepository::save)
      .map(authObjMapper::toDto);
  }

  /**
   * Get one authObj by id.
   *
   * @param id the id of the entity.
   * @return the entity.
   */
  @Transactional(readOnly = true)
  public Optional<AuthObjDTO> findOne(String id) {
    log.debug("Request to get AuthObj : {}", id);
    return authObjRepository.findById(id).map(authObjMapper::toDto);
  }

  /**
   * Delete the authObj by id.
   *
   * @param id the id of the entity.
   */
  public void delete(String id) {
    log.debug("Request to delete AuthObj : {}", id);
    authObjRepository.deleteById(id);
  }
}
