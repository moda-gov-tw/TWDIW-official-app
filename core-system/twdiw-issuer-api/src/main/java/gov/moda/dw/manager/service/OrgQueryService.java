package gov.moda.dw.manager.service;

import gov.moda.dw.manager.domain.*;
import gov.moda.dw.manager.domain.Org;
import gov.moda.dw.manager.repository.OrgRepository;
import gov.moda.dw.manager.service.criteria.OrgCriteria;
import gov.moda.dw.manager.service.dto.OrgDTO;
import gov.moda.dw.manager.service.mapper.OrgMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Org} entities in the database.
 * The main input is a {@link OrgCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link OrgDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class OrgQueryService extends QueryService<Org> {

    private final Logger log = LoggerFactory.getLogger(OrgQueryService.class);

    private final OrgRepository orgRepository;

    private final OrgMapper orgMapper;

    public OrgQueryService(OrgRepository orgRepository, OrgMapper orgMapper) {
        this.orgRepository = orgRepository;
        this.orgMapper = orgMapper;
    }

    /**
     * Return a {@link Page} of {@link OrgDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<OrgDTO> findByCriteria(OrgCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Org> specification = createSpecification(criteria);
        return orgRepository.findAll(specification, page).map(orgMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(OrgCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Org> specification = createSpecification(criteria);
        return orgRepository.count(specification);
    }

    /**
     * Function to convert {@link OrgCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Org> createSpecification(OrgCriteria criteria) {
        Specification<Org> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Org_.id));
            }
            if (criteria.getOrgId() != null) {
                specification = specification.and(buildStringSpecification(criteria.getOrgId(), Org_.orgId));
            }
            if (criteria.getOrgTwName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getOrgTwName(), Org_.orgTwName));
            }
            if (criteria.getOrgEnName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getOrgEnName(), Org_.orgEnName));
            }
            if (criteria.getCreateTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreateTime(), Org_.createTime));
            }
            if (criteria.getUpdateTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUpdateTime(), Org_.updateTime));
            }
            if (criteria.getVcDataSource() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getVcDataSource(), Org_.vcDataSource));
            }
        }
        return specification;
    }
}
