package gov.moda.dw.manager.service;

import gov.moda.dw.manager.domain.*; // for static metamodels
import gov.moda.dw.manager.domain.VCItem;
import gov.moda.dw.manager.repository.custom.CustomOrgRepository;
import gov.moda.dw.manager.repository.custom.CustomUserRepository;
import gov.moda.dw.manager.repository.custom.CustomVCItemRepository;
import gov.moda.dw.manager.security.SecurityUtils;
import gov.moda.dw.manager.security.jwt.JwtUserObject;
import gov.moda.dw.manager.service.criteria.VCItemCriteria;
import gov.moda.dw.manager.service.custom.CustomQueryService;
import gov.moda.dw.manager.service.dto.VCItemDTO;
import gov.moda.dw.manager.service.mapper.VCItemMapper;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for executing complex queries for {@link VCItem} entities in the database.
 * The main input is a {@link VCItemCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link VCItemDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class DwSandBoxVC201WQueryService extends CustomQueryService<VCItem> {

    private static final Logger log = LoggerFactory.getLogger(DwSandBoxVC201WQueryService.class);

    private final CustomVCItemRepository vCItemRepository;

    private final VCItemMapper vCItemMapper;

    private CustomUserRepository customUserRepository;

    private final CustomOrgRepository customOrgRepository;
    
    private final DwSandBoxVC201WService dwSandBoxVC201WService;

    public DwSandBoxVC201WQueryService(
        CustomVCItemRepository vCItemRepository,
        VCItemMapper vCItemMapper,
        CustomUserRepository customUserRepository,
        CustomOrgRepository customOrgRepository,
        DwSandBoxVC201WService dwSandBoxVC201WService
    ) {
        this.vCItemRepository = vCItemRepository;
        this.vCItemMapper = vCItemMapper;
        this.customUserRepository = customUserRepository;
        this.customOrgRepository = customOrgRepository;
        this.dwSandBoxVC201WService = dwSandBoxVC201WService;
    }

    /**
     * Return a {@link Page} of {@link VCItemDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<VCItemDTO> findByCriteria(VCItemCriteria criteria, Pageable page, String businessIdContains) {
        log.debug("find by criteria : {}, page: {}", criteria, page);

        final Specification<VCItem> specification = createSpecificationWithExpose(criteria, businessIdContains);
        Long userId = getLoginUserId();

        boolean sortByOwner = false;
        boolean descending = false;

        if (page.getSort() != null) {
            for (Sort.Order order : page.getSort()) {
                if ("owner".equals(order.getProperty())) {
                    sortByOwner = true;
                    descending = order.getDirection() == Sort.Direction.DESC;
                    break;
                }
            }
        }

        List<VCItemDTO> dtoList;

        // 另外處理以"我的建立"進行排序
        if (sortByOwner) {
            // 查全部資料（不使用 pageable），以便後續做 owner 排序 + 自行分頁
            List<VCItem> allItems = vCItemRepository.findAll(specification);

            dtoList = allItems.stream().map(vcItem -> {
                VCItemDTO dto = vCItemMapper.toDto(vcItem);
                dto.setBusinessEngName(vcItem.getOrg().getOrgEnName());
                dto.setBusinessTWName(vcItem.getOrg().getOrgTwName());
                dto.setOwner(vcItem.getCrUser().equals(userId));
                return dto;
            }).collect(Collectors.toList());

            Comparator<VCItemDTO> comparator = Comparator.comparing(VCItemDTO::isOwner);
            if (descending) {
                comparator = comparator.reversed();
            }
            dtoList.sort(comparator);

            // 手動分頁
            int start = (int) page.getOffset();
            int end = Math.min(start + page.getPageSize(), dtoList.size());
            List<VCItemDTO> pagedList = dtoList.subList(start, end);

            return new PageImpl<>(pagedList, page, dtoList.size());
        } else {
            Page<VCItem> vcItemPage = vCItemRepository.findAll(specification, page);

            dtoList = vcItemPage.getContent().stream().map(vcItem -> {
                VCItemDTO dto = vCItemMapper.toDto(vcItem);
                dto.setBusinessEngName(vcItem.getOrg().getOrgEnName());
                dto.setBusinessTWName(vcItem.getOrg().getOrgTwName());
                dto.setOwner(vcItem.getCrUser().equals(userId));
                return dto;
            }).collect(Collectors.toList());

            return new PageImpl<>(dtoList, page, vcItemPage.getTotalElements());
        }
    }

    @Transactional(readOnly = true)
    public List<VCItemDTO> findByCriteria(VCItemCriteria criteria) {
        log.debug("find by criteria : {}, page: {}", criteria);
        final Specification<VCItem> specification = createSpecification(criteria);
        return vCItemRepository.findAll(specification).stream().map(vCItemMapper::toDto).toList();
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(VCItemCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<VCItem> specification = createSpecification(criteria);
        return vCItemRepository.count(specification);
    }

    /**
     * Function to convert {@link VCItemCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<VCItem> createSpecification(VCItemCriteria criteria) {
        Specification<VCItem> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), VCItem_.id));
            }
            if (criteria.getSerialNo() != null) {
                specification = specification.and(buildStringSpecification(criteria.getSerialNo(), VCItem_.serialNo));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), VCItem_.name));
            }
            if (criteria.getCrUser() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCrUser(), VCItem_.crUser));
            }
            if (criteria.getCrDatetime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCrDatetime(), VCItem_.crDatetime));
            }
            if (criteria.getCategoryId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCategoryId(), VCItem_.categoryId));
            }
            if (criteria.getBusinessId() != null) {
                specification = specification.and(buildStringSpecification(criteria.getBusinessId(), VCItem_.businessId));
            }
            if (criteria.getSchemaId() != null) {
                specification = specification.and(buildStringSpecification(criteria.getSchemaId(), VCItem_.schemaId));
            }
            if (criteria.getUnitTypeExpire() != null) {
                specification = specification.and(buildStringSpecification(criteria.getUnitTypeExpire(), VCItem_.unitTypeExpire));
            }
            if (criteria.getLengthExpire() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLengthExpire(), VCItem_.lengthExpire));
            }
        }
        return specification;
    }

    /*
    在查詢條件有下組織Id的情況下，多撈 expose 類型的 VC 模版
     */
    protected Specification<VCItem> createSpecificationWithExpose(VCItemCriteria criteria, String businessIdContains) {
        Specification<VCItem> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), VCItem_.id));
            }
            if (criteria.getSerialNo() != null) {
                specification = specification.and(buildStringSpecification(criteria.getSerialNo(), VCItem_.serialNo));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), VCItem_.name));
            }
            if (criteria.getCrUser() != null) {
                Specification<VCItem> i = buildRangeSpecification(criteria.getCrUser(), VCItem_.crUser);

                i = i.or(buildSpecification(criteria.getExpose(), VCItem_.expose));

                specification = specification.and(i);
            }
            if (criteria.getCrDatetime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCrDatetime(), VCItem_.crDatetime));
            }
			if (criteria.getCategoryId() != null) {
				specification = specification
						.and(buildRangeSpecification(criteria.getCategoryId(), VCItem_.categoryId));
			}
			if (criteria.getBusinessId() != null) {
				Specification<VCItem> i = buildStringSpecification(criteria.getBusinessId(), VCItem_.businessId)
						.or(buildSpecification(criteria.getExpose(), VCItem_.expose))
						.and((root, query, builder) -> builder.isFalse(root.get(VCItem_.isTemp)));

				// 自己建立的 VC 條件（不論 isTemp 為 true/false）
				Long loginUserId = dwSandBoxVC201WService.getLoginUserId();
				Specification<VCItem> selfCreatedTemp = (root, query, builder) -> builder.and(
						builder.equal(root.get(VCItem_.crUser), loginUserId), builder.isTrue(root.get(VCItem_.isTemp)));

				specification = specification.and(i.or(selfCreatedTemp));
			}
			if (criteria.getSchemaId() != null) {
				specification = specification.and(buildStringSpecification(criteria.getSchemaId(), VCItem_.schemaId));
			}
            if (criteria.getUnitTypeExpire() != null) {
                specification = specification.and(buildStringSpecification(criteria.getUnitTypeExpire(), VCItem_.unitTypeExpire));
            }
            if (criteria.getLengthExpire() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLengthExpire(), VCItem_.lengthExpire));
			}
			if (dwSandBoxVC201WService.checkIsPrivilegedAccount(SecurityUtils.getJwtUserObject().get(0).getUserId())) {
				Long loginUserId = dwSandBoxVC201WService.getLoginUserId();

				Specification<VCItem> allVC = (root, query, builder) -> builder.isFalse(root.get(VCItem_.isTemp));

				Specification<VCItem> selfTemp = (root, query, builder) -> builder.and(
						builder.equal(root.get(VCItem_.crUser), loginUserId), builder.isTrue(root.get(VCItem_.isTemp)));

				specification = specification.and(allVC.or(selfTemp));
			}
		}
		if (businessIdContains != null) {
			List<String> orgIds = customOrgRepository.findOrgIdsByKeyword(businessIdContains);
			return specification.and((root, query, cb) -> cb.in(root.get("businessId")).value(orgIds));
		}

		return specification;
    }

    public long getLoginUserId() {
        String loginId = null;
        Long userId = 0L;
        Optional<JwtUserObject> first = SecurityUtils.getJwtUserObject().stream().findFirst();
        if (first.isPresent()) {
            loginId = first.get().getUserId();
            userId = findUserId(loginId);
        }
        return userId;
    }

    public Long findUserId(String login) {
        return customUserRepository.findOneByLogin(login).orElseThrow().getId();
    }
}
