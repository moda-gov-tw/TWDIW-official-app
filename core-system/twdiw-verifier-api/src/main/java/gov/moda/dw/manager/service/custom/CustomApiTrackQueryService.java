package gov.moda.dw.manager.service.custom;

import gov.moda.dw.manager.domain.ApiTrack;
import gov.moda.dw.manager.repository.ApiTrackRepository;
import gov.moda.dw.manager.service.ApiTrackQueryService;
import gov.moda.dw.manager.service.criteria.ApiTrackCriteria;
import gov.moda.dw.manager.service.dto.ApiTrackDTO;
import gov.moda.dw.manager.service.mapper.ApiTrackMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for executing complex queries for {@link ApiTrack} entities in the database.
 * The main input is a {@link ApiTrackCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ApiTrackDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CustomApiTrackQueryService extends ApiTrackQueryService {

    private final Logger log = LoggerFactory.getLogger(CustomApiTrackQueryService.class);

    private final ApiTrackRepository apiTrackRepository;

    private final ApiTrackMapper apiTrackMapper;

    public CustomApiTrackQueryService(ApiTrackRepository apiTrackRepository, ApiTrackMapper apiTrackMapper) {
        super(apiTrackRepository, apiTrackMapper);
        this.apiTrackRepository = apiTrackRepository;
        this.apiTrackMapper = apiTrackMapper;
    }

    /**
     * Return a {@link List} of {@link ApiTrackDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ApiTrackDTO> findByCriteria(ApiTrackCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<ApiTrack> specification = createSpecification(criteria);
        return apiTrackMapper.toDto(apiTrackRepository.findAll(specification));
    }

    public Page<ApiTrackDTO> findBySpecification(Specification<ApiTrack> specification, Pageable pageable) {
        log.debug("find by specification : {}", specification);
        return apiTrackRepository.findAll(specification, pageable).map(apiTrackMapper::toDto);
    }
}
