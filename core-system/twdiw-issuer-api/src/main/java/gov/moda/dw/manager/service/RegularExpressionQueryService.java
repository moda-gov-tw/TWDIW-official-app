package gov.moda.dw.manager.service;

import gov.moda.dw.manager.domain.*; // for static metamodels
import gov.moda.dw.manager.domain.RegularExpression;
import gov.moda.dw.manager.repository.RegularExpressionRepository;
import gov.moda.dw.manager.service.criteria.RegularExpressionCriteria;
import gov.moda.dw.manager.service.custom.CustomQueryService;
import gov.moda.dw.manager.service.dto.RegularExpressionDTO;
import gov.moda.dw.manager.service.mapper.RegularExpressionMapper;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for executing complex queries for {@link RegularExpression} entities in the database.
 * The main input is a {@link RegularExpressionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link RegularExpression} or a {@link Page} of {@link RegularExpression} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class RegularExpressionQueryService extends CustomQueryService<RegularExpression> {

    private final Logger log = LoggerFactory.getLogger(RegularExpressionQueryService.class);

    private final RegularExpressionRepository regularExpressionRepository;

    private final RegularExpressionMapper regularExpressionMapper;

    public RegularExpressionQueryService(
        RegularExpressionRepository regularExpressionRepository,
        RegularExpressionMapper regularExpressionMapper
    ) {
        this.regularExpressionRepository = regularExpressionRepository;
        this.regularExpressionMapper = regularExpressionMapper;
    }

    /**
     * Return a {@link List} of {@link RegularExpression} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<RegularExpressionDTO> findByCriteria(RegularExpressionCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<RegularExpression> specification = createSpecification(criteria);
        return regularExpressionMapper.toDto(regularExpressionRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link RegularExpression} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<RegularExpressionDTO> findByCriteria(RegularExpressionCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<RegularExpression> specification = createSpecification(criteria);
        return regularExpressionRepository.findAll(specification, page).map(regularExpressionMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(RegularExpressionCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<RegularExpression> specification = createSpecification(criteria);
        return regularExpressionRepository.count(specification);
    }

    /**
     * Function to convert {@link RegularExpressionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<RegularExpression> createSpecification(RegularExpressionCriteria criteria) {
        Specification<RegularExpression> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), RegularExpression_.id));
            }
            if (criteria.getType() != null) {
                specification = specification.and(buildStringSpecification(criteria.getType(), RegularExpression_.type));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), RegularExpression_.name));
            }
            if (criteria.getRegularExpression() != null) {
                specification = specification.and(
                    buildStringSpecification(criteria.getRegularExpression(), RegularExpression_.regularExpression)
                );
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), RegularExpression_.description));
            }
            if (criteria.getErrorMsg() != null) {
                specification = specification.and(buildStringSpecification(criteria.getErrorMsg(), RegularExpression_.errorMsg));
            }
            if (criteria.getRuleType() != null) {
                specification = specification.and(buildStringSpecification(criteria.getRuleType(), RegularExpression_.ruleType));
            }
        }
        return specification;
    }
}
