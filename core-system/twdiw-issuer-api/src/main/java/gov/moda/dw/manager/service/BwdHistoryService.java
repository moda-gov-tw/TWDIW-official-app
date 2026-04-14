package gov.moda.dw.manager.service;

import java.util.Optional;
import gov.moda.dw.manager.domain.BwdHistory;
import gov.moda.dw.manager.repository.BwdHistoryRepository;
import gov.moda.dw.manager.service.dto.BwdHistoryDTO;
import gov.moda.dw.manager.service.mapper.BwdHistoryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link BwdHistory}.
 */
@Service
@Transactional
public class BwdHistoryService {

  private final Logger log = LoggerFactory.getLogger(BwdHistoryService.class);

  private final BwdHistoryRepository bwdHistoryRepository;

  private final BwdHistoryMapper bwdHistoryMapper;

  public BwdHistoryService(BwdHistoryRepository bwdHistoryRepository, BwdHistoryMapper bwdHistoryMapper) {
    this.bwdHistoryRepository = bwdHistoryRepository;
    this.bwdHistoryMapper = bwdHistoryMapper;
  }

  /**
   * Save a bwdHistory.
   *
   * @param bwdHistoryDTO the entity to save.
   * @return the persisted entity.
   */
  public BwdHistoryDTO save(BwdHistoryDTO bwdHistoryDTO) {
    BwdHistory bwdHistory = bwdHistoryMapper.toEntity(bwdHistoryDTO);
    bwdHistory = bwdHistoryRepository.save(bwdHistory);
    return bwdHistoryMapper.toDto(bwdHistory);
  }

  /**
   * Update a bwdHistory.
   *
   * @param bwdHistoryDTO the entity to save.
   * @return the persisted entity.
   */
  public BwdHistoryDTO update(BwdHistoryDTO bwdHistoryDTO) {
    log.debug("Request to update BwdHistory : {}", bwdHistoryDTO);
    BwdHistory bwdHistory = bwdHistoryMapper.toEntity(bwdHistoryDTO);
    bwdHistory = bwdHistoryRepository.save(bwdHistory);
    return bwdHistoryMapper.toDto(bwdHistory);
  }

  /**
   * Partially update a bwdHistory.
   *
   * @param bwdHistoryDTO the entity to update partially.
   * @return the persisted entity.
   */
  public Optional<BwdHistoryDTO> partialUpdate(BwdHistoryDTO bwdHistoryDTO) {
    log.debug("Request to partially update BwdHistory : {}", bwdHistoryDTO);

    return bwdHistoryRepository
      .findById(bwdHistoryDTO.getId())
      .map(existingBwdHistory -> {
        bwdHistoryMapper.partialUpdate(existingBwdHistory, bwdHistoryDTO);

        return existingBwdHistory;
      })
      .map(bwdHistoryRepository::save)
      .map(bwdHistoryMapper::toDto);
  }

  /**
   * Get one bwdHistory by id.
   *
   * @param id the id of the entity.
   * @return the entity.
   */
  @Transactional(readOnly = true)
  public Optional<BwdHistoryDTO> findOne(Long id) {
    log.debug("Request to get BwdHistory : {}", id);
    return bwdHistoryRepository.findById(id).map(bwdHistoryMapper::toDto);
  }

  /**
   * Delete the bwdHistory by id.
   *
   * @param id the id of the entity.
   */
  public void delete(Long id) {
    log.debug("Request to delete BwdHistory : {}", id);
    bwdHistoryRepository.deleteById(id);
  }
}
