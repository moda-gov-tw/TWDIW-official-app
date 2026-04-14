package gov.moda.dw.manager.service;

import gov.moda.dw.manager.domain.*; // for static metamodels
import gov.moda.dw.manager.domain.Field;
import gov.moda.dw.manager.repository.FieldRepository;
import gov.moda.dw.manager.service.criteria.FieldCriteria;
import gov.moda.dw.manager.service.custom.CustomQueryService;
import gov.moda.dw.manager.service.dto.FieldDTO;
import gov.moda.dw.manager.service.mapper.FieldMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for executing complex queries for {@link Field} entities in the database.
 * The main input is a {@link FieldCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link FieldDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class FieldQueryService extends CustomQueryService<Field> {

    private static final Logger log = LoggerFactory.getLogger(FieldQueryService.class);

    private final FieldRepository fieldRepository;

    private final FieldMapper fieldMapper;

    public FieldQueryService(FieldRepository fieldRepository, FieldMapper fieldMapper) {
        this.fieldRepository = fieldRepository;
        this.fieldMapper = fieldMapper;
    }

    /**
     * Return a {@link Page} of {@link FieldDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<FieldDTO> findByCriteria(FieldCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Field> specification = createSpecification(criteria);
        return fieldRepository.findAll(specification, page).map(fieldMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(FieldCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Field> specification = createSpecification(criteria);
        return fieldRepository.count(specification);
    }

    /**
     * Function to convert {@link FieldCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Field> createSpecification(FieldCriteria criteria) {
        Specification<Field> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Field_.id));
            }
            if (criteria.getCname() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCname(), Field_.cname));
            }
            if (criteria.getEname() != null) {
                specification = specification.and(buildStringSpecification(criteria.getEname(), Field_.ename));
            }
            if (criteria.getType() != null) {
                specification = specification.and(buildStringSpecification(criteria.getType(), Field_.type));
            }
            if (criteria.getVisible() != null) {
                specification = specification.and(buildSpecification(criteria.getVisible(), Field_.visible));
            }
            if (criteria.getBusinessId() != null) {
                specification = specification.and(buildStringSpecification(criteria.getBusinessId(), Field_.businessId));
            }
        }
        return specification;
    }
}
