package gov.moda.dw.issuer.oidvci.service;

import java.util.Optional;
import gov.moda.dw.issuer.oidvci.domain.ApiTrack;
import gov.moda.dw.issuer.oidvci.repository.ApiTrackRepository;
import gov.moda.dw.issuer.oidvci.service.dto.ApiTrackDTO;
import gov.moda.dw.issuer.oidvci.service.mapper.ApiTrackMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ApiTrack}.
 */
@Service
@Transactional
public class ApiTrackService {

  private final Logger log = LoggerFactory.getLogger(ApiTrackService.class);

  private final ApiTrackRepository apiTrackRepository;

  private final ApiTrackMapper apiTrackMapper;

  public ApiTrackService(ApiTrackRepository apiTrackRepository, ApiTrackMapper apiTrackMapper) {
    this.apiTrackRepository = apiTrackRepository;
    this.apiTrackMapper = apiTrackMapper;
  }

  /**
   * Save a apiTrack.
   *
   * @param apiTrackDTO the entity to save.
   * @return the persisted entity.
   */
  public ApiTrackDTO save(ApiTrackDTO apiTrackDTO) {
    log.debug("Request to save ApiTrack : {}", apiTrackDTO);
    ApiTrack apiTrack = apiTrackMapper.toEntity(apiTrackDTO);
    apiTrack = apiTrackRepository.save(apiTrack);
    return apiTrackMapper.toDto(apiTrack);
  }

  /**
   * Update a apiTrack.
   *
   * @param apiTrackDTO the entity to save.
   * @return the persisted entity.
   */
  public ApiTrackDTO update(ApiTrackDTO apiTrackDTO) {
    log.debug("Request to update ApiTrack : {}", apiTrackDTO);
    ApiTrack apiTrack = apiTrackMapper.toEntity(apiTrackDTO);
    apiTrack = apiTrackRepository.save(apiTrack);
    return apiTrackMapper.toDto(apiTrack);
  }

  /**
   * Partially update a apiTrack.
   *
   * @param apiTrackDTO the entity to update partially.
   * @return the persisted entity.
   */
  public Optional<ApiTrackDTO> partialUpdate(ApiTrackDTO apiTrackDTO) {
    log.debug("Request to partially update ApiTrack : {}", apiTrackDTO);

    return apiTrackRepository
      .findById(apiTrackDTO.getId())
      .map(existingApiTrack -> {
        apiTrackMapper.partialUpdate(existingApiTrack, apiTrackDTO);

        return existingApiTrack;
      })
      .map(apiTrackRepository::save)
      .map(apiTrackMapper::toDto);
  }

  /**
   * Get one apiTrack by id.
   *
   * @param id the id of the entity.
   * @return the entity.
   */
  @Transactional(readOnly = true)
  public Optional<ApiTrackDTO> findOne(Long id) {
    log.debug("Request to get ApiTrack : {}", id);
    return apiTrackRepository.findById(id).map(apiTrackMapper::toDto);
  }

  /**
   * Delete the apiTrack by id.
   *
   * @param id the id of the entity.
   */
  public void delete(Long id) {
    log.debug("Request to delete ApiTrack : {}", id);
    apiTrackRepository.deleteById(id);
  }
}
