package gov.moda.dw.manager.service;

import gov.moda.dw.manager.domain.*; // for static metamodels
import gov.moda.dw.manager.domain.VCItemData;
import gov.moda.dw.manager.repository.VCItemDataRepository;
import gov.moda.dw.manager.service.criteria.VCItemDataCriteria;
import gov.moda.dw.manager.service.dto.VCItemDTO;
import gov.moda.dw.manager.service.dto.VCItemDataDTO;
import gov.moda.dw.manager.service.mapper.VCItemDataMapper;
import gov.moda.dw.manager.service.mapper.VCItemMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link VCItemData} entities in the database.
 * The main input is a {@link VCItemDataCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link VCItemDataDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class VCItemDataQueryService extends QueryService<VCItemData> {

    private static final Logger log = LoggerFactory.getLogger(VCItemDataQueryService.class);

    private final VCItemDataRepository vCItemDataRepository;

    private final VCItemDataMapper vCItemDataMapper;

    private final VCItemMapper vcItemMapper;

    public VCItemDataQueryService(VCItemDataRepository vCItemDataRepository, VCItemDataMapper vCItemDataMapper, VCItemMapper vcItemMapper) {
        this.vCItemDataRepository = vCItemDataRepository;
        this.vCItemDataMapper = vCItemDataMapper;
        this.vcItemMapper = vcItemMapper;
    }

    /**
     * Return a {@link Page} of {@link VCItemDataDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<VCItemDataDTO> findByCriteria(VCItemDataCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<VCItemData> specification = createSpecification(criteria);

        Page<VCItemData> vcItemDataPages = vCItemDataRepository.findAll(specification, page);

        Page<VCItemDataDTO> result = vcItemDataPages.map(vcItemData -> {
            VCItemDataDTO itemData = vCItemDataMapper.toDto(vcItemData);

            VCItemDTO item = vcItemMapper.toDto(vcItemData.getVcItem());

            itemData.setVcItem(item);

            return itemData;
        });

        return result;
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(VCItemDataCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<VCItemData> specification = createSpecification(criteria);
        return vCItemDataRepository.count(specification);
    }

    /**
     * Function to convert {@link VCItemDataCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<VCItemData> createSpecification(VCItemDataCriteria criteria) {
        Specification<VCItemData> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), VCItemData_.id));
            }
            if (criteria.getCrUser() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCrUser(), VCItemData_.crUser));
            }
            if (criteria.getCrDatetime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCrDatetime(), VCItemData_.crDatetime));
            }
            if (criteria.getValid() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getValid(), VCItemData_.valid));
            }
            if (criteria.getClearScheduleId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getClearScheduleId(), VCItemData_.clearScheduleId));
            }
            if (criteria.getClearScheduleDatetime() != null) {
                specification = specification.and(
                    buildRangeSpecification(criteria.getClearScheduleDatetime(), VCItemData_.clearScheduleDatetime)
                );
            }
            if (criteria.getVcCid() != null) {
                specification = specification.and(buildStringSpecification(criteria.getVcCid(), VCItemData_.vcCid));
            }

            if (criteria.getTransactionId() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTransactionId(), VCItemData_.transactionId));
            }
            if (criteria.getBusinessId() != null) {
                specification = specification.and(buildStringSpecification(criteria.getBusinessId(), VCItemData_.businessId));
            }
            if (criteria.getVcItemName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getVcItemName(), VCItemData_.vcItemName));
            }
            if (criteria.getExpired() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getExpired(), VCItemData_.expired));
            }
            if (criteria.getScheduleRevokeMessage() != null) {
                specification = specification.and(
                    buildStringSpecification(criteria.getScheduleRevokeMessage(), VCItemData_.scheduleRevokeMessage)
                );
            }
            if (criteria.getDataTag() != null) {
                specification = specification.and(
                  buildStringSpecification(criteria.getDataTag(), VCItemData_.dataTag)
                );
              }
            if (criteria.getVcItemId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getVcItemId(), root -> root.join(VCItemData_.vcItem, JoinType.LEFT).get(VCItem_.id))
                );
            }
        }
        return specification;
    }
}
