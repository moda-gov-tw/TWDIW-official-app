package gov.moda.dw.manager.service;

import java.util.Optional;
import gov.moda.dw.manager.domain.BwdParam;
import gov.moda.dw.manager.repository.BwdParamRepository;
import gov.moda.dw.manager.service.dto.BwdParamDTO;
import gov.moda.dw.manager.service.mapper.BwdParamMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link BwdParam}.
 */
@Service
@Transactional
public class BwdParamService {

  private final Logger log = LoggerFactory.getLogger(BwdParamService.class);

  private final BwdParamRepository bwdParamRepository;

  private final BwdParamMapper bwdParamMapper;

  public BwdParamService(BwdParamRepository bwdParamRepository, BwdParamMapper bwdParamMapper) {
    this.bwdParamRepository = bwdParamRepository;
    this.bwdParamMapper = bwdParamMapper;
  }

  /**
   * Save a bwdParam.
   *
   * @param bwdParamDTO the entity to save.
   * @return the persisted entity.
   */
  public BwdParamDTO save(BwdParamDTO bwdParamDTO) {
    log.debug("Request to save BwdParam : {}", bwdParamDTO);
    BwdParam bwdParam = bwdParamMapper.toEntity(bwdParamDTO);
    bwdParam = bwdParamRepository.save(bwdParam);
    return bwdParamMapper.toDto(bwdParam);
  }

  /**
   * Update a bwdParam.
   *
   * @param bwdParamDTO the entity to save.
   * @return the persisted entity.
   */
  public BwdParamDTO update(BwdParamDTO bwdParamDTO) {
    log.debug("Request to update BwdParam : {}", bwdParamDTO);
    BwdParam bwdParam = bwdParamMapper.toEntity(bwdParamDTO);
    bwdParam = bwdParamRepository.save(bwdParam);
    return bwdParamMapper.toDto(bwdParam);
  }

  /**
   * Partially update a bwdParam.
   *
   * @param bwdParamDTO the entity to update partially.
   * @return the persisted entity.
   */
  public Optional<BwdParamDTO> partialUpdate(BwdParamDTO bwdParamDTO) {
    log.debug("Request to partially update BwdParam : {}", bwdParamDTO);

    return bwdParamRepository
      .findById(bwdParamDTO.getId())
      .map(existingBwdParam -> {
        bwdParamMapper.partialUpdate(existingBwdParam, bwdParamDTO);

        return existingBwdParam;
      })
      .map(bwdParamRepository::save)
      .map(bwdParamMapper::toDto);
  }

  /**
   * Get one bwdParam by id.
   *
   * @param id the id of the entity.
   * @return the entity.
   */
  @Transactional(readOnly = true)
  public Optional<BwdParamDTO> findOne(Long id) {
    log.debug("Request to get BwdParam : {}", id);
    return bwdParamRepository.findById(id).map(bwdParamMapper::toDto);
  }

  /**
   * Delete the bwdParam by id.
   *
   * @param id the id of the entity.
   */
  public void delete(Long id) {
    log.debug("Request to delete BwdParam : {}", id);
    bwdParamRepository.deleteById(id);
  }
}
