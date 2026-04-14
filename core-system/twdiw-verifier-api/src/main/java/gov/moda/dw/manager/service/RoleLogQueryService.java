package gov.moda.dw.manager.service;

import gov.moda.dw.manager.domain.*; // for static metamodels
import gov.moda.dw.manager.domain.RoleLog;
import gov.moda.dw.manager.repository.RoleLogRepository;
import gov.moda.dw.manager.service.criteria.RoleLogCriteria;
import gov.moda.dw.manager.service.dto.RoleLogDTO;
import gov.moda.dw.manager.service.mapper.RoleLogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link RoleLog} entities in the database.
 * The main input is a {@link RoleLogCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link RoleLogDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class RoleLogQueryService extends QueryService<RoleLog> {

  private final Logger log = LoggerFactory.getLogger(RoleLogQueryService.class);

  private final RoleLogRepository roleLogRepository;

  private final RoleLogMapper roleLogMapper;

  public RoleLogQueryService(RoleLogRepository roleLogRepository, RoleLogMapper roleLogMapper) {
    this.roleLogRepository = roleLogRepository;
    this.roleLogMapper = roleLogMapper;
  }

  /**
   * Return a {@link Page} of {@link RoleLogDTO} which matches the criteria from the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @param page The page, which should be returned.
   * @return the matching entities.
   */
  @Transactional(readOnly = true)
  public Page<RoleLogDTO> findByCriteria(RoleLogCriteria criteria, Pageable page) {
    log.debug("find by criteria : {}, page: {}", criteria, page);
    final Specification<RoleLog> specification = createSpecification(criteria);
    return roleLogRepository.findAll(specification, page).map(roleLogMapper::toDto);
  }

  /**
   * Return the number of matching entities in the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the number of matching entities.
   */
  @Transactional(readOnly = true)
  public long countByCriteria(RoleLogCriteria criteria) {
    log.debug("count by criteria : {}", criteria);
    final Specification<RoleLog> specification = createSpecification(criteria);
    return roleLogRepository.count(specification);
  }

  /**
   * Function to convert {@link RoleLogCriteria} to a {@link Specification}
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the matching {@link Specification} of the entity.
   */
  protected Specification<RoleLog> createSpecification(RoleLogCriteria criteria) {
    Specification<RoleLog> specification = Specification.where(null);
    if (criteria != null) {
      // This has to be called first, because the distinct method returns null
      if (criteria.getDistinct() != null) {
        specification = specification.and(distinct(criteria.getDistinct()));
      }
      if (criteria.getId() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getId(), RoleLog_.id));
      }
      if (criteria.getActor() != null) {
        specification = specification.and(buildStringSpecification(criteria.getActor(), RoleLog_.actor));
      }
      if (criteria.getLogType() != null) {
        specification = specification.and(buildStringSpecification(criteria.getLogType(), RoleLog_.logType));
      }
      if (criteria.getLogTime() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getLogTime(), RoleLog_.logTime));
      }
      if (criteria.getRoleId() != null) {
        specification = specification.and(buildStringSpecification(criteria.getRoleId(), RoleLog_.roleId));
      }
      if (criteria.getRoleName() != null) {
        specification = specification.and(buildStringSpecification(criteria.getRoleName(), RoleLog_.roleName));
      }
      if (criteria.getDescription() != null) {
        specification = specification.and(buildStringSpecification(criteria.getDescription(), RoleLog_.description));
      }
      if (criteria.getState() != null) {
        specification = specification.and(buildStringSpecification(criteria.getState(), RoleLog_.state));
      }
      if (criteria.getDataRole1() != null) {
        specification = specification.and(buildStringSpecification(criteria.getDataRole1(), RoleLog_.dataRole1));
      }
      if (criteria.getDataRole2() != null) {
        specification = specification.and(buildStringSpecification(criteria.getDataRole2(), RoleLog_.dataRole2));
      }
      if (criteria.getCreateTime() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getCreateTime(), RoleLog_.createTime));
      }
      if (criteria.getAuthChangeTime() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getAuthChangeTime(), RoleLog_.authChangeTime));
      }
    }
    return specification;
  }
}
