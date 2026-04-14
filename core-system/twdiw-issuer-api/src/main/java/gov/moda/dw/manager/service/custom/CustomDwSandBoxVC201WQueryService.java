package gov.moda.dw.manager.service.custom;

import gov.moda.dw.manager.domain.VCItem;
import gov.moda.dw.manager.repository.custom.CustomVCItemRepository;
import gov.moda.dw.manager.service.DwSandBoxVC201WQueryService;
import gov.moda.dw.manager.service.criteria.VCItemCriteria;
import gov.moda.dw.manager.service.dto.VCItemDTO;
import gov.moda.dw.manager.service.mapper.VCItemMapper;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
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

@Service
@Transactional(readOnly = true)
public class CustomDwSandBoxVC201WQueryService extends QueryService<VCItem> {

    private static final Logger log = LoggerFactory.getLogger(CustomDwSandBoxVC201WQueryService.class);

    private final CustomVCItemRepository vCItemRepository;

    private final VCItemMapper vcItemMapper;

    public CustomDwSandBoxVC201WQueryService(CustomVCItemRepository customVCItemRepository, VCItemMapper vcItemMapper) {
        this.vCItemRepository = customVCItemRepository;
        this.vcItemMapper = vcItemMapper;
    }

    @Transactional(readOnly = true)
    public Page<VCItemDTO> findByCriteria(VCItemCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<VCItem> specification = createSpecificationWithExpose(criteria);

        Page<VCItem> vcItemPage = vCItemRepository.findAll(specification, page);

        List<VCItemDTO> dtoList = new ArrayList<>();
        for (VCItem vcItem : vcItemPage.getContent()) {
            VCItemDTO vcItemDTO = vcItemMapper.toDto(vcItem);
            vcItemDTO.setBusinessEngName(vcItem.getOrg().getOrgEnName());
            vcItemDTO.setBusinessTWName(vcItem.getOrg().getOrgTwName());
            dtoList.add(vcItemDTO);
        }

        Page<VCItemDTO> result = new PageImpl<>(dtoList, page, vcItemPage.getTotalElements());

        return result; // vCItemRepository.findAll(specification, page).map(vCItemMapper::toDto);
    }

    protected Specification<VCItem> createSpecificationWithExpose(VCItemCriteria criteria) {
        Specification<VCItem> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (
                criteria.getSerialNo() != null &&
                criteria.getSerialNo().getContains() != null &&
                !criteria.getSerialNo().getContains().isEmpty()
            ) {
                predicates.add(criteriaBuilder.like(root.get("serial_no"), "%" + criteria.getSerialNo().getContains() + "%", '\\'));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return specification;
    }
}
