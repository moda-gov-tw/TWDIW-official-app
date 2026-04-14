package gov.moda.dw.manager.service;

import gov.moda.dw.manager.domain.*; // for static metamodels
import gov.moda.dw.manager.domain.BwdParam;
import gov.moda.dw.manager.repository.BwdParamRepository;
import gov.moda.dw.manager.service.criteria.BwdParamCriteria;
import gov.moda.dw.manager.service.dto.BwdParamDTO;
import gov.moda.dw.manager.service.mapper.BwdParamMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link BwdParam} entities in the database.
 * The main input is a {@link BwdParamCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link BwdParamDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class BwdParamQueryService extends QueryService<BwdParam> {

  private final Logger log = LoggerFactory.getLogger(BwdParamQueryService.class);

  private final BwdParamRepository bwdParamRepository;

  private final BwdParamMapper bwdParamMapper;

  public BwdParamQueryService(BwdParamRepository bwdParamRepository, BwdParamMapper bwdParamMapper) {
    this.bwdParamRepository = bwdParamRepository;
    this.bwdParamMapper = bwdParamMapper;
  }

  /**
   * Return a {@link Page} of {@link BwdParamDTO} which matches the criteria from the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @param page The page, which should be returned.
   * @return the matching entities.
   */
  @Transactional(readOnly = true)
  public Page<BwdParamDTO> findByCriteria(BwdParamCriteria criteria, Pageable page) {
    log.debug("find by criteria : {}, page: {}", criteria, page);
    final Specification<BwdParam> specification = createSpecification(criteria);
    return bwdParamRepository.findAll(specification, page).map(bwdParamMapper::toDto);
  }

  /**
   * Return the number of matching entities in the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the number of matching entities.
   */
  @Transactional(readOnly = true)
  public long countByCriteria(BwdParamCriteria criteria) {
    log.debug("count by criteria : {}", criteria);
    final Specification<BwdParam> specification = createSpecification(criteria);
    return bwdParamRepository.count(specification);
  }

  /**
   * Function to convert {@link BwdParamCriteria} to a {@link Specification}
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the matching {@link Specification} of the entity.
   */
  protected Specification<BwdParam> createSpecification(BwdParamCriteria criteria) {
    Specification<BwdParam> specification = Specification.where(null);
    if (criteria != null) {
      // This has to be called first, because the distinct method returns null
      if (criteria.getDistinct() != null) {
        specification = specification.and(distinct(criteria.getDistinct()));
      }
      if (criteria.getId() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getId(), BwdParam_.id));
      }
      if (criteria.getBwdProfileId() != null) {
        specification = specification.and(buildStringSpecification(criteria.getBwdProfileId(), BwdParam_.bwdProfileId));
      }
      if (criteria.getRuleId() != null) {
        specification = specification.and(buildStringSpecification(criteria.getRuleId(), BwdParam_.ruleId));
      }
      if (criteria.getRuleName() != null) {
        specification = specification.and(buildStringSpecification(criteria.getRuleName(), BwdParam_.ruleName));
      }
      if (criteria.getDescription() != null) {
        specification = specification.and(buildStringSpecification(criteria.getDescription(), BwdParam_.description));
      }
      if (criteria.getState() != null) {
        specification = specification.and(buildSpecification(criteria.getState(), BwdParam_.state));
      }
      if (criteria.getStrRegular() != null) {
        specification = specification.and(buildStringSpecification(criteria.getStrRegular(), BwdParam_.strRegular));
      }
      if (criteria.getParamValue() != null) {
        specification = specification.and(buildStringSpecification(criteria.getParamValue(), BwdParam_.paramValue));
      }
      if (criteria.getActionType() != null) {
        specification = specification.and(buildStringSpecification(criteria.getActionType(), BwdParam_.actionType));
      }
      if (criteria.getCheckType() != null) {
        specification = specification.and(buildStringSpecification(criteria.getCheckType(), BwdParam_.checkType));
      }
      if (criteria.getErrorMessage() != null) {
        specification = specification.and(buildStringSpecification(criteria.getErrorMessage(), BwdParam_.errorMessage));
      }
      if (criteria.getCreateTime() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getCreateTime(), BwdParam_.createTime));
      }
      if (criteria.getUpdateTime() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getUpdateTime(), BwdParam_.updateTime));
      }
    }
    return specification;
  }
}
