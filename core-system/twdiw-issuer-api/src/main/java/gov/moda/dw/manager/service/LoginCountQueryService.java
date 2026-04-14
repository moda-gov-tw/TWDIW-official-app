package gov.moda.dw.manager.service;

import gov.moda.dw.manager.domain.*; // for static metamodels
import gov.moda.dw.manager.domain.LoginCount;
import gov.moda.dw.manager.repository.LoginCountRepository;
import gov.moda.dw.manager.service.criteria.LoginCountCriteria;
import gov.moda.dw.manager.service.dto.LoginCountDTO;
import gov.moda.dw.manager.service.mapper.LoginCountMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link LoginCount} entities in the database.
 * The main input is a {@link LoginCountCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link LoginCountDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class LoginCountQueryService extends QueryService<LoginCount> {

  private final Logger log = LoggerFactory.getLogger(LoginCountQueryService.class);

  private final LoginCountRepository loginCountRepository;

  private final LoginCountMapper loginCountMapper;

  public LoginCountQueryService(LoginCountRepository loginCountRepository, LoginCountMapper loginCountMapper) {
    this.loginCountRepository = loginCountRepository;
    this.loginCountMapper = loginCountMapper;
  }

  /**
   * Return a {@link Page} of {@link LoginCountDTO} which matches the criteria from the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @param page The page, which should be returned.
   * @return the matching entities.
   */
  @Transactional(readOnly = true)
  public Page<LoginCountDTO> findByCriteria(LoginCountCriteria criteria, Pageable page) {
    log.debug("find by criteria : {}, page: {}", criteria, page);
    final Specification<LoginCount> specification = createSpecification(criteria);
    return loginCountRepository.findAll(specification, page).map(loginCountMapper::toDto);
  }

  /**
   * Return the number of matching entities in the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the number of matching entities.
   */
  @Transactional(readOnly = true)
  public long countByCriteria(LoginCountCriteria criteria) {
    log.debug("count by criteria : {}", criteria);
    final Specification<LoginCount> specification = createSpecification(criteria);
    return loginCountRepository.count(specification);
  }

  /**
   * Function to convert {@link LoginCountCriteria} to a {@link Specification}
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the matching {@link Specification} of the entity.
   */
  protected Specification<LoginCount> createSpecification(LoginCountCriteria criteria) {
    Specification<LoginCount> specification = Specification.where(null);
    if (criteria != null) {
      // This has to be called first, because the distinct method returns null
      if (criteria.getDistinct() != null) {
        specification = specification.and(distinct(criteria.getDistinct()));
      }
      if (criteria.getId() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getId(), LoginCount_.id));
      }
      if (criteria.getUserId() != null) {
        specification = specification.and(buildStringSpecification(criteria.getUserId(), LoginCount_.userId));
      }
      if (criteria.getFailCount() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getFailCount(), LoginCount_.failCount));
      }
      if (criteria.getUpdateTime() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getUpdateTime(), LoginCount_.updateTime));
      }
    }
    return specification;
  }
}
