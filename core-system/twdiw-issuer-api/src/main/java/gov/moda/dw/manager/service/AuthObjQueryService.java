package gov.moda.dw.manager.service;

import gov.moda.dw.manager.domain.*; // for static metamodels
import gov.moda.dw.manager.domain.AuthObj;
import gov.moda.dw.manager.repository.AuthObjRepository;
import gov.moda.dw.manager.service.criteria.AuthObjCriteria;
import gov.moda.dw.manager.service.dto.AuthObjDTO;
import gov.moda.dw.manager.service.mapper.AuthObjMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link AuthObj} entities in the database.
 * The main input is a {@link AuthObjCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link AuthObjDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AuthObjQueryService extends QueryService<AuthObj> {

  private final Logger log = LoggerFactory.getLogger(AuthObjQueryService.class);

  private final AuthObjRepository authObjRepository;

  private final AuthObjMapper authObjMapper;

  public AuthObjQueryService(AuthObjRepository authObjRepository, AuthObjMapper authObjMapper) {
    this.authObjRepository = authObjRepository;
    this.authObjMapper = authObjMapper;
  }

  /**
   * Return a {@link Page} of {@link AuthObjDTO} which matches the criteria from the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @param page The page, which should be returned.
   * @return the matching entities.
   */
  @Transactional(readOnly = true)
  public Page<AuthObjDTO> findByCriteria(AuthObjCriteria criteria, Pageable page) {
    log.debug("find by criteria : {}, page: {}", criteria, page);
    final Specification<AuthObj> specification = createSpecification(criteria);
    return authObjRepository.findAll(specification, page).map(authObjMapper::toDto);
  }

  /**
   * Return the number of matching entities in the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the number of matching entities.
   */
  @Transactional(readOnly = true)
  public long countByCriteria(AuthObjCriteria criteria) {
    log.debug("count by criteria : {}", criteria);
    final Specification<AuthObj> specification = createSpecification(criteria);
    return authObjRepository.count(specification);
  }

  /**
   * Function to convert {@link AuthObjCriteria} to a {@link Specification}
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the matching {@link Specification} of the entity.
   */
  protected Specification<AuthObj> createSpecification(AuthObjCriteria criteria) {
    Specification<AuthObj> specification = Specification.where(null);
    if (criteria != null) {
      // This has to be called first, because the distinct method returns null
      if (criteria.getDistinct() != null) {
        specification = specification.and(distinct(criteria.getDistinct()));
      }
      if (criteria.getId() != null) {
        specification = specification.and(buildStringSpecification(criteria.getId(), AuthObj_.id));
      }
      if (criteria.getUserId() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getUserId(), AuthObj_.userId));
      }
      if (criteria.getResCode() != null) {
        specification = specification.and(buildStringSpecification(criteria.getResCode(), AuthObj_.resCode));
      }
      if (criteria.getLogin() != null) {
        specification = specification.and(buildStringSpecification(criteria.getLogin(), AuthObj_.login));
      }
      if (criteria.getRoleId() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getRoleId(), AuthObj_.roleId));
      }
      if (criteria.getRoleCode() != null) {
        specification = specification.and(buildStringSpecification(criteria.getRoleCode(), AuthObj_.roleCode));
      }
      if (criteria.getRoleName() != null) {
        specification = specification.and(buildStringSpecification(criteria.getRoleName(), AuthObj_.roleName));
      }
    }
    return specification;
  }
}
