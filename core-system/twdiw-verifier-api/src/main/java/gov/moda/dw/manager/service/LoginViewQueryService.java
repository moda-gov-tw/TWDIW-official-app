package gov.moda.dw.manager.service;

import gov.moda.dw.manager.domain.*; // for static metamodels
import gov.moda.dw.manager.domain.LoginView;
import gov.moda.dw.manager.repository.LoginViewRepository;
import gov.moda.dw.manager.service.criteria.LoginViewCriteria;
import gov.moda.dw.manager.service.dto.LoginViewDTO;
import gov.moda.dw.manager.service.mapper.LoginViewMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link LoginView} entities in the database.
 * The main input is a {@link LoginViewCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link LoginViewDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class LoginViewQueryService extends QueryService<LoginView> {

  private final Logger log = LoggerFactory.getLogger(LoginViewQueryService.class);

  private final LoginViewRepository loginViewRepository;

  private final LoginViewMapper loginViewMapper;

  public LoginViewQueryService(LoginViewRepository loginViewRepository, LoginViewMapper loginViewMapper) {
    this.loginViewRepository = loginViewRepository;
    this.loginViewMapper = loginViewMapper;
  }

  /**
   * Return a {@link Page} of {@link LoginViewDTO} which matches the criteria from the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @param page The page, which should be returned.
   * @return the matching entities.
   */
  @Transactional(readOnly = true)
  public Page<LoginViewDTO> findByCriteria(LoginViewCriteria criteria, Pageable page) {
    log.debug("find by criteria : {}, page: {}", criteria, page);
    final Specification<LoginView> specification = createSpecification(criteria);
    return loginViewRepository.findAll(specification, page).map(loginViewMapper::toDto);
  }

  /**
   * Return the number of matching entities in the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the number of matching entities.
   */
  @Transactional(readOnly = true)
  public long countByCriteria(LoginViewCriteria criteria) {
    log.debug("count by criteria : {}", criteria);
    final Specification<LoginView> specification = createSpecification(criteria);
    return loginViewRepository.count(specification);
  }

  /**
   * Function to convert {@link LoginViewCriteria} to a {@link Specification}
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the matching {@link Specification} of the entity.
   */
  protected Specification<LoginView> createSpecification(LoginViewCriteria criteria) {
    Specification<LoginView> specification = Specification.where(null);
    if (criteria != null) {
      // This has to be called first, because the distinct method returns null
      if (criteria.getDistinct() != null) {
        specification = specification.and(distinct(criteria.getDistinct()));
      }
      if (criteria.getId() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getId(), LoginView_.id));
      }
      if (criteria.getUserId() != null) {
        specification = specification.and(buildStringSpecification(criteria.getUserId(), LoginView_.userId));
      }
      if (criteria.getFailCount() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getFailCount(), LoginView_.failCount));
      }
      if (criteria.getLastLogin() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getLastLogin(), LoginView_.lastLogin));
      }
      if (criteria.getBwdHash() != null) {
        specification = specification.and(buildStringSpecification(criteria.getBwdHash(), LoginView_.bwdHash));
      }
      if (criteria.getBwdDate() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getBwdDate(), LoginView_.bwdDate));
      }
      if (criteria.getLoginIdState() != null) {
        specification = specification.and(buildStringSpecification(criteria.getLoginIdState(), LoginView_.loginIdState));
      }
    }
    return specification;
  }
}
