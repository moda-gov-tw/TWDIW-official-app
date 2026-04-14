package gov.moda.dw.manager.service;

import gov.moda.dw.manager.domain.*; // for static metamodels
import gov.moda.dw.manager.domain.VPItemField;
import gov.moda.dw.manager.repository.VPItemFieldRepository;
import gov.moda.dw.manager.service.criteria.VPItemFieldCriteria;
import gov.moda.dw.manager.service.dto.VPItemFieldDTO;
import gov.moda.dw.manager.service.mapper.VPItemFieldMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link VPItemField} entities in the database.
 * The main input is a {@link VPItemFieldCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link VPItemFieldDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class VPItemFieldQueryService extends QueryService<VPItemField> {

    private static final Logger log = LoggerFactory.getLogger(VPItemFieldQueryService.class);

    private final VPItemFieldRepository vPItemFieldRepository;

    private final VPItemFieldMapper vPItemFieldMapper;

    public VPItemFieldQueryService(VPItemFieldRepository vPItemFieldRepository, VPItemFieldMapper vPItemFieldMapper) {
        this.vPItemFieldRepository = vPItemFieldRepository;
        this.vPItemFieldMapper = vPItemFieldMapper;
    }

    /**
     * Return a {@link Page} of {@link VPItemFieldDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<VPItemFieldDTO> findByCriteria(VPItemFieldCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<VPItemField> specification = createSpecification(criteria);
        return vPItemFieldRepository.findAll(specification, page).map(vPItemFieldMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(VPItemFieldCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<VPItemField> specification = createSpecification(criteria);
        return vPItemFieldRepository.count(specification);
    }

    /**
     * Function to convert {@link VPItemFieldCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<VPItemField> createSpecification(VPItemFieldCriteria criteria) {
        Specification<VPItemField> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), VPItemField_.id));
            }
            if (criteria.getVpItemId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getVpItemId(), VPItemField_.vpItemId));
            }
            if (criteria.getVcCategoryDescription() != null) {
                specification = specification.and(
                    buildStringSpecification(criteria.getVcCategoryDescription(), VPItemField_.vcCategoryDescription)
                );
            }
            if (criteria.getVcName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getVcName(), VPItemField_.vcName));
            }
            if (criteria.getVcItemFieldType() != null) {
                specification = specification.and(buildStringSpecification(criteria.getVcItemFieldType(), VPItemField_.vcItemFieldType));
            }
            if (criteria.getCname() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCname(), VPItemField_.cname));
            }
            if (criteria.getEname() != null) {
                specification = specification.and(buildStringSpecification(criteria.getEname(), VPItemField_.ename));
            }
            if (criteria.getVcSerialNo() != null) {
                specification = specification.and(buildStringSpecification(criteria.getVcSerialNo(), VPItemField_.vcSerialNo));
            }
            if (criteria.getVcBusinessId() != null) {
                specification = specification.and(buildStringSpecification(criteria.getVcBusinessId(), VPItemField_.vcBusinessId));
            }
            if (criteria.getVpBusinessId() != null) {
                specification = specification.and(buildStringSpecification(criteria.getVpBusinessId(), VPItemField_.vpBusinessId));
            }
            if (criteria.getIsRequired() != null) {
                specification = specification.and(buildSpecification(criteria.getIsRequired(), VPItemField_.isRequired));
            }
        }
        return specification;
    }
}
