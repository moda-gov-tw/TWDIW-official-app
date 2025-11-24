package gov.moda.dw.issuer.oidvci.service;

import java.util.List;
import gov.moda.dw.issuer.oidvci.domain.*; // for static metamodels
import gov.moda.dw.issuer.oidvci.domain.AccessToken;
import gov.moda.dw.issuer.oidvci.repository.AccessTokenApiRepository;
import gov.moda.dw.issuer.oidvci.service.criteria.AccessTokenCriteria;
import gov.moda.dw.issuer.oidvci.service.dto.AccessTokenDTO;
import gov.moda.dw.issuer.oidvci.service.mapper.AccessTokenMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link AccessToken} entities in the database.
 * The main input is a {@link AccessTokenCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link AccessTokenDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AccessTokenQueryService extends QueryService<AccessToken> {

  private final Logger log = LoggerFactory.getLogger(AccessTokenQueryService.class);

  private final AccessTokenApiRepository accessTokenApiRepository;

  private final AccessTokenMapper accessTokenMapper;

  public AccessTokenQueryService(AccessTokenApiRepository accessTokenApiRepository, AccessTokenMapper accessTokenMapper) {
    this.accessTokenApiRepository = accessTokenApiRepository;
    this.accessTokenMapper = accessTokenMapper;
  }

  /**
   * Return a {@link Page} of {@link AccessTokenDTO} which matches the criteria from the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @param page The page, which should be returned.
   * @return the matching entities.
   */
  @Transactional(readOnly = true)
  public Page<AccessTokenDTO> findByCriteria(AccessTokenCriteria criteria, Pageable page) {
    log.debug("find by criteria : {}, page: {}", criteria, page);
    final Specification<AccessToken> specification = createSpecification(criteria);
    return accessTokenApiRepository.findAll(specification, page).map(accessTokenMapper::toDto);
  }

  /**
   * Return a {@link List} of {@link AccessTokenDTO} which matches the criteria from the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the matching entities.
   */
  @Transactional
  public List<AccessTokenDTO> findByCriteria(AccessTokenCriteria criteria) {
    log.debug("find by criteria : {}", criteria);
    final Specification<AccessToken> specification = createSpecification(criteria);
    return accessTokenMapper.toDto(accessTokenApiRepository.findAll(specification));
  }

  /**
   * Return the number of matching entities in the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the number of matching entities.
   */
  @Transactional(readOnly = true)
  public long countByCriteria(AccessTokenCriteria criteria) {
    log.debug("count by criteria : {}", criteria);
    final Specification<AccessToken> specification = createSpecification(criteria);
    return accessTokenApiRepository.count(specification);
  }

  /**
   * Function to convert {@link AccessTokenCriteria} to a {@link Specification}
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the matching {@link Specification} of the entity.
   */
  protected Specification<AccessToken> createSpecification(AccessTokenCriteria criteria) {
    Specification<AccessToken> specification = Specification.where(null);
    if (criteria != null) {
      // This has to be called first, because the distinct method returns null
      if (criteria.getDistinct() != null) {
        specification = specification.and(distinct(criteria.getDistinct()));
      }
      if (criteria.getId() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getId(), AccessToken_.id));
      }
      if (criteria.getAccessToken() != null) {
        specification = specification.and(buildStringSpecification(criteria.getAccessToken(), AccessToken_.accessToken));
      }
      if (criteria.getAccessTokenName() != null) {
        specification = specification.and(buildStringSpecification(criteria.getAccessTokenName(), AccessToken_.accessTokenName));
      }
      if (criteria.getOwner() != null) {
        specification = specification.and(buildStringSpecification(criteria.getOwner(), AccessToken_.owner));
      }
      if (criteria.getOwnerName() != null) {
        specification = specification.and(buildStringSpecification(criteria.getOwnerName(), AccessToken_.ownerName));
      }
      if (criteria.getOrgId() != null) {
        specification = specification.and(buildStringSpecification(criteria.getOrgId(), AccessToken_.orgId));
      }
      if (criteria.getOrgName() != null) {
        specification = specification.and(buildStringSpecification(criteria.getOrgName(), AccessToken_.orgName));
      }
      if (criteria.getState() != null) {
        specification = specification.and(buildStringSpecification(criteria.getState(), AccessToken_.state));
      }
      if (criteria.getActype() != null) {
        specification = specification.and(buildStringSpecification(criteria.getActype(), AccessToken_.actype));
      }
      if (criteria.getDataRole1() != null) {
        specification = specification.and(buildStringSpecification(criteria.getDataRole1(), AccessToken_.dataRole1));
      }
      if (criteria.getDataRole2() != null) {
        specification = specification.and(buildStringSpecification(criteria.getDataRole2(), AccessToken_.dataRole2));
      }
      if (criteria.getSecuLayer() != null) {
        specification = specification.and(buildStringSpecification(criteria.getSecuLayer(), AccessToken_.secuLayer));
      }
      if (criteria.getExpirationTime() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getExpirationTime(), AccessToken_.expirationTime));
      }
      if (criteria.getCreateTime() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getCreateTime(), AccessToken_.createTime));
      }
    }
    return specification;
  }
}
