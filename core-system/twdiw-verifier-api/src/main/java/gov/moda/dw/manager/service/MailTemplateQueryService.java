package gov.moda.dw.manager.service;

import gov.moda.dw.manager.domain.*; // for static metamodels
import gov.moda.dw.manager.domain.MailTemplate;
import gov.moda.dw.manager.repository.MailTemplateRepository;
import gov.moda.dw.manager.service.criteria.MailTemplateCriteria;
import gov.moda.dw.manager.service.dto.MailTemplateDTO;
import gov.moda.dw.manager.service.mapper.MailTemplateMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link MailTemplate} entities in the database.
 * The main input is a {@link MailTemplateCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link MailTemplateDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class MailTemplateQueryService extends QueryService<MailTemplate> {

  private final Logger log = LoggerFactory.getLogger(MailTemplateQueryService.class);

  private final MailTemplateRepository mailTemplateRepository;

  private final MailTemplateMapper mailTemplateMapper;

  public MailTemplateQueryService(MailTemplateRepository mailTemplateRepository, MailTemplateMapper mailTemplateMapper) {
    this.mailTemplateRepository = mailTemplateRepository;
    this.mailTemplateMapper = mailTemplateMapper;
  }

  /**
   * Return a {@link Page} of {@link MailTemplateDTO} which matches the criteria from the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @param page The page, which should be returned.
   * @return the matching entities.
   */
  @Transactional(readOnly = true)
  public Page<MailTemplateDTO> findByCriteria(MailTemplateCriteria criteria, Pageable page) {
    log.debug("find by criteria : {}, page: {}", criteria, page);
    final Specification<MailTemplate> specification = createSpecification(criteria);
    return mailTemplateRepository.findAll(specification, page).map(mailTemplateMapper::toDto);
  }

  /**
   * Return the number of matching entities in the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the number of matching entities.
   */
  @Transactional(readOnly = true)
  public long countByCriteria(MailTemplateCriteria criteria) {
    log.debug("count by criteria : {}", criteria);
    final Specification<MailTemplate> specification = createSpecification(criteria);
    return mailTemplateRepository.count(specification);
  }

  /**
   * Function to convert {@link MailTemplateCriteria} to a {@link Specification}
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the matching {@link Specification} of the entity.
   */
  protected Specification<MailTemplate> createSpecification(MailTemplateCriteria criteria) {
    Specification<MailTemplate> specification = Specification.where(null);
    if (criteria != null) {
      // This has to be called first, because the distinct method returns null
      if (criteria.getDistinct() != null) {
        specification = specification.and(distinct(criteria.getDistinct()));
      }
      if (criteria.getId() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getId(), MailTemplate_.id));
      }
      if (criteria.getMailType() != null) {
        specification = specification.and(buildStringSpecification(criteria.getMailType(), MailTemplate_.mailType));
      }
      if (criteria.getDescription() != null) {
        specification = specification.and(buildStringSpecification(criteria.getDescription(), MailTemplate_.description));
      }
      if (criteria.getRecipientRole() != null) {
        specification = specification.and(buildStringSpecification(criteria.getRecipientRole(), MailTemplate_.recipientRole));
      }
      if (criteria.getSubject() != null) {
        specification = specification.and(buildStringSpecification(criteria.getSubject(), MailTemplate_.subject));
      }
      if (criteria.getHtmlState() != null) {
        specification = specification.and(buildStringSpecification(criteria.getHtmlState(), MailTemplate_.htmlState));
      }
      if (criteria.getActivated() != null) {
        specification = specification.and(buildStringSpecification(criteria.getActivated(), MailTemplate_.activated));
      }
      if (criteria.getCreateTime() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getCreateTime(), MailTemplate_.createTime));
      }
    }
    return specification;
  }
}
