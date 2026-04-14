package gov.moda.dw.manager.service;

import gov.moda.dw.manager.domain.*; // for static metamodels
import gov.moda.dw.manager.domain.BwdHistory;
import gov.moda.dw.manager.repository.BwdHistoryRepository;
import gov.moda.dw.manager.service.criteria.BwdHistoryCriteria;
import gov.moda.dw.manager.service.dto.BwdHistoryDTO;
import gov.moda.dw.manager.service.mapper.BwdHistoryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link BwdHistory} entities in the database.
 * The main input is a {@link BwdHistoryCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link BwdHistoryDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class BwdHistoryQueryService extends QueryService<BwdHistory> {

  private final Logger log = LoggerFactory.getLogger(BwdHistoryQueryService.class);

  private final BwdHistoryRepository bwdHistoryRepository;

  private final BwdHistoryMapper bwdHistoryMapper;

  public BwdHistoryQueryService(BwdHistoryRepository bwdHistoryRepository, BwdHistoryMapper bwdHistoryMapper) {
    this.bwdHistoryRepository = bwdHistoryRepository;
    this.bwdHistoryMapper = bwdHistoryMapper;
  }

  /**
   * Return a {@link Page} of {@link BwdHistoryDTO} which matches the criteria from the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @param page The page, which should be returned.
   * @return the matching entities.
   */
  @Transactional(readOnly = true)
  public Page<BwdHistoryDTO> findByCriteria(BwdHistoryCriteria criteria, Pageable page) {
    log.debug("find by criteria : {}, page: {}", criteria, page);
    final Specification<BwdHistory> specification = createSpecification(criteria);
    return bwdHistoryRepository.findAll(specification, page).map(bwdHistoryMapper::toDto);
  }

  /**
   * Return the number of matching entities in the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the number of matching entities.
   */
  @Transactional(readOnly = true)
  public long countByCriteria(BwdHistoryCriteria criteria) {
    log.debug("count by criteria : {}", criteria);
    final Specification<BwdHistory> specification = createSpecification(criteria);
    return bwdHistoryRepository.count(specification);
  }

  /**
   * Function to convert {@link BwdHistoryCriteria} to a {@link Specification}
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the matching {@link Specification} of the entity.
   */
  protected Specification<BwdHistory> createSpecification(BwdHistoryCriteria criteria) {
    Specification<BwdHistory> specification = Specification.where(null);
    if (criteria != null) {
      // This has to be called first, because the distinct method returns null
      if (criteria.getDistinct() != null) {
        specification = specification.and(distinct(criteria.getDistinct()));
      }
      if (criteria.getId() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getId(), BwdHistory_.id));
      }
      if (criteria.getUserId() != null) {
        specification = specification.and(buildStringSpecification(criteria.getUserId(), BwdHistory_.userId));
      }
      if (criteria.getCreateTime() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getCreateTime(), BwdHistory_.createTime));
      }
      if (criteria.getBwdHash() != null) {
        specification = specification.and(buildStringSpecification(criteria.getBwdHash(), BwdHistory_.bwdHash));
      }
      if (criteria.getBwdCode() != null) {
        specification = specification.and(buildStringSpecification(criteria.getBwdCode(), BwdHistory_.bwdCode));
      }
    }
    return specification;
  }
}
