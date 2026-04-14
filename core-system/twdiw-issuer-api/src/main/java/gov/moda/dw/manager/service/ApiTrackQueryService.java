package gov.moda.dw.manager.service;

import gov.moda.dw.manager.domain.*; // for static metamodels
import gov.moda.dw.manager.domain.ApiTrack;
import gov.moda.dw.manager.repository.ApiTrackRepository;
import gov.moda.dw.manager.service.criteria.ApiTrackCriteria;
import gov.moda.dw.manager.service.dto.ApiTrackDTO;
import gov.moda.dw.manager.service.mapper.ApiTrackMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link ApiTrack} entities in the database.
 * The main input is a {@link ApiTrackCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ApiTrackDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ApiTrackQueryService extends QueryService<ApiTrack> {

  private final Logger log = LoggerFactory.getLogger(ApiTrackQueryService.class);

  private final ApiTrackRepository apiTrackRepository;

  private final ApiTrackMapper apiTrackMapper;

  public ApiTrackQueryService(ApiTrackRepository apiTrackRepository, ApiTrackMapper apiTrackMapper) {
    this.apiTrackRepository = apiTrackRepository;
    this.apiTrackMapper = apiTrackMapper;
  }

  /**
   * Return a {@link Page} of {@link ApiTrackDTO} which matches the criteria from the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @param page The page, which should be returned.
   * @return the matching entities.
   */
  @Transactional(readOnly = true)
  public Page<ApiTrackDTO> findByCriteria(ApiTrackCriteria criteria, Pageable page) {
    log.debug("find by criteria : {}, page: {}", criteria, page);
    final Specification<ApiTrack> specification = createSpecification(criteria);
    return apiTrackRepository.findAll(specification, page).map(apiTrackMapper::toDto);
  }

  /**
   * Return the number of matching entities in the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the number of matching entities.
   */
  @Transactional(readOnly = true)
  public long countByCriteria(ApiTrackCriteria criteria) {
    log.debug("count by criteria : {}", criteria);
    final Specification<ApiTrack> specification = createSpecification(criteria);
    return apiTrackRepository.count(specification);
  }

  /**
   * Function to convert {@link ApiTrackCriteria} to a {@link Specification}
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the matching {@link Specification} of the entity.
   */
  protected Specification<ApiTrack> createSpecification(ApiTrackCriteria criteria) {
    Specification<ApiTrack> specification = Specification.where(null);
    if (criteria != null) {
      // This has to be called first, because the distinct method returns null
      if (criteria.getDistinct() != null) {
        specification = specification.and(distinct(criteria.getDistinct()));
      }
      if (criteria.getId() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getId(), ApiTrack_.id));
      }
      if (criteria.getUuid() != null) {
        specification = specification.and(buildStringSpecification(criteria.getUuid(), ApiTrack_.uuid));
      }
      if (criteria.getTimestamp() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getTimestamp(), ApiTrack_.timestamp));
      }
      if (criteria.getSource() != null) {
        specification = specification.and(buildStringSpecification(criteria.getSource(), ApiTrack_.source));
      }
      if (criteria.getServiceId() != null) {
        specification = specification.and(buildStringSpecification(criteria.getServiceId(), ApiTrack_.serviceId));
      }
      if (criteria.getUri() != null) {
        specification = specification.and(buildStringSpecification(criteria.getUri(), ApiTrack_.uri));
      }
      if (criteria.getUrl() != null) {
        specification = specification.and(buildStringSpecification(criteria.getUrl(), ApiTrack_.url));
      }
      if (criteria.getStatusCode() != null) {
        specification = specification.and(buildStringSpecification(criteria.getStatusCode(), ApiTrack_.statusCode));
      }
      if (criteria.getRtt() != null) {
        specification = specification.and(buildStringSpecification(criteria.getRtt(), ApiTrack_.rtt));
      }
      if (criteria.getRequestMethod() != null) {
        specification = specification.and(buildStringSpecification(criteria.getRequestMethod(), ApiTrack_.requestMethod));
      }
      if (criteria.getAccessToken1() != null) {
        specification = specification.and(buildStringSpecification(criteria.getAccessToken1(), ApiTrack_.accessToken1));
      }
      if (criteria.getAccessToken2() != null) {
        specification = specification.and(buildStringSpecification(criteria.getAccessToken2(), ApiTrack_.accessToken2));
      }
      if (criteria.getJhiFrom() != null) {
        specification = specification.and(buildStringSpecification(criteria.getJhiFrom(), ApiTrack_.jhiFrom));
      }
      if (criteria.getJhiTo() != null) {
        specification = specification.and(buildStringSpecification(criteria.getJhiTo(), ApiTrack_.jhiTo));
      }
      if (criteria.getCost() != null) {
        specification = specification.and(buildStringSpecification(criteria.getCost(), ApiTrack_.cost));
      }
      if (criteria.getCharged() != null) {
        specification = specification.and(buildStringSpecification(criteria.getCharged(), ApiTrack_.charged));
      }
      if (criteria.getSynced() != null) {
        specification = specification.and(buildStringSpecification(criteria.getSynced(), ApiTrack_.synced));
      }
    }
    return specification;
  }
}
