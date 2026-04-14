package gov.moda.dw.manager.service;

import gov.moda.dw.manager.domain.*; // for static metamodels
import gov.moda.dw.manager.domain.VCDataStatusLog;
import gov.moda.dw.manager.repository.VCDataStatusLogRepository;
import gov.moda.dw.manager.service.criteria.VCDataStatusLogCriteria;
import gov.moda.dw.manager.service.dto.VCDataStatusLogDTO;
import gov.moda.dw.manager.service.mapper.VCDataStatusLogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link VCDataStatusLog} entities in
 * the database. The main input is a {@link VCDataStatusLogCriteria} which gets
 * converted to {@link Specification}, in a way that all the filters must apply.
 * It returns a {@link Page} of {@link VCDataStatusLogDTO} which fulfills the
 * criteria.
 */
@Service
@Transactional(readOnly = true)
public class VCDataStatusLogQueryService extends QueryService<VCDataStatusLog> {

    private static final Logger log = LoggerFactory.getLogger(VCDataStatusLogQueryService.class);

    private final VCDataStatusLogRepository vCDataStatusLogRepository;

    private final VCDataStatusLogMapper vCDataStatusLogMapper;

    public VCDataStatusLogQueryService(VCDataStatusLogRepository vCDataStatusLogRepository,
            VCDataStatusLogMapper vCDataStatusLogMapper) {
        this.vCDataStatusLogRepository = vCDataStatusLogRepository;
        this.vCDataStatusLogMapper = vCDataStatusLogMapper;
    }

    /**
     * Return a {@link Page} of {@link VCDataStatusLogDTO} which matches the
     * criteria from the database.
     * 
     * @param criteria The object which holds all the filters, which the entities
     *                 should match.
     * @param page     The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<VCDataStatusLogDTO> findByCriteria(VCDataStatusLogCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<VCDataStatusLog> specification = createSpecification(criteria);
        return vCDataStatusLogRepository.findAll(specification, page).map(vCDataStatusLogMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * 
     * @param criteria The object which holds all the filters, which the entities
     *                 should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(VCDataStatusLogCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<VCDataStatusLog> specification = createSpecification(criteria);
        return vCDataStatusLogRepository.count(specification);
    }

    /**
     * Function to convert {@link VCDataStatusLogCriteria} to a
     * {@link Specification}
     * 
     * @param criteria The object which holds all the filters, which the entities
     *                 should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<VCDataStatusLog> createSpecification(VCDataStatusLogCriteria criteria) {
        Specification<VCDataStatusLog> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), VCDataStatusLog_.id));
            }
            if (criteria.getVcCid() != null) {
                specification = specification
                        .and(buildStringSpecification(criteria.getVcCid(), VCDataStatusLog_.vcCid));
            }
            if (criteria.getVcItemId() != null) {
                specification = specification
                        .and(buildRangeSpecification(criteria.getVcItemId(), VCDataStatusLog_.vcItemId));
            }
            if (criteria.getTransactionId() != null) {
                specification = specification
                        .and(buildStringSpecification(criteria.getTransactionId(), VCDataStatusLog_.transactionId));
            }
            if (criteria.getValid() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getValid(), VCDataStatusLog_.valid));
            }
            if (criteria.getCrUser() != null) {
                specification = specification
                        .and(buildRangeSpecification(criteria.getCrUser(), VCDataStatusLog_.crUser));
            }
            if (criteria.getCrDatetime() != null) {
                specification = specification
                        .and(buildRangeSpecification(criteria.getCrDatetime(), VCDataStatusLog_.crDatetime));
            }
            if (criteria.getLastUpdateTime() != null) {
                specification = specification
                        .and(buildRangeSpecification(criteria.getLastUpdateTime(), VCDataStatusLog_.lastUpdateTime));
            }
        }
        return specification;
    }
}
