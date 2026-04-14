package gov.moda.dw.manager.service;

import gov.moda.dw.manager.domain.*; // for static metamodels
import gov.moda.dw.manager.domain.ImgVerifyCode;
import gov.moda.dw.manager.repository.ImgVerifyCodeRepository;
import gov.moda.dw.manager.service.criteria.ImgVerifyCodeCriteria;
import gov.moda.dw.manager.service.dto.ImgVerifyCodeDTO;
import gov.moda.dw.manager.service.mapper.ImgVerifyCodeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link ImgVerifyCode} entities in the database.
 * The main input is a {@link ImgVerifyCodeCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ImgVerifyCodeDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ImgVerifyCodeQueryService extends QueryService<ImgVerifyCode> {

  private final Logger log = LoggerFactory.getLogger(ImgVerifyCodeQueryService.class);

  private final ImgVerifyCodeRepository imgVerifyCodeRepository;

  private final ImgVerifyCodeMapper imgVerifyCodeMapper;

  public ImgVerifyCodeQueryService(ImgVerifyCodeRepository imgVerifyCodeRepository, ImgVerifyCodeMapper imgVerifyCodeMapper) {
    this.imgVerifyCodeRepository = imgVerifyCodeRepository;
    this.imgVerifyCodeMapper = imgVerifyCodeMapper;
  }

  /**
   * Return a {@link Page} of {@link ImgVerifyCodeDTO} which matches the criteria from the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @param page The page, which should be returned.
   * @return the matching entities.
   */
  @Transactional(readOnly = true)
  public Page<ImgVerifyCodeDTO> findByCriteria(ImgVerifyCodeCriteria criteria, Pageable page) {
    log.debug("find by criteria : {}, page: {}", criteria, page);
    final Specification<ImgVerifyCode> specification = createSpecification(criteria);
    return imgVerifyCodeRepository.findAll(specification, page).map(imgVerifyCodeMapper::toDto);
  }

  /**
   * Return the number of matching entities in the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the number of matching entities.
   */
  @Transactional(readOnly = true)
  public long countByCriteria(ImgVerifyCodeCriteria criteria) {
    log.debug("count by criteria : {}", criteria);
    final Specification<ImgVerifyCode> specification = createSpecification(criteria);
    return imgVerifyCodeRepository.count(specification);
  }

  /**
   * Function to convert {@link ImgVerifyCodeCriteria} to a {@link Specification}
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the matching {@link Specification} of the entity.
   */
  protected Specification<ImgVerifyCode> createSpecification(ImgVerifyCodeCriteria criteria) {
    Specification<ImgVerifyCode> specification = Specification.where(null);
    if (criteria != null) {
      // This has to be called first, because the distinct method returns null
      if (criteria.getDistinct() != null) {
        specification = specification.and(distinct(criteria.getDistinct()));
      }
      if (criteria.getId() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getId(), ImgVerifyCode_.id));
      }
      if (criteria.getVerifyCode() != null) {
        specification = specification.and(buildStringSpecification(criteria.getVerifyCode(), ImgVerifyCode_.verifyCode));
      }
      if (criteria.getVerifyUniId() != null) {
        specification = specification.and(buildStringSpecification(criteria.getVerifyUniId(), ImgVerifyCode_.verifyUniId));
      }
      if (criteria.getCreateTime() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getCreateTime(), ImgVerifyCode_.createTime));
      }
      if (criteria.getExpireTime() != null) {
        specification = specification.and(buildRangeSpecification(criteria.getExpireTime(), ImgVerifyCode_.expireTime));
      }
    }
    return specification;
  }
}
