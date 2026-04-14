package gov.moda.dw.manager.service;

import gov.moda.dw.manager.domain.*; // for static metamodels
import gov.moda.dw.manager.domain.ExtendedUser;
import gov.moda.dw.manager.repository.ExtendedUserRepository;
import gov.moda.dw.manager.service.criteria.ExtendedUserCriteria;
import gov.moda.dw.manager.service.dto.ExtendedUserDTO;
import gov.moda.dw.manager.service.mapper.ExtendedUserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link ExtendedUser} entities in the database.
 * The main input is a {@link ExtendedUserCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ExtendedUserDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ExtendedUserQueryService extends QueryService<ExtendedUser> {

    private final Logger log = LoggerFactory.getLogger(ExtendedUserQueryService.class);

    private final ExtendedUserRepository extendedUserRepository;

    private final ExtendedUserMapper extendedUserMapper;

    public ExtendedUserQueryService(ExtendedUserRepository extendedUserRepository, ExtendedUserMapper extendedUserMapper) {
        this.extendedUserRepository = extendedUserRepository;
        this.extendedUserMapper = extendedUserMapper;
    }

    /**
     * Return a {@link Page} of {@link ExtendedUserDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ExtendedUserDTO> findByCriteria(ExtendedUserCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ExtendedUser> specification = createSpecification(criteria);
        return extendedUserRepository.findAll(specification, page).map(extendedUserMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ExtendedUserCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<ExtendedUser> specification = createSpecification(criteria);
        return extendedUserRepository.count(specification);
    }

    /**
     * Function to convert {@link ExtendedUserCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ExtendedUser> createSpecification(ExtendedUserCriteria criteria) {
        Specification<ExtendedUser> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), ExtendedUser_.id));
            }
            if (criteria.getOrgId() != null) {
                specification = specification.and(buildStringSpecification(criteria.getOrgId(), ExtendedUser_.orgId));
            }
            if (criteria.getUserId() != null) {
                specification = specification.and(buildStringSpecification(criteria.getUserId(), ExtendedUser_.userId));
            }
            if (criteria.getUserName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getUserName(), ExtendedUser_.userName));
            }
            if (criteria.getEmail() != null) {
                specification = specification.and(buildStringSpecification(criteria.getEmail(), ExtendedUser_.email));
            }
            if (criteria.getPhone() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPhone(), ExtendedUser_.phone));
            }
            if (criteria.getTel() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTel(), ExtendedUser_.tel));
            }
            if (criteria.getEmployeeId() != null) {
                specification = specification.and(buildStringSpecification(criteria.getEmployeeId(), ExtendedUser_.employeeId));
            }
            if (criteria.getEmployeeTypeId() != null) {
                specification = specification.and(buildStringSpecification(criteria.getEmployeeTypeId(), ExtendedUser_.employeeTypeId));
            }
            if (criteria.getLeftDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLeftDate(), ExtendedUser_.leftDate));
            }
            if (criteria.getOnboardDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getOnboardDate(), ExtendedUser_.onboardDate));
            }
            if (criteria.getUserTypeId() != null) {
                specification = specification.and(buildStringSpecification(criteria.getUserTypeId(), ExtendedUser_.userTypeId));
            }
            if (criteria.getDataRole1() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDataRole1(), ExtendedUser_.dataRole1));
            }
            if (criteria.getDataRole2() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDataRole2(), ExtendedUser_.dataRole2));
            }
            if (criteria.getState() != null) {
                specification = specification.and(buildStringSpecification(criteria.getState(), ExtendedUser_.state));
            }
            if (criteria.getCreateTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreateTime(), ExtendedUser_.createTime));
            }
            if (criteria.getAuthChangeTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getAuthChangeTime(), ExtendedUser_.authChangeTime));
            }
            if (criteria.getPwdResetTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPwdResetTime(), ExtendedUser_.pwdResetTime));
            }
        }
        return specification;
    }
}
