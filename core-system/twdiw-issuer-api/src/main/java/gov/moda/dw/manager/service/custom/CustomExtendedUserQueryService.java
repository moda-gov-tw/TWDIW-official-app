package gov.moda.dw.manager.service.custom;

import gov.moda.dw.manager.domain.ExtendedUser;
import gov.moda.dw.manager.domain.ExtendedUser_;
import gov.moda.dw.manager.repository.ExtendedUserRepository;
import gov.moda.dw.manager.service.ExtendedUserQueryService;
import gov.moda.dw.manager.service.criteria.ExtendedUserCriteria;
import gov.moda.dw.manager.service.dto.ExtendedUserDTO;
import gov.moda.dw.manager.service.mapper.ExtendedUserMapper;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for executing complex queries for {@link ExtendedUser} entities in the database.
 * The main input is a {@link ExtendedUserCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ExtendedUserDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CustomExtendedUserQueryService extends ExtendedUserQueryService {

    private final Logger log = LoggerFactory.getLogger(CustomExtendedUserQueryService.class);

    private final ExtendedUserRepository extendedUserRepository;

    private final ExtendedUserMapper extendedUserMapper;

    public CustomExtendedUserQueryService(ExtendedUserRepository extendedUserRepository, ExtendedUserMapper extendedUserMapper) {
        super(extendedUserRepository, extendedUserMapper);
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

    @Transactional(readOnly = true)
    public List<ExtendedUserDTO> findByCriteria(ExtendedUserCriteria criteria) {
        log.debug("find by criteria: {}", criteria);
        final Specification<ExtendedUser> specification = createSpecification(criteria);
        List<ExtendedUser> extendedUsers = extendedUserRepository.findAll(specification);
        return extendedUserMapper.toDto(extendedUsers);
    }

    public List<ExtendedUserDTO> findByCriteria(ExtendedUserCriteria criteria, Sort sort) {
        log.debug("find by criteria : {}, sort: {}", criteria, sort);

        // 初始化一個列表來存儲替換後的排序條件
        List<Sort.Order> orders = new ArrayList<>();
        boolean hasUserNameSort = false; // 用於檢查是否包含userName排序
        Sort.Order userNameOrder = null; // 用於儲存userName排序條件

        if (sort != null) {
            // 檢查sort是否包含login或userName屬性並進行替換或記錄
            for (Sort.Order order : sort) {
                if ("login".equals(order.getProperty())) {
                    // 如果屬性是login，則使用userId替換
                    orders.add(new Sort.Order(order.getDirection(), "userId"));
                } else if ("userName".equals(order.getProperty())) {
                    // 如果屬性是 userName，記錄並標記
                    hasUserNameSort = true;
                    userNameOrder = order;
                } else {
                    // 否則保留原有的排序條件
                    orders.add(order);
                }
            }
        }

        // 使用替換後的排序條件創建新的Sort對象
        Sort updatedSort = Sort.by(orders);

        // 根據criteria生成規範
        final Specification<ExtendedUser> specification = this.customCreateSpecification(criteria);

        // 從資料庫獲取未排序或部分排序的結果
        List<ExtendedUserDTO> result = extendedUserRepository
            .findAll(specification, updatedSort)
            .stream()
            .map(extendedUserMapper::toDto)
            .collect(Collectors.toList());

        // 如果包含userName排序條件，則對結果進行排序
        if (hasUserNameSort) {
            if (userNameOrder.getDirection().isAscending()) {
                result.sort(Comparator.comparing(ExtendedUserDTO::getUserName)); // ASC排序
            } else {
                result.sort(Comparator.comparing(ExtendedUserDTO::getUserName).reversed()); // DESC排序
            }
        }

        return result;
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

    protected Specification<ExtendedUser> customCreateSpecification(ExtendedUserCriteria criteria) {
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
                if (StringUtils.isNotBlank(criteria.getUserId().getContains())) {
                    // 使用CriteriaBuilder來處理LIKE和萬用字元。
                    specification = specification.and((root, query, criteriaBuilder) -> {
                        String escapedUserId = criteria.getUserId().getContains().replace("_", "\\_").replace("%", "\\%");
                        return criteriaBuilder.like(root.get(ExtendedUser_.userId), "%" + escapedUserId + "%", '\\');
                    });
                } else {
                    specification = specification.and(buildStringSpecification(criteria.getUserId(), ExtendedUser_.userId));
                }
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
