package gov.moda.dw.manager.service;

import gov.moda.dw.manager.domain.*; // for static metamodels
import gov.moda.dw.manager.domain.VPItem;
import gov.moda.dw.manager.repository.VPItemRepository;
import gov.moda.dw.manager.service.criteria.VPItemCriteria;
import gov.moda.dw.manager.service.custom.query.CustomQueryService;
import gov.moda.dw.manager.service.dto.VPItemDTO;
import gov.moda.dw.manager.service.mapper.VPItemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link VPItem} entities in the database.
 * The main input is a {@link VPItemCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link VPItemDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class VPItemQueryService extends CustomQueryService<VPItem> {

    private static final Logger log = LoggerFactory.getLogger(VPItemQueryService.class);

    private final VPItemRepository vPItemRepository;

    private final VPItemMapper vPItemMapper;

    public VPItemQueryService(VPItemRepository vPItemRepository, VPItemMapper vPItemMapper) {
        this.vPItemRepository = vPItemRepository;
        this.vPItemMapper = vPItemMapper;
    }

    /**
     * Return a {@link Page} of {@link VPItemDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<VPItemDTO> findByCriteria(VPItemCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<VPItem> specification = createSpecification(criteria);
        Page<VPItem> page1 = vPItemRepository.findAll(specification, page);
        return page1.map(vPItemMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(VPItemCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<VPItem> specification = createSpecification(criteria);
        return vPItemRepository.count(specification);
    }

    /**
     * Function to convert {@link VPItemCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<VPItem> createSpecification(VPItemCriteria criteria) {
        Specification<VPItem> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), VPItem_.id));
            }
            if (criteria.getSerialNo() != null) {
                specification = specification.and(buildStringSpecification(criteria.getSerialNo(), VPItem_.serialNo));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), VPItem_.name));
            }
            if (criteria.getCrUser() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCrUser(), VPItem_.crUser));
            }
            if (criteria.getCrDatetime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCrDatetime(), VPItem_.crDatetime));
            }
            if (criteria.getBusinessId() != null) {
                specification = specification.and(buildStringSpecification(criteria.getBusinessId(), VPItem_.businessId));
            }
            if (criteria.getPurpose() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPurpose(), VPItem_.purpose));
            }
            if (criteria.getUpUser() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUpUser(), VPItem_.upUser));
            }
            if (criteria.getUpDatetime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUpDatetime(), VPItem_.upDatetime));
            }
            if (criteria.getIsStatic() != null) {
                specification = specification.and(buildSpecification(criteria.getIsStatic(), VPItem_.isStatic));
            }
            if (criteria.getIsOffline() != null) {
                specification = specification.and(buildSpecification(criteria.getIsOffline(), VPItem_.isOffline));
            }
            if (criteria.getTag() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTag(), VPItem_.tag));
            }
            if (criteria.getIsEncryptEnabled() != null) {
                specification = specification.and(buildSpecification(criteria.getIsEncryptEnabled(), VPItem_.isEncryptEnabled));
            }
        }
        return specification;
    }
}
