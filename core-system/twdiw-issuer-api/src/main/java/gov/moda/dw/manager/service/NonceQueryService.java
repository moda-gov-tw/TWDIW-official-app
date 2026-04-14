package gov.moda.dw.manager.service;

import gov.moda.dw.manager.domain.*; // for static metamodels
import gov.moda.dw.manager.domain.Nonce;
import gov.moda.dw.manager.repository.NonceRepository;
import gov.moda.dw.manager.service.criteria.NonceCriteria;
import gov.moda.dw.manager.service.dto.NonceDTO;
import gov.moda.dw.manager.service.mapper.NonceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Nonce} entities in the database.
 * The main input is a {@link NonceCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link NonceDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class NonceQueryService extends QueryService<Nonce> {

  private final Logger log = LoggerFactory.getLogger(NonceQueryService.class);

  private final NonceRepository nonceRepository;

  private final NonceMapper nonceMapper;

  public NonceQueryService(NonceRepository nonceRepository, NonceMapper nonceMapper) {
    this.nonceRepository = nonceRepository;
    this.nonceMapper = nonceMapper;
  }

  /**
   * Return a {@link Page} of {@link NonceDTO} which matches the criteria from the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @param page The page, which should be returned.
   * @return the matching entities.
   */
  @Transactional(readOnly = true)
  public Page<NonceDTO> findByCriteria(NonceCriteria criteria, Pageable page) {
    log.debug("find by criteria : {}, page: {}", criteria, page);
    final Specification<Nonce> specification = createSpecification(criteria);
    return nonceRepository.findAll(specification, page).map(nonceMapper::toDto);
  }

  /**
   * Return the number of matching entities in the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the number of matching entities.
   */
  @Transactional(readOnly = true)
  public long countByCriteria(NonceCriteria criteria) {
    log.debug("count by criteria : {}", criteria);
    final Specification<Nonce> specification = createSpecification(criteria);
    return nonceRepository.count(specification);
  }

  /**
   * Function to convert {@link NonceCriteria} to a {@link Specification}
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the matching {@link Specification} of the entity.
   */
  protected Specification<Nonce> createSpecification(NonceCriteria criteria) {
    Specification<Nonce> specification = Specification.where(null);
    if (criteria != null) {
      // This has to be called first, because the distinct method returns null
      if (criteria.getDistinct() != null) {
        specification = specification.and(distinct(criteria.getDistinct()));
      }
      if (criteria.getId() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getId(), Nonce_.id));
      }
      if (criteria.getsId() != null) {
        specification = specification.and(buildStringSpecification(criteria.getsId(), Nonce_.sId));
      }
      if (criteria.getNonceId() != null) {
        specification = specification.and(buildStringSpecification(criteria.getNonceId(), Nonce_.nonceId));
      }
      if (criteria.getUserId() != null) {
        specification = specification.and(buildStringSpecification(criteria.getUserId(), Nonce_.userId));
      }
      if (criteria.getCreateTime() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getCreateTime(), Nonce_.createTime));
      }
      if (criteria.getCaptchaCode() != null) {
        specification = specification.and(buildStringSpecification(criteria.getCaptchaCode(), Nonce_.captchaCode));
      }
    }
    return specification;
  }
}
