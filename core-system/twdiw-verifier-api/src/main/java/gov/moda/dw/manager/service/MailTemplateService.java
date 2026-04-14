package gov.moda.dw.manager.service;

import java.util.Optional;
import gov.moda.dw.manager.domain.MailTemplate;
import gov.moda.dw.manager.repository.MailTemplateRepository;
import gov.moda.dw.manager.service.dto.MailTemplateDTO;
import gov.moda.dw.manager.service.mapper.MailTemplateMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link MailTemplate}.
 */
@Service
@Transactional
public class MailTemplateService {

  private final Logger log = LoggerFactory.getLogger(MailTemplateService.class);

  private final MailTemplateRepository mailTemplateRepository;

  private final MailTemplateMapper mailTemplateMapper;

  public MailTemplateService(MailTemplateRepository mailTemplateRepository, MailTemplateMapper mailTemplateMapper) {
    this.mailTemplateRepository = mailTemplateRepository;
    this.mailTemplateMapper = mailTemplateMapper;
  }

  /**
   * Save a mailTemplate.
   *
   * @param mailTemplateDTO the entity to save.
   * @return the persisted entity.
   */
  public MailTemplateDTO save(MailTemplateDTO mailTemplateDTO) {
    log.debug("Request to save MailTemplate : {}", mailTemplateDTO);
    MailTemplate mailTemplate = mailTemplateMapper.toEntity(mailTemplateDTO);
    mailTemplate = mailTemplateRepository.save(mailTemplate);
    return mailTemplateMapper.toDto(mailTemplate);
  }

  /**
   * Update a mailTemplate.
   *
   * @param mailTemplateDTO the entity to save.
   * @return the persisted entity.
   */
  public MailTemplateDTO update(MailTemplateDTO mailTemplateDTO) {
    log.debug("Request to update MailTemplate : {}", mailTemplateDTO);
    MailTemplate mailTemplate = mailTemplateMapper.toEntity(mailTemplateDTO);
    mailTemplate = mailTemplateRepository.save(mailTemplate);
    return mailTemplateMapper.toDto(mailTemplate);
  }

  /**
   * Partially update a mailTemplate.
   *
   * @param mailTemplateDTO the entity to update partially.
   * @return the persisted entity.
   */
  public Optional<MailTemplateDTO> partialUpdate(MailTemplateDTO mailTemplateDTO) {
    log.debug("Request to partially update MailTemplate : {}", mailTemplateDTO);

    return mailTemplateRepository
      .findById(mailTemplateDTO.getId())
      .map(existingMailTemplate -> {
        mailTemplateMapper.partialUpdate(existingMailTemplate, mailTemplateDTO);

        return existingMailTemplate;
      })
      .map(mailTemplateRepository::save)
      .map(mailTemplateMapper::toDto);
  }

  /**
   * Get one mailTemplate by id.
   *
   * @param id the id of the entity.
   * @return the entity.
   */
  @Transactional(readOnly = true)
  public Optional<MailTemplateDTO> findOne(Long id) {
    log.debug("Request to get MailTemplate : {}", id);
    return mailTemplateRepository.findById(id).map(mailTemplateMapper::toDto);
  }

  /**
   * Delete the mailTemplate by id.
   *
   * @param id the id of the entity.
   */
  public void delete(Long id) {
    log.debug("Request to delete MailTemplate : {}", id);
    mailTemplateRepository.deleteById(id);
  }
}
