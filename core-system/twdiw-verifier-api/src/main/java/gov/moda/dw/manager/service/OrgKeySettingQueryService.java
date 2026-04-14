package gov.moda.dw.manager.service;

import gov.moda.dw.manager.domain.*; // for static metamodels
import gov.moda.dw.manager.domain.OrgKeySetting;
import gov.moda.dw.manager.repository.OrgKeySettingRepository;
import gov.moda.dw.manager.service.criteria.OrgKeySettingCriteria;
import gov.moda.dw.manager.service.dto.OrgKeySettingDTO;
import gov.moda.dw.manager.service.mapper.OrgKeySettingMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link OrgKeySetting} entities in the database.
 * The main input is a {@link OrgKeySettingCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link OrgKeySettingDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class OrgKeySettingQueryService extends QueryService<OrgKeySetting> {

    private static final Logger LOG = LoggerFactory.getLogger(OrgKeySettingQueryService.class);

    private final OrgKeySettingRepository orgKeySettingRepository;

    private final OrgKeySettingMapper orgKeySettingMapper;

    public OrgKeySettingQueryService(OrgKeySettingRepository orgKeySettingRepository, OrgKeySettingMapper orgKeySettingMapper) {
        this.orgKeySettingRepository = orgKeySettingRepository;
        this.orgKeySettingMapper = orgKeySettingMapper;
    }

    /**
     * Return a {@link Page} of {@link OrgKeySettingDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<OrgKeySettingDTO> findByCriteria(OrgKeySettingCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<OrgKeySetting> specification = createSpecification(criteria);
        return orgKeySettingRepository.findAll(specification, page).map(orgKeySettingMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(OrgKeySettingCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<OrgKeySetting> specification = createSpecification(criteria);
        return orgKeySettingRepository.count(specification);
    }

    /**
     * Function to convert {@link OrgKeySettingCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<OrgKeySetting> createSpecification(OrgKeySettingCriteria criteria) {
        Specification<OrgKeySetting> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), OrgKeySetting_.id));
            }
            if (criteria.getOrgId() != null) {
                specification = specification.and(buildStringSpecification(criteria.getOrgId(), OrgKeySetting_.orgId));
            }
            if (criteria.getKeyId() != null) {
                specification = specification.and(buildStringSpecification(criteria.getKeyId(), OrgKeySetting_.keyId));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), OrgKeySetting_.description));
            }
            if (criteria.getIsActive() != null) {
                specification = specification.and(buildSpecification(criteria.getIsActive(), OrgKeySetting_.isActive));
            }
            if (criteria.getCrDatetime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCrDatetime(), OrgKeySetting_.crDatetime));
            }
            if (criteria.getUpDatetime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUpDatetime(), OrgKeySetting_.upDatetime));
            }
        }
        return specification;
    }
}
