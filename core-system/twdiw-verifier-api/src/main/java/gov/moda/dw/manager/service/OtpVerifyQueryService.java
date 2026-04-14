package gov.moda.dw.manager.service;

import gov.moda.dw.manager.domain.*; // for static metamodels
import gov.moda.dw.manager.domain.OtpVerify;
import gov.moda.dw.manager.repository.OtpVerifyRepository;
import gov.moda.dw.manager.service.criteria.OtpVerifyCriteria;
import gov.moda.dw.manager.service.dto.OtpVerifyDTO;
import gov.moda.dw.manager.service.mapper.OtpVerifyMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link OtpVerify} entities in the
 * database. The main input is a {@link OtpVerifyCriteria} which gets converted
 * to {@link Specification}, in a way that all the filters must apply. It
 * returns a {@link Page} of {@link OtpVerifyDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class OtpVerifyQueryService extends QueryService<OtpVerify> {

    private static final Logger LOG = LoggerFactory.getLogger(OtpVerifyQueryService.class);

    private final OtpVerifyRepository otpVerifyRepository;

    private final OtpVerifyMapper otpVerifyMapper;

    public OtpVerifyQueryService(OtpVerifyRepository otpVerifyRepository, OtpVerifyMapper otpVerifyMapper) {
        this.otpVerifyRepository = otpVerifyRepository;
        this.otpVerifyMapper = otpVerifyMapper;
    }

    /**
     * Return a {@link Page} of {@link OtpVerifyDTO} which matches the criteria from
     * the database.
     * 
     * @param criteria The object which holds all the filters, which the entities
     *                 should match.
     * @param page     The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<OtpVerifyDTO> findByCriteria(OtpVerifyCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<OtpVerify> specification = createSpecification(criteria);
        return otpVerifyRepository.findAll(specification, page).map(otpVerifyMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * 
     * @param criteria The object which holds all the filters, which the entities
     *                 should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(OtpVerifyCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<OtpVerify> specification = createSpecification(criteria);
        return otpVerifyRepository.count(specification);
    }

    /**
     * Function to convert {@link OtpVerifyCriteria} to a {@link Specification}
     * 
     * @param criteria The object which holds all the filters, which the entities
     *                 should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<OtpVerify> createSpecification(OtpVerifyCriteria criteria) {
        Specification<OtpVerify> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), OtpVerify_.id));
            }
            if (criteria.getEmail() != null) {
                specification = specification.and(buildStringSpecification(criteria.getEmail(), OtpVerify_.email));
            }
            if (criteria.getOtpToken() != null) {
                specification = specification
                        .and(buildStringSpecification(criteria.getOtpToken(), OtpVerify_.otpToken));
            }
            if (criteria.getOtpTokenExpired() != null) {
                specification = specification
                        .and(buildRangeSpecification(criteria.getOtpTokenExpired(), OtpVerify_.otpTokenExpired));
            }
            if (criteria.getIsPass() != null) {
                specification = specification.and(buildSpecification(criteria.getIsPass(), OtpVerify_.isPass));
            }
            if (criteria.getCreateTime() != null) {
                specification = specification
                        .and(buildRangeSpecification(criteria.getCreateTime(), OtpVerify_.createTime));
            }
        }
        return specification;
    }
}
