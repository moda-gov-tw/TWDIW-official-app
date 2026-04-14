package gov.moda.dw.manager.service;

import java.util.Optional;
import gov.moda.dw.manager.domain.Role;
import gov.moda.dw.manager.repository.RoleRepository;
import gov.moda.dw.manager.service.dto.RoleDTO;
import gov.moda.dw.manager.service.mapper.RoleMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Role}.
 */
@Service
@Transactional
public class RoleService {

  private final Logger log = LoggerFactory.getLogger(RoleService.class);

  private final RoleRepository roleRepository;

  private final RoleMapper roleMapper;

  public RoleService(RoleRepository roleRepository, RoleMapper roleMapper) {
    this.roleRepository = roleRepository;
    this.roleMapper = roleMapper;
  }

  /**
   * Save a role.
   *
   * @param roleDTO the entity to save.
   * @return the persisted entity.
   */
  public RoleDTO save(RoleDTO roleDTO) {
    log.debug("Request to save Role : {}", roleDTO);
    Role role = roleMapper.toEntity(roleDTO);
    role = roleRepository.save(role);
    return roleMapper.toDto(role);
  }

  /**
   * Update a role.
   *
   * @param roleDTO the entity to save.
   * @return the persisted entity.
   */
  public RoleDTO update(RoleDTO roleDTO) {
    log.debug("Request to update Role : {}", roleDTO);
    Role role = roleMapper.toEntity(roleDTO);
    role = roleRepository.save(role);
    return roleMapper.toDto(role);
  }

  /**
   * Partially update a role.
   *
   * @param roleDTO the entity to update partially.
   * @return the persisted entity.
   */
  public Optional<RoleDTO> partialUpdate(RoleDTO roleDTO) {
    log.debug("Request to partially update Role : {}", roleDTO);

    return roleRepository
      .findById(roleDTO.getId())
      .map(existingRole -> {
        roleMapper.partialUpdate(existingRole, roleDTO);

        return existingRole;
      })
      .map(roleRepository::save)
      .map(roleMapper::toDto);
  }

  /**
   * Get one role by id.
   *
   * @param id the id of the entity.
   * @return the entity.
   */
  @Transactional(readOnly = true)
  public Optional<RoleDTO> findOne(Long id) {
    log.debug("Request to get Role : {}", id);
    return roleRepository.findById(id).map(roleMapper::toDto);
  }

  /**
   * Delete the role by id.
   *
   * @param id the id of the entity.
   */
  public void delete(Long id) {
    log.debug("Request to delete Role : {}", id);
    roleRepository.deleteById(id);
  }
}
