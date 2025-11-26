package gov.moda.dw.issuer.oidvci.service;

import java.util.Optional;
import gov.moda.dw.issuer.oidvci.domain.Rel;
import gov.moda.dw.issuer.oidvci.repository.RelRepository;
import gov.moda.dw.issuer.oidvci.service.dto.RelDTO;
import gov.moda.dw.issuer.oidvci.service.mapper.RelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Rel}.
 */
@Service
@Transactional
public class RelService {

  private final Logger log = LoggerFactory.getLogger(RelService.class);

  private final RelRepository relRepository;

  private final RelMapper relMapper;

  public RelService(RelRepository relRepository, RelMapper relMapper) {
    this.relRepository = relRepository;
    this.relMapper = relMapper;
  }

  /**
   * Save a rel.
   *
   * @param relDTO the entity to save.
   * @return the persisted entity.
   */
  public RelDTO save(RelDTO relDTO) {
    log.debug("Request to save Rel : {}", relDTO);
    Rel rel = relMapper.toEntity(relDTO);
    rel = relRepository.save(rel);
    return relMapper.toDto(rel);
  }

  /**
   * Update a rel.
   *
   * @param relDTO the entity to save.
   * @return the persisted entity.
   */
  public RelDTO update(RelDTO relDTO) {
    log.debug("Request to update Rel : {}", relDTO);
    Rel rel = relMapper.toEntity(relDTO);
    rel = relRepository.save(rel);
    return relMapper.toDto(rel);
  }

  /**
   * Partially update a rel.
   *
   * @param relDTO the entity to update partially.
   * @return the persisted entity.
   */
  public Optional<RelDTO> partialUpdate(RelDTO relDTO) {
    log.debug("Request to partially update Rel : {}", relDTO);

    return relRepository
      .findById(relDTO.getId())
      .map(existingRel -> {
        relMapper.partialUpdate(existingRel, relDTO);

        return existingRel;
      })
      .map(relRepository::save)
      .map(relMapper::toDto);
  }

  /**
   * Get one rel by id.
   *
   * @param id the id of the entity.
   * @return the entity.
   */
  @Transactional(readOnly = true)
  public Optional<RelDTO> findOne(Long id) {
    log.debug("Request to get Rel : {}", id);
    return relRepository.findById(id).map(relMapper::toDto);
  }

  /**
   * Delete the rel by id.
   *
   * @param id the id of the entity.
   */
  public void delete(Long id) {
    log.debug("Request to delete Rel : {}", id);
    relRepository.deleteById(id);
  }
}
