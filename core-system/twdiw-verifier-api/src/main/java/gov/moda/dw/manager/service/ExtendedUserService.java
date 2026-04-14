package gov.moda.dw.manager.service;

import java.util.Optional;
import gov.moda.dw.manager.domain.ExtendedUser;
import gov.moda.dw.manager.repository.ExtendedUserRepository;
import gov.moda.dw.manager.service.dto.ExtendedUserDTO;
import gov.moda.dw.manager.service.mapper.ExtendedUserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ExtendedUser}.
 */
@Service
@Transactional
public class ExtendedUserService {

  private final Logger log = LoggerFactory.getLogger(ExtendedUserService.class);

  private final ExtendedUserRepository extendedUserRepository;

  private final ExtendedUserMapper extendedUserMapper;

  public ExtendedUserService(ExtendedUserRepository extendedUserRepository, ExtendedUserMapper extendedUserMapper) {
    this.extendedUserRepository = extendedUserRepository;
    this.extendedUserMapper = extendedUserMapper;
  }

  /**
   * Save a extendedUser.
   *
   * @param extendedUserDTO the entity to save.
   * @return the persisted entity.
   */
  public ExtendedUserDTO save(ExtendedUserDTO extendedUserDTO) {
    log.debug("Request to save ExtendedUser : {}", extendedUserDTO);
    ExtendedUser extendedUser = extendedUserMapper.toEntity(extendedUserDTO);
    extendedUser = extendedUserRepository.save(extendedUser);
    return extendedUserMapper.toDto(extendedUser);
  }

  /**
   * Update a extendedUser.
   *
   * @param extendedUserDTO the entity to save.
   * @return the persisted entity.
   */
  public ExtendedUserDTO update(ExtendedUserDTO extendedUserDTO) {
    log.debug("Request to update ExtendedUser : {}", extendedUserDTO);
    ExtendedUser extendedUser = extendedUserMapper.toEntity(extendedUserDTO);
    extendedUser = extendedUserRepository.save(extendedUser);
    return extendedUserMapper.toDto(extendedUser);
  }

  /**
   * Partially update a extendedUser.
   *
   * @param extendedUserDTO the entity to update partially.
   * @return the persisted entity.
   */
  public Optional<ExtendedUserDTO> partialUpdate(ExtendedUserDTO extendedUserDTO) {
    log.debug("Request to partially update ExtendedUser : {}", extendedUserDTO);

    return extendedUserRepository
      .findById(extendedUserDTO.getId())
      .map(existingExtendedUser -> {
        extendedUserMapper.partialUpdate(existingExtendedUser, extendedUserDTO);

        return existingExtendedUser;
      })
      .map(extendedUserRepository::save)
      .map(extendedUserMapper::toDto);
  }

  /**
   * Get one extendedUser by id.
   *
   * @param id the id of the entity.
   * @return the entity.
   */
  @Transactional(readOnly = true)
  public Optional<ExtendedUserDTO> findOne(Long id) {
    log.debug("Request to get ExtendedUser : {}", id);
    return extendedUserRepository.findById(id).map(extendedUserMapper::toDto);
  }

  /**
   * Delete the extendedUser by id.
   *
   * @param id the id of the entity.
   */
  public void delete(Long id) {
    log.debug("Request to delete ExtendedUser : {}", id);
    extendedUserRepository.deleteById(id);
  }
}
