package gov.moda.dw.manager.service;

import java.util.Optional;
import gov.moda.dw.manager.domain.Nonce;
import gov.moda.dw.manager.repository.NonceRepository;
import gov.moda.dw.manager.service.dto.NonceDTO;
import gov.moda.dw.manager.service.mapper.NonceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Nonce}.
 */
@Service
@Transactional
public class NonceService {

  private final Logger log = LoggerFactory.getLogger(NonceService.class);

  private final NonceRepository nonceRepository;

  private final NonceMapper nonceMapper;

  public NonceService(NonceRepository nonceRepository, NonceMapper nonceMapper) {
    this.nonceRepository = nonceRepository;
    this.nonceMapper = nonceMapper;
  }

  /**
   * Save a nonce.
   *
   * @param nonceDTO the entity to save.
   * @return the persisted entity.
   */
  public NonceDTO save(NonceDTO nonceDTO) {
    log.debug("Request to save Nonce : {}", nonceDTO);
    Nonce nonce = nonceMapper.toEntity(nonceDTO);
    nonce = nonceRepository.save(nonce);
    return nonceMapper.toDto(nonce);
  }

  /**
   * Update a nonce.
   *
   * @param nonceDTO the entity to save.
   * @return the persisted entity.
   */
  public NonceDTO update(NonceDTO nonceDTO) {
    log.debug("Request to update Nonce : {}", nonceDTO);
    Nonce nonce = nonceMapper.toEntity(nonceDTO);
    nonce = nonceRepository.save(nonce);
    return nonceMapper.toDto(nonce);
  }

  /**
   * Partially update a nonce.
   *
   * @param nonceDTO the entity to update partially.
   * @return the persisted entity.
   */
  public Optional<NonceDTO> partialUpdate(NonceDTO nonceDTO) {
    log.debug("Request to partially update Nonce : {}", nonceDTO);

    return nonceRepository
      .findById(nonceDTO.getId())
      .map(existingNonce -> {
        nonceMapper.partialUpdate(existingNonce, nonceDTO);

        return existingNonce;
      })
      .map(nonceRepository::save)
      .map(nonceMapper::toDto);
  }

  /**
   * Get one nonce by id.
   *
   * @param id the id of the entity.
   * @return the entity.
   */
  @Transactional(readOnly = true)
  public Optional<NonceDTO> findOne(Long id) {
    log.debug("Request to get Nonce : {}", id);
    return nonceRepository.findById(id).map(nonceMapper::toDto);
  }

  /**
   * Delete the nonce by id.
   *
   * @param id the id of the entity.
   */
  public void delete(Long id) {
    log.debug("Request to delete Nonce : {}", id);
    nonceRepository.deleteById(id);
  }
}
