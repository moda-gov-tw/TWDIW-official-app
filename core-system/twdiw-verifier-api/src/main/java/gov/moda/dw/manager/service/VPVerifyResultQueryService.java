package gov.moda.dw.manager.service;

import gov.moda.dw.manager.domain.*; // for static metamodels
import gov.moda.dw.manager.domain.VPVerifyResult;
import gov.moda.dw.manager.repository.VPVerifyResultRepository;
import gov.moda.dw.manager.service.criteria.VPVerifyResultCriteria;
import gov.moda.dw.manager.service.dto.VPVerifyResultDTO;
import gov.moda.dw.manager.service.mapper.VPVerifyResultMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link VPVerifyResult} entities in the database.
 * The main input is a {@link VPVerifyResultCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link VPVerifyResultDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class VPVerifyResultQueryService extends QueryService<VPVerifyResult> {

    private static final Logger log = LoggerFactory.getLogger(VPVerifyResultQueryService.class);

    private final VPVerifyResultRepository vPVerifyResultRepository;

    private final VPVerifyResultMapper vPVerifyResultMapper;

    public VPVerifyResultQueryService(VPVerifyResultRepository vPVerifyResultRepository, VPVerifyResultMapper vPVerifyResultMapper) {
        this.vPVerifyResultRepository = vPVerifyResultRepository;
        this.vPVerifyResultMapper = vPVerifyResultMapper;
    }

    /**
     * Return a {@link Page} of {@link VPVerifyResultDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<VPVerifyResultDTO> findByCriteria(VPVerifyResultCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<VPVerifyResult> specification = createSpecification(criteria);
        return vPVerifyResultRepository.findAll(specification, page).map(vPVerifyResultMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(VPVerifyResultCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<VPVerifyResult> specification = createSpecification(criteria);
        return vPVerifyResultRepository.count(specification);
    }

    /**
     * Function to convert {@link VPVerifyResultCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<VPVerifyResult> createSpecification(VPVerifyResultCriteria criteria) {
        Specification<VPVerifyResult> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), VPVerifyResult_.id));
            }
            if (criteria.getTransactionId() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTransactionId(), VPVerifyResult_.transactionId));
            }
            if (criteria.getVpItemId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getVpItemId(), VPVerifyResult_.vpItemId));
            }
            if (criteria.getCrDatetime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCrDatetime(), VPVerifyResult_.crDatetime));
            }
            if (criteria.getVerifyDatetime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getVerifyDatetime(), VPVerifyResult_.verifyDatetime));
            }
        }
        return specification;
    }
}
