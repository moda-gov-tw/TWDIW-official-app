package gov.moda.dw.manager.service;

import java.util.Optional;
import gov.moda.dw.manager.domain.AccessToken;
import gov.moda.dw.manager.repository.AccessTokenRepository;
import gov.moda.dw.manager.service.dto.AccessTokenDTO;
import gov.moda.dw.manager.service.mapper.AccessTokenMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link AccessToken}.
 */
@Service
@Transactional
public class AccessTokenService {

  private final Logger log = LoggerFactory.getLogger(AccessTokenService.class);

  private final AccessTokenRepository accessTokenRepository;

  private final AccessTokenMapper accessTokenMapper;

  public AccessTokenService(AccessTokenRepository accessTokenRepository, AccessTokenMapper accessTokenMapper) {
    this.accessTokenRepository = accessTokenRepository;
    this.accessTokenMapper = accessTokenMapper;
  }

  /**
   * Save a accessToken.
   *
   * @param accessTokenDTO the entity to save.
   * @return the persisted entity.
   */
  public AccessTokenDTO save(AccessTokenDTO accessTokenDTO) {
    log.debug("Request to save AccessToken : {}", accessTokenDTO);
    AccessToken accessToken = accessTokenMapper.toEntity(accessTokenDTO);
    accessToken = accessTokenRepository.save(accessToken);
    return accessTokenMapper.toDto(accessToken);
  }

  /**
   * Update a accessToken.
   *
   * @param accessTokenDTO the entity to save.
   * @return the persisted entity.
   */
  public AccessTokenDTO update(AccessTokenDTO accessTokenDTO) {
    log.debug("Request to update AccessToken : {}", accessTokenDTO);
    AccessToken accessToken = accessTokenMapper.toEntity(accessTokenDTO);
    accessToken = accessTokenRepository.save(accessToken);
    return accessTokenMapper.toDto(accessToken);
  }

  /**
   * Partially update a accessToken.
   *
   * @param accessTokenDTO the entity to update partially.
   * @return the persisted entity.
   */
  public Optional<AccessTokenDTO> partialUpdate(AccessTokenDTO accessTokenDTO) {
    log.debug("Request to partially update AccessToken : {}", accessTokenDTO);

    return accessTokenRepository
      .findById(accessTokenDTO.getId())
      .map(existingAccessToken -> {
        accessTokenMapper.partialUpdate(existingAccessToken, accessTokenDTO);

        return existingAccessToken;
      })
      .map(accessTokenRepository::save)
      .map(accessTokenMapper::toDto);
  }

  /**
   * Get one accessToken by id.
   *
   * @param id the id of the entity.
   * @return the entity.
   */
  @Transactional(readOnly = true)
  public Optional<AccessTokenDTO> findOne(Long id) {
    log.debug("Request to get AccessToken : {}", id);
    return accessTokenRepository.findById(id).map(accessTokenMapper::toDto);
  }

  /**
   * Delete the accessToken by id.
   *
   * @param id the id of the entity.
   */
  public void delete(Long id) {
    log.debug("Request to delete AccessToken : {}", id);
    accessTokenRepository.deleteById(id);
  }
}
