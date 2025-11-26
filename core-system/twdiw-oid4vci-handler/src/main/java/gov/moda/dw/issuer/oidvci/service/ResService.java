package gov.moda.dw.issuer.oidvci.service;

import java.util.Optional;
import gov.moda.dw.issuer.oidvci.domain.Res;
import gov.moda.dw.issuer.oidvci.repository.ResRepository;
import gov.moda.dw.issuer.oidvci.service.dto.ResDTO;
import gov.moda.dw.issuer.oidvci.service.mapper.ResMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Res}.
 */
@Service
@Transactional
public class ResService {

  private final Logger log = LoggerFactory.getLogger(ResService.class);

  private final ResRepository resRepository;

  private final ResMapper resMapper;

  public ResService(ResRepository resRepository, ResMapper resMapper) {
    this.resRepository = resRepository;
    this.resMapper = resMapper;
  }

  /**
   * Save a res.
   *
   * @param resDTO the entity to save.
   * @return the persisted entity.
   */
  public ResDTO save(ResDTO resDTO) {
    log.debug("Request to save Res : {}", resDTO);
    Res res = resMapper.toEntity(resDTO);
    res = resRepository.save(res);
    return resMapper.toDto(res);
  }

  /**
   * Update a res.
   *
   * @param resDTO the entity to save.
   * @return the persisted entity.
   */
  public ResDTO update(ResDTO resDTO) {
    log.debug("Request to update Res : {}", resDTO);
    Res res = resMapper.toEntity(resDTO);
    res = resRepository.save(res);
    return resMapper.toDto(res);
  }

  /**
   * Partially update a res.
   *
   * @param resDTO the entity to update partially.
   * @return the persisted entity.
   */
  public Optional<ResDTO> partialUpdate(ResDTO resDTO) {
    log.debug("Request to partially update Res : {}", resDTO);

    return resRepository
      .findById(resDTO.getId())
      .map(existingRes -> {
        resMapper.partialUpdate(existingRes, resDTO);

        return existingRes;
      })
      .map(resRepository::save)
      .map(resMapper::toDto);
  }

  /**
   * Get one res by id.
   *
   * @param id the id of the entity.
   * @return the entity.
   */
  @Transactional(readOnly = true)
  public Optional<ResDTO> findOne(Long id) {
    log.debug("Request to get Res : {}", id);
    return resRepository.findById(id).map(resMapper::toDto);
  }

  /**
   * Delete the res by id.
   *
   * @param id the id of the entity.
   */
  public void delete(Long id) {
    log.debug("Request to delete Res : {}", id);
    resRepository.deleteById(id);
  }
}
