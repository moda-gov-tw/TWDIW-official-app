package gov.moda.dw.manager.service;

import gov.moda.dw.manager.domain.*; // for static metamodels
import gov.moda.dw.manager.domain.VCItemField;
import gov.moda.dw.manager.repository.VCItemFieldRepository;
import gov.moda.dw.manager.repository.custom.CustomVCItemFieldRepository;
import gov.moda.dw.manager.service.criteria.VCItemFieldCriteria;
import gov.moda.dw.manager.service.dto.VCItemFieldDTO;
import gov.moda.dw.manager.service.mapper.VCItemFieldMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link VCItemField} entities in the database.
 * The main input is a {@link VCItemFieldCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link VCItemFieldDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class VCItemFieldQueryService extends QueryService<VCItemField> {

    private static final Logger log = LoggerFactory.getLogger(VCItemFieldQueryService.class);

    private final CustomVCItemFieldRepository vCItemFieldRepository;

    private final VCItemFieldMapper vCItemFieldMapper;

    public VCItemFieldQueryService(CustomVCItemFieldRepository vCItemFieldRepository, VCItemFieldMapper vCItemFieldMapper) {
        this.vCItemFieldRepository = vCItemFieldRepository;
        this.vCItemFieldMapper = vCItemFieldMapper;
    }

    /**
     * Return a {@link Page} of {@link VCItemFieldDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<VCItemFieldDTO> findByCriteria(VCItemFieldCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<VCItemField> specification = createSpecification(criteria);
        return vCItemFieldRepository.findAll(specification, page).map(vCItemFieldMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(VCItemFieldCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<VCItemField> specification = createSpecification(criteria);
        return vCItemFieldRepository.count(specification);
    }

    /**
     * Function to convert {@link VCItemFieldCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<VCItemField> createSpecification(VCItemFieldCriteria criteria) {
        Specification<VCItemField> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), VCItemField_.id));
            }
            if (criteria.getVcItemId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getVcItemId(), VCItemField_.vcItemId));
            }
            if (criteria.getType() != null) {
                specification = specification.and(buildStringSpecification(criteria.getType(), VCItemField_.type));
            }
            if (criteria.getCname() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCname(), VCItemField_.cname));
            }
            if (criteria.getEname() != null) {
                specification = specification.and(buildStringSpecification(criteria.getEname(), VCItemField_.ename));
            }
        }
        return specification;
    }
}
