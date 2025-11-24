package gov.moda.dw.issuer.oidvci.service;

import gov.moda.dw.issuer.oidvci.domain.*; // for static metamodels
import gov.moda.dw.issuer.oidvci.domain.Rel;
import gov.moda.dw.issuer.oidvci.repository.RelRepository;
import gov.moda.dw.issuer.oidvci.service.criteria.RelCriteria;
import gov.moda.dw.issuer.oidvci.service.dto.RelDTO;
import gov.moda.dw.issuer.oidvci.service.mapper.RelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Rel} entities in the database.
 * The main input is a {@link RelCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link RelDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class RelQueryService extends QueryService<Rel> {

  private final Logger log = LoggerFactory.getLogger(RelQueryService.class);

  private final RelRepository relRepository;

  private final RelMapper relMapper;

  public RelQueryService(RelRepository relRepository, RelMapper relMapper) {
    this.relRepository = relRepository;
    this.relMapper = relMapper;
  }

  /**
   * Return a {@link Page} of {@link RelDTO} which matches the criteria from the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @param page The page, which should be returned.
   * @return the matching entities.
   */
  @Transactional(readOnly = true)
  public Page<RelDTO> findByCriteria(RelCriteria criteria, Pageable page) {
    log.debug("find by criteria : {}, page: {}", criteria, page);
    final Specification<Rel> specification = createSpecification(criteria);
    return relRepository.findAll(specification, page).map(relMapper::toDto);
  }

  /**
   * Return the number of matching entities in the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the number of matching entities.
   */
  @Transactional(readOnly = true)
  public long countByCriteria(RelCriteria criteria) {
    log.debug("count by criteria : {}", criteria);
    final Specification<Rel> specification = createSpecification(criteria);
    return relRepository.count(specification);
  }

  /**
   * Function to convert {@link RelCriteria} to a {@link Specification}
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the matching {@link Specification} of the entity.
   */
  protected Specification<Rel> createSpecification(RelCriteria criteria) {
    Specification<Rel> specification = Specification.where(null);
    if (criteria != null) {
      // This has to be called first, because the distinct method returns null
      if (criteria.getDistinct() != null) {
        specification = specification.and(distinct(criteria.getDistinct()));
      }
      if (criteria.getId() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getId(), Rel_.id));
      }
      if (criteria.getLeftTbl() != null) {
        specification = specification.and(buildStringSpecification(criteria.getLeftTbl(), Rel_.leftTbl));
      }
      if (criteria.getLeftId() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getLeftId(), Rel_.leftId));
      }
      if (criteria.getRightTbl() != null) {
        specification = specification.and(buildStringSpecification(criteria.getRightTbl(), Rel_.rightTbl));
      }
      if (criteria.getRightId() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getRightId(), Rel_.rightId));
      }
      if (criteria.getLeftCode() != null) {
        specification = specification.and(buildStringSpecification(criteria.getLeftCode(), Rel_.leftCode));
      }
      if (criteria.getRightCode() != null) {
        specification = specification.and(buildStringSpecification(criteria.getRightCode(), Rel_.rightCode));
      }
      if (criteria.getState() != null) {
        specification = specification.and(buildStringSpecification(criteria.getState(), Rel_.state));
      }
      if (criteria.getDataRole1() != null) {
        specification = specification.and(buildStringSpecification(criteria.getDataRole1(), Rel_.dataRole1));
      }
      if (criteria.getDataRole2() != null) {
        specification = specification.and(buildStringSpecification(criteria.getDataRole2(), Rel_.dataRole2));
      }
      if (criteria.getDataAuth() != null) {
        specification = specification.and(buildStringSpecification(criteria.getDataAuth(), Rel_.dataAuth));
      }
      if (criteria.getCreateTime() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getCreateTime(), Rel_.createTime));
      }
    }
    return specification;
  }
}
