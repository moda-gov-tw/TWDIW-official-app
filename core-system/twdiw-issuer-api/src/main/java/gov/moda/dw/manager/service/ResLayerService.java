package gov.moda.dw.manager.service;

import java.util.Optional;
import gov.moda.dw.manager.domain.ResLayer;
import gov.moda.dw.manager.repository.ResLayerRepository;
import gov.moda.dw.manager.service.dto.ResLayerDTO;
import gov.moda.dw.manager.service.mapper.ResLayerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ResLayer}.
 */
@Service
@Transactional
public class ResLayerService {

  private final Logger log = LoggerFactory.getLogger(ResLayerService.class);

  private final ResLayerRepository resLayerRepository;

  private final ResLayerMapper resLayerMapper;

  public ResLayerService(ResLayerRepository resLayerRepository, ResLayerMapper resLayerMapper) {
    this.resLayerRepository = resLayerRepository;
    this.resLayerMapper = resLayerMapper;
  }

  /**
   * Save a resLayer.
   *
   * @param resLayerDTO the entity to save.
   * @return the persisted entity.
   */
  public ResLayerDTO save(ResLayerDTO resLayerDTO) {
    log.debug("Request to save ResLayer : {}", resLayerDTO);
    ResLayer resLayer = resLayerMapper.toEntity(resLayerDTO);
    resLayer = resLayerRepository.save(resLayer);
    return resLayerMapper.toDto(resLayer);
  }

  /**
   * Update a resLayer.
   *
   * @param resLayerDTO the entity to save.
   * @return the persisted entity.
   */
  public ResLayerDTO update(ResLayerDTO resLayerDTO) {
    log.debug("Request to update ResLayer : {}", resLayerDTO);
    ResLayer resLayer = resLayerMapper.toEntity(resLayerDTO);
    resLayer = resLayerRepository.save(resLayer);
    return resLayerMapper.toDto(resLayer);
  }

  /**
   * Partially update a resLayer.
   *
   * @param resLayerDTO the entity to update partially.
   * @return the persisted entity.
   */
  public Optional<ResLayerDTO> partialUpdate(ResLayerDTO resLayerDTO) {
    log.debug("Request to partially update ResLayer : {}", resLayerDTO);

    return resLayerRepository
      .findById(resLayerDTO.getId())
      .map(existingResLayer -> {
        resLayerMapper.partialUpdate(existingResLayer, resLayerDTO);

        return existingResLayer;
      })
      .map(resLayerRepository::save)
      .map(resLayerMapper::toDto);
  }

  /**
   * Get one resLayer by id.
   *
   * @param id the id of the entity.
   * @return the entity.
   */
  @Transactional(readOnly = true)
  public Optional<ResLayerDTO> findOne(Long id) {
    log.debug("Request to get ResLayer : {}", id);
    return resLayerRepository.findById(id).map(resLayerMapper::toDto);
  }

  /**
   * Delete the resLayer by id.
   *
   * @param id the id of the entity.
   */
  public void delete(Long id) {
    log.debug("Request to delete ResLayer : {}", id);
    resLayerRepository.deleteById(id);
  }
}
