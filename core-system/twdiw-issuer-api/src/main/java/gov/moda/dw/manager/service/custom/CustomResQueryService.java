package gov.moda.dw.manager.service.custom;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import gov.moda.dw.manager.domain.Res;
import gov.moda.dw.manager.repository.ResRepository;
import gov.moda.dw.manager.service.ResQueryService;
import gov.moda.dw.manager.service.criteria.ResCriteria;
import gov.moda.dw.manager.service.dto.ResDTO;
import gov.moda.dw.manager.service.mapper.ResMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for executing complex queries for {@link Res} entities in the database.
 * The main input is a {@link ResCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ResDTO} which fulfills the criteria.
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class CustomResQueryService extends ResQueryService {

    private final ResRepository resRepository;

    private final ResMapper resMapper;

    public CustomResQueryService(ResRepository resRepository, ResMapper resMapper) {
        super(resRepository, resMapper);
        this.resRepository = resRepository;
        this.resMapper = resMapper;
    }

    /**
     * Return a {@link List} of {@link ResDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional
    public List<ResDTO> findByCriteria(ResCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Res> specification = createSpecification(criteria);
        return resMapper.toDto(resRepository.findAll(specification));
    }
}
