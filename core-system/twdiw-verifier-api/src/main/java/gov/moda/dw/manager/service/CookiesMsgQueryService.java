package gov.moda.dw.manager.service;

import gov.moda.dw.manager.domain.*; // for static metamodels
import gov.moda.dw.manager.domain.CookiesMsg;
import gov.moda.dw.manager.repository.CookiesMsgRepository;
import gov.moda.dw.manager.service.criteria.CookiesMsgCriteria;
import gov.moda.dw.manager.service.dto.CookiesMsgDTO;
import gov.moda.dw.manager.service.mapper.CookiesMsgMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link CookiesMsg} entities in the database.
 * The main input is a {@link CookiesMsgCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link CookiesMsgDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CookiesMsgQueryService extends QueryService<CookiesMsg> {

  private final Logger log = LoggerFactory.getLogger(CookiesMsgQueryService.class);

  private final CookiesMsgRepository cookiesMsgRepository;

  private final CookiesMsgMapper cookiesMsgMapper;

  public CookiesMsgQueryService(CookiesMsgRepository cookiesMsgRepository, CookiesMsgMapper cookiesMsgMapper) {
    this.cookiesMsgRepository = cookiesMsgRepository;
    this.cookiesMsgMapper = cookiesMsgMapper;
  }

  /**
   * Return a {@link Page} of {@link CookiesMsgDTO} which matches the criteria from the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @param page The page, which should be returned.
   * @return the matching entities.
   */
  @Transactional(readOnly = true)
  public Page<CookiesMsgDTO> findByCriteria(CookiesMsgCriteria criteria, Pageable page) {
    log.debug("find by criteria : {}, page: {}", criteria, page);
    final Specification<CookiesMsg> specification = createSpecification(criteria);
    return cookiesMsgRepository.findAll(specification, page).map(cookiesMsgMapper::toDto);
  }

  /**
   * Return the number of matching entities in the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the number of matching entities.
   */
  @Transactional(readOnly = true)
  public long countByCriteria(CookiesMsgCriteria criteria) {
    log.debug("count by criteria : {}", criteria);
    final Specification<CookiesMsg> specification = createSpecification(criteria);
    return cookiesMsgRepository.count(specification);
  }

  /**
   * Function to convert {@link CookiesMsgCriteria} to a {@link Specification}
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the matching {@link Specification} of the entity.
   */
  protected Specification<CookiesMsg> createSpecification(CookiesMsgCriteria criteria) {
    Specification<CookiesMsg> specification = Specification.where(null);
    if (criteria != null) {
      // This has to be called first, because the distinct method returns null
      if (criteria.getDistinct() != null) {
        specification = specification.and(distinct(criteria.getDistinct()));
      }
      if (criteria.getId() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getId(), CookiesMsg_.id));
      }
      if (criteria.getCid() != null) {
        specification = specification.and(buildStringSpecification(criteria.getCid(), CookiesMsg_.cid));
      }
      if (criteria.getMsg() != null) {
        specification = specification.and(buildStringSpecification(criteria.getMsg(), CookiesMsg_.msg));
      }
    }
    return specification;
  }
}
