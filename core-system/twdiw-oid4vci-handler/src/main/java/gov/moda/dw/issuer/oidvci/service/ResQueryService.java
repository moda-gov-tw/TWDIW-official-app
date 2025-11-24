package gov.moda.dw.issuer.oidvci.service;

import gov.moda.dw.issuer.oidvci.domain.*; // for static metamodels
import gov.moda.dw.issuer.oidvci.domain.Res;
import gov.moda.dw.issuer.oidvci.repository.ResRepository;
import gov.moda.dw.issuer.oidvci.service.criteria.ResCriteria;
import gov.moda.dw.issuer.oidvci.service.dto.ResDTO;
import gov.moda.dw.issuer.oidvci.service.mapper.ResMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Res} entities in the database.
 * The main input is a {@link ResCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ResDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ResQueryService extends QueryService<Res> {

  private final Logger log = LoggerFactory.getLogger(ResQueryService.class);

  private final ResRepository resRepository;

  private final ResMapper resMapper;

  public ResQueryService(ResRepository resRepository, ResMapper resMapper) {
    this.resRepository = resRepository;
    this.resMapper = resMapper;
  }

  /**
   * Return a {@link Page} of {@link ResDTO} which matches the criteria from the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @param page The page, which should be returned.
   * @return the matching entities.
   */
  @Transactional(readOnly = true)
  public Page<ResDTO> findByCriteria(ResCriteria criteria, Pageable page) {
    log.debug("find by criteria : {}, page: {}", criteria, page);
    final Specification<Res> specification = createSpecification(criteria);
    return resRepository.findAll(specification, page).map(resMapper::toDto);
  }

  /**
   * Return the number of matching entities in the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the number of matching entities.
   */
  @Transactional(readOnly = true)
  public long countByCriteria(ResCriteria criteria) {
    log.debug("count by criteria : {}", criteria);
    final Specification<Res> specification = createSpecification(criteria);
    return resRepository.count(specification);
  }

  /**
   * Function to convert {@link ResCriteria} to a {@link Specification}
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the matching {@link Specification} of the entity.
   */
  protected Specification<Res> createSpecification(ResCriteria criteria) {
    Specification<Res> specification = Specification.where(null);
    if (criteria != null) {
      // This has to be called first, because the distinct method returns null
      if (criteria.getDistinct() != null) {
        specification = specification.and(distinct(criteria.getDistinct()));
      }
      if (criteria.getId() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getId(), Res_.id));
      }
      if (criteria.getTypeId() != null) {
        specification = specification.and(buildStringSpecification(criteria.getTypeId(), Res_.typeId));
      }
      if (criteria.getResId() != null) {
        specification = specification.and(buildStringSpecification(criteria.getResId(), Res_.resId));
      }
      if (criteria.getResGrp() != null) {
        specification = specification.and(buildStringSpecification(criteria.getResGrp(), Res_.resGrp));
      }
      if (criteria.getResName() != null) {
        specification = specification.and(buildStringSpecification(criteria.getResName(), Res_.resName));
      }
      if (criteria.getDescription() != null) {
        specification = specification.and(buildStringSpecification(criteria.getDescription(), Res_.description));
      }
      if (criteria.getState() != null) {
        specification = specification.and(buildStringSpecification(criteria.getState(), Res_.state));
      }
      if (criteria.getApiUri() != null) {
        specification = specification.and(buildStringSpecification(criteria.getApiUri(), Res_.apiUri));
      }
      if (criteria.getWebUrl() != null) {
        specification = specification.and(buildStringSpecification(criteria.getWebUrl(), Res_.webUrl));
      }
      if (criteria.getDataRole1() != null) {
        specification = specification.and(buildStringSpecification(criteria.getDataRole1(), Res_.dataRole1));
      }
      if (criteria.getDataRole2() != null) {
        specification = specification.and(buildStringSpecification(criteria.getDataRole2(), Res_.dataRole2));
      }
      if (criteria.getCreateTime() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getCreateTime(), Res_.createTime));
      }
    }
    return specification;
  }
}
