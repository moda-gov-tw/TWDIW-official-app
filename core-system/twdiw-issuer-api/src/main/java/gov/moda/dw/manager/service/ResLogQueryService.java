package gov.moda.dw.manager.service;

import gov.moda.dw.manager.domain.*; // for static metamodels
import gov.moda.dw.manager.domain.ResLog;
import gov.moda.dw.manager.repository.ResLogRepository;
import gov.moda.dw.manager.service.criteria.ResLogCriteria;
import gov.moda.dw.manager.service.dto.ResLogDTO;
import gov.moda.dw.manager.service.mapper.ResLogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link ResLog} entities in the database.
 * The main input is a {@link ResLogCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ResLogDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ResLogQueryService extends QueryService<ResLog> {

  private final Logger log = LoggerFactory.getLogger(ResLogQueryService.class);

  private final ResLogRepository resLogRepository;

  private final ResLogMapper resLogMapper;

  public ResLogQueryService(ResLogRepository resLogRepository, ResLogMapper resLogMapper) {
    this.resLogRepository = resLogRepository;
    this.resLogMapper = resLogMapper;
  }

  /**
   * Return a {@link Page} of {@link ResLogDTO} which matches the criteria from the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @param page The page, which should be returned.
   * @return the matching entities.
   */
  @Transactional(readOnly = true)
  public Page<ResLogDTO> findByCriteria(ResLogCriteria criteria, Pageable page) {
    log.debug("find by criteria : {}, page: {}", criteria, page);
    final Specification<ResLog> specification = createSpecification(criteria);
    return resLogRepository.findAll(specification, page).map(resLogMapper::toDto);
  }

  /**
   * Return the number of matching entities in the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the number of matching entities.
   */
  @Transactional(readOnly = true)
  public long countByCriteria(ResLogCriteria criteria) {
    log.debug("count by criteria : {}", criteria);
    final Specification<ResLog> specification = createSpecification(criteria);
    return resLogRepository.count(specification);
  }

  /**
   * Function to convert {@link ResLogCriteria} to a {@link Specification}
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the matching {@link Specification} of the entity.
   */
  protected Specification<ResLog> createSpecification(ResLogCriteria criteria) {
    Specification<ResLog> specification = Specification.where(null);
    if (criteria != null) {
      // This has to be called first, because the distinct method returns null
      if (criteria.getDistinct() != null) {
        specification = specification.and(distinct(criteria.getDistinct()));
      }
      if (criteria.getId() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getId(), ResLog_.id));
      }
      if (criteria.getActor() != null) {
        specification = specification.and(buildStringSpecification(criteria.getActor(), ResLog_.actor));
      }
      if (criteria.getLogType() != null) {
        specification = specification.and(buildStringSpecification(criteria.getLogType(), ResLog_.logType));
      }
      if (criteria.getLogTime() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getLogTime(), ResLog_.logTime));
      }
      if (criteria.getTypeId() != null) {
        specification = specification.and(buildStringSpecification(criteria.getTypeId(), ResLog_.typeId));
      }
      if (criteria.getResId() != null) {
        specification = specification.and(buildStringSpecification(criteria.getResId(), ResLog_.resId));
      }
      if (criteria.getResGrp() != null) {
        specification = specification.and(buildStringSpecification(criteria.getResGrp(), ResLog_.resGrp));
      }
      if (criteria.getResName() != null) {
        specification = specification.and(buildStringSpecification(criteria.getResName(), ResLog_.resName));
      }
      if (criteria.getDescription() != null) {
        specification = specification.and(buildStringSpecification(criteria.getDescription(), ResLog_.description));
      }
      if (criteria.getState() != null) {
        specification = specification.and(buildStringSpecification(criteria.getState(), ResLog_.state));
      }
      if (criteria.getApiUri() != null) {
        specification = specification.and(buildStringSpecification(criteria.getApiUri(), ResLog_.apiUri));
      }
      if (criteria.getWebUrl() != null) {
        specification = specification.and(buildStringSpecification(criteria.getWebUrl(), ResLog_.webUrl));
      }
      if (criteria.getDataRole1() != null) {
        specification = specification.and(buildStringSpecification(criteria.getDataRole1(), ResLog_.dataRole1));
      }
      if (criteria.getDataRole2() != null) {
        specification = specification.and(buildStringSpecification(criteria.getDataRole2(), ResLog_.dataRole2));
      }
      if (criteria.getCreateTime() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getCreateTime(), ResLog_.createTime));
      }
    }
    return specification;
  }
}
