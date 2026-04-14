package gov.moda.dw.manager.service;

import java.util.Optional;
import gov.moda.dw.manager.domain.CookiesMsg;
import gov.moda.dw.manager.repository.CookiesMsgRepository;
import gov.moda.dw.manager.service.dto.CookiesMsgDTO;
import gov.moda.dw.manager.service.mapper.CookiesMsgMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link CookiesMsg}.
 */
@Service
@Transactional
public class CookiesMsgService {

  private final Logger log = LoggerFactory.getLogger(CookiesMsgService.class);

  private final CookiesMsgRepository cookiesMsgRepository;

  private final CookiesMsgMapper cookiesMsgMapper;

  public CookiesMsgService(CookiesMsgRepository cookiesMsgRepository, CookiesMsgMapper cookiesMsgMapper) {
    this.cookiesMsgRepository = cookiesMsgRepository;
    this.cookiesMsgMapper = cookiesMsgMapper;
  }

  /**
   * Save a cookiesMsg.
   *
   * @param cookiesMsgDTO the entity to save.
   * @return the persisted entity.
   */
  public CookiesMsgDTO save(CookiesMsgDTO cookiesMsgDTO) {
    log.debug("Request to save CookiesMsg : {}", cookiesMsgDTO);
    CookiesMsg cookiesMsg = cookiesMsgMapper.toEntity(cookiesMsgDTO);
    cookiesMsg = cookiesMsgRepository.save(cookiesMsg);
    return cookiesMsgMapper.toDto(cookiesMsg);
  }

  /**
   * Update a cookiesMsg.
   *
   * @param cookiesMsgDTO the entity to save.
   * @return the persisted entity.
   */
  public CookiesMsgDTO update(CookiesMsgDTO cookiesMsgDTO) {
    log.debug("Request to update CookiesMsg : {}", cookiesMsgDTO);
    CookiesMsg cookiesMsg = cookiesMsgMapper.toEntity(cookiesMsgDTO);
    cookiesMsg = cookiesMsgRepository.save(cookiesMsg);
    return cookiesMsgMapper.toDto(cookiesMsg);
  }

  /**
   * Partially update a cookiesMsg.
   *
   * @param cookiesMsgDTO the entity to update partially.
   * @return the persisted entity.
   */
  public Optional<CookiesMsgDTO> partialUpdate(CookiesMsgDTO cookiesMsgDTO) {
    log.debug("Request to partially update CookiesMsg : {}", cookiesMsgDTO);

    return cookiesMsgRepository
      .findById(cookiesMsgDTO.getId())
      .map(existingCookiesMsg -> {
        cookiesMsgMapper.partialUpdate(existingCookiesMsg, cookiesMsgDTO);

        return existingCookiesMsg;
      })
      .map(cookiesMsgRepository::save)
      .map(cookiesMsgMapper::toDto);
  }

  /**
   * Get one cookiesMsg by id.
   *
   * @param id the id of the entity.
   * @return the entity.
   */
  @Transactional(readOnly = true)
  public Optional<CookiesMsgDTO> findOne(Long id) {
    log.debug("Request to get CookiesMsg : {}", id);
    return cookiesMsgRepository.findById(id).map(cookiesMsgMapper::toDto);
  }

  /**
   * Delete the cookiesMsg by id.
   *
   * @param id the id of the entity.
   */
  public void delete(Long id) {
    log.debug("Request to delete CookiesMsg : {}", id);
    cookiesMsgRepository.deleteById(id);
  }
}
