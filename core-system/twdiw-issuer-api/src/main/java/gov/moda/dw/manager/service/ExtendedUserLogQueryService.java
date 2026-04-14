package gov.moda.dw.manager.service;

import gov.moda.dw.manager.domain.*; // for static metamodels
import gov.moda.dw.manager.domain.ExtendedUserLog;
import gov.moda.dw.manager.repository.ExtendedUserLogRepository;
import gov.moda.dw.manager.service.criteria.ExtendedUserLogCriteria;
import gov.moda.dw.manager.service.dto.ExtendedUserLogDTO;
import gov.moda.dw.manager.service.mapper.ExtendedUserLogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link ExtendedUserLog} entities in the database.
 * The main input is a {@link ExtendedUserLogCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ExtendedUserLogDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ExtendedUserLogQueryService extends QueryService<ExtendedUserLog> {

    private final Logger log = LoggerFactory.getLogger(ExtendedUserLogQueryService.class);

    private final ExtendedUserLogRepository extendedUserLogRepository;

    private final ExtendedUserLogMapper extendedUserLogMapper;

    public ExtendedUserLogQueryService(ExtendedUserLogRepository extendedUserLogRepository, ExtendedUserLogMapper extendedUserLogMapper) {
        this.extendedUserLogRepository = extendedUserLogRepository;
        this.extendedUserLogMapper = extendedUserLogMapper;
    }

    /**
     * Return a {@link Page} of {@link ExtendedUserLogDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ExtendedUserLogDTO> findByCriteria(ExtendedUserLogCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ExtendedUserLog> specification = createSpecification(criteria);
        return extendedUserLogRepository.findAll(specification, page).map(extendedUserLogMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ExtendedUserLogCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<ExtendedUserLog> specification = createSpecification(criteria);
        return extendedUserLogRepository.count(specification);
    }

    /**
     * Function to convert {@link ExtendedUserLogCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ExtendedUserLog> createSpecification(ExtendedUserLogCriteria criteria) {
        Specification<ExtendedUserLog> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), ExtendedUserLog_.id));
            }
            if (criteria.getActor() != null) {
                specification = specification.and(buildStringSpecification(criteria.getActor(), ExtendedUserLog_.actor));
            }
            if (criteria.getLogType() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLogType(), ExtendedUserLog_.logType));
            }
            if (criteria.getLogTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLogTime(), ExtendedUserLog_.logTime));
            }
            if (criteria.getOrgId() != null) {
                specification = specification.and(buildStringSpecification(criteria.getOrgId(), ExtendedUserLog_.orgId));
            }
            if (criteria.getUserId() != null) {
                specification = specification.and(buildStringSpecification(criteria.getUserId(), ExtendedUserLog_.userId));
            }
            if (criteria.getUserName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getUserName(), ExtendedUserLog_.userName));
            }
            if (criteria.getEmail() != null) {
                specification = specification.and(buildStringSpecification(criteria.getEmail(), ExtendedUserLog_.email));
            }
            if (criteria.getPhone() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPhone(), ExtendedUserLog_.phone));
            }
            if (criteria.getTel() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTel(), ExtendedUserLog_.tel));
            }
            if (criteria.getEmployeeId() != null) {
                specification = specification.and(buildStringSpecification(criteria.getEmployeeId(), ExtendedUserLog_.employeeId));
            }
            if (criteria.getEmployeeTypeId() != null) {
                specification = specification.and(buildStringSpecification(criteria.getEmployeeTypeId(), ExtendedUserLog_.employeeTypeId));
            }
            if (criteria.getLeftDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLeftDate(), ExtendedUserLog_.leftDate));
            }
            if (criteria.getOnboardDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getOnboardDate(), ExtendedUserLog_.onboardDate));
            }
            if (criteria.getUserTypeId() != null) {
                specification = specification.and(buildStringSpecification(criteria.getUserTypeId(), ExtendedUserLog_.userTypeId));
            }
            if (criteria.getDataRole1() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDataRole1(), ExtendedUserLog_.dataRole1));
            }
            if (criteria.getDataRole2() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDataRole2(), ExtendedUserLog_.dataRole2));
            }
            if (criteria.getState() != null) {
                specification = specification.and(buildStringSpecification(criteria.getState(), ExtendedUserLog_.state));
            }
            if (criteria.getCreateTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreateTime(), ExtendedUserLog_.createTime));
            }
            if (criteria.getAuthChangeTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getAuthChangeTime(), ExtendedUserLog_.authChangeTime));
            }
            if (criteria.getPwdResetTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPwdResetTime(), ExtendedUserLog_.pwdResetTime));
            }
        }
        return specification;
    }
}
