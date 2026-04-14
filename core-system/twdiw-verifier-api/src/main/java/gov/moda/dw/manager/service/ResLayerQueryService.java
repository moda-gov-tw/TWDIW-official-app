package gov.moda.dw.manager.service;

import gov.moda.dw.manager.domain.*; // for static metamodels
import gov.moda.dw.manager.domain.ResLayer;
import gov.moda.dw.manager.repository.ResLayerRepository;
import gov.moda.dw.manager.service.criteria.ResLayerCriteria;
import gov.moda.dw.manager.service.dto.ResLayerDTO;
import gov.moda.dw.manager.service.mapper.ResLayerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link ResLayer} entities in the database.
 * The main input is a {@link ResLayerCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ResLayerDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ResLayerQueryService extends QueryService<ResLayer> {

  private final Logger log = LoggerFactory.getLogger(ResLayerQueryService.class);

  private final ResLayerRepository resLayerRepository;

  private final ResLayerMapper resLayerMapper;

  public ResLayerQueryService(ResLayerRepository resLayerRepository, ResLayerMapper resLayerMapper) {
    this.resLayerRepository = resLayerRepository;
    this.resLayerMapper = resLayerMapper;
  }

  /**
   * Return a {@link Page} of {@link ResLayerDTO} which matches the criteria from the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @param page The page, which should be returned.
   * @return the matching entities.
   */
  @Transactional(readOnly = true)
  public Page<ResLayerDTO> findByCriteria(ResLayerCriteria criteria, Pageable page) {
    log.debug("find by criteria : {}, page: {}", criteria, page);
    final Specification<ResLayer> specification = createSpecification(criteria);
    return resLayerRepository.findAll(specification, page).map(resLayerMapper::toDto);
  }

  /**
   * Return the number of matching entities in the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the number of matching entities.
   */
  @Transactional(readOnly = true)
  public long countByCriteria(ResLayerCriteria criteria) {
    log.debug("count by criteria : {}", criteria);
    final Specification<ResLayer> specification = createSpecification(criteria);
    return resLayerRepository.count(specification);
  }

  /**
   * Function to convert {@link ResLayerCriteria} to a {@link Specification}
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the matching {@link Specification} of the entity.
   */
  protected Specification<ResLayer> createSpecification(ResLayerCriteria criteria) {
    Specification<ResLayer> specification = Specification.where(null);
    if (criteria != null) {
      // This has to be called first, because the distinct method returns null
      if (criteria.getDistinct() != null) {
        specification = specification.and(distinct(criteria.getDistinct()));
      }
      if (criteria.getId() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getId(), ResLayer_.id));
      }
      if (criteria.getParentId() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getParentId(), ResLayer_.parentId));
      }
      if (criteria.getChildId() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getChildId(), ResLayer_.childId));
      }
      if (criteria.getParentCode() != null) {
        specification = specification.and(buildStringSpecification(criteria.getParentCode(), ResLayer_.parentCode));
      }
      if (criteria.getChildCode() != null) {
        specification = specification.and(buildStringSpecification(criteria.getChildCode(), ResLayer_.childCode));
      }
      if (criteria.getCreateTime() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getCreateTime(), ResLayer_.createTime));
      }
      if (criteria.getOrderval() != null) {
        specification = specification.and(buildStringSpecification(criteria.getOrderval(), ResLayer_.orderval));
      }
    }
    return specification;
  }
}
