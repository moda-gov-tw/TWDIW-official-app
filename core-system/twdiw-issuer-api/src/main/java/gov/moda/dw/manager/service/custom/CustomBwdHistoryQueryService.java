package gov.moda.dw.manager.service.custom;

import gov.moda.dw.manager.domain.BwdHistory;
import gov.moda.dw.manager.repository.BwdHistoryRepository;
import gov.moda.dw.manager.service.BwdHistoryQueryService;
import gov.moda.dw.manager.service.criteria.BwdHistoryCriteria;
import gov.moda.dw.manager.service.dto.BwdHistoryDTO;
import gov.moda.dw.manager.service.mapper.BwdHistoryMapper;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CustomBwdHistoryQueryService extends BwdHistoryQueryService {

    private final Logger log = LoggerFactory.getLogger(CustomBwdHistoryQueryService.class);

    private final BwdHistoryRepository bwdHistoryRepository;

    private final BwdHistoryMapper bwdHistoryMapper;

    public CustomBwdHistoryQueryService(BwdHistoryRepository bwdHistoryRepository, BwdHistoryMapper bwdHistoryMapper) {
        super(bwdHistoryRepository, bwdHistoryMapper);
        this.bwdHistoryRepository = bwdHistoryRepository;
        this.bwdHistoryMapper = bwdHistoryMapper;
    }

    @Transactional(readOnly = true)
    public List<BwdHistoryDTO> findByCriteria(BwdHistoryCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<BwdHistory> specification = createSpecification(criteria);
        return bwdHistoryRepository.findAll(specification).stream().map(bwdHistoryMapper::toDto).collect(Collectors.toList());
    }

    /**
     * Return a {@link Page} of {@link BwdHistoryDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<BwdHistoryDTO> findByCriteria(BwdHistoryCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<BwdHistory> specification = createSpecification(criteria);
        return bwdHistoryRepository.findAll(specification, page).map(bwdHistoryMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(BwdHistoryCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<BwdHistory> specification = createSpecification(criteria);
        return bwdHistoryRepository.count(specification);
    }
}
