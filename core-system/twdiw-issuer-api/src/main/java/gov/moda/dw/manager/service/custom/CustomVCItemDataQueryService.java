package gov.moda.dw.manager.service.custom;

import gov.moda.dw.manager.domain.VCItemData;
import gov.moda.dw.manager.repository.VCItemDataRepository;
import gov.moda.dw.manager.service.VCItemDataQueryService;
import gov.moda.dw.manager.service.criteria.VCItemDataCriteria;
import gov.moda.dw.manager.service.mapper.VCItemDataMapper;
import gov.moda.dw.manager.service.mapper.VCItemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomVCItemDataQueryService extends VCItemDataQueryService {

    private static final Logger log = LoggerFactory.getLogger(VCItemDataQueryService.class);

    private final VCItemDataRepository vCItemDataRepository;

    public CustomVCItemDataQueryService(
        VCItemDataRepository vCItemDataRepository,
        VCItemDataMapper vCItemDataMapper,
        VCItemMapper vcItemMapper
    ) {
        super(vCItemDataRepository, vCItemDataMapper, vcItemMapper);
        this.vCItemDataRepository = vCItemDataRepository;
    }

    @Transactional(readOnly = true)
    public Page<VCItemData> findByCriteriaReturnEntity(VCItemDataCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<VCItemData> specification = createSpecification(criteria);

        Page<VCItemData> vcItemDataPages = vCItemDataRepository.findAll(specification, page);

        return vcItemDataPages;
    }
}
