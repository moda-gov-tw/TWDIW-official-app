package gov.moda.dw.manager.service;

import gov.moda.dw.manager.domain.*; // for static metamodels
import gov.moda.dw.manager.domain.Schedule;
import gov.moda.dw.manager.repository.ScheduleRepository;
import gov.moda.dw.manager.service.criteria.ScheduleCriteria;
import gov.moda.dw.manager.service.dto.ScheduleDTO;
import gov.moda.dw.manager.service.mapper.ScheduleMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Schedule} entities in the database.
 * The main input is a {@link ScheduleCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ScheduleDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ScheduleQueryService extends QueryService<Schedule> {

    private static final Logger log = LoggerFactory.getLogger(ScheduleQueryService.class);

    private final ScheduleRepository scheduleRepository;

    private final ScheduleMapper scheduleMapper;

    public ScheduleQueryService(ScheduleRepository scheduleRepository, ScheduleMapper scheduleMapper) {
        this.scheduleRepository = scheduleRepository;
        this.scheduleMapper = scheduleMapper;
    }

    /**
     * Return a {@link Page} of {@link ScheduleDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ScheduleDTO> findByCriteria(ScheduleCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Schedule> specification = createSpecification(criteria);
        return scheduleRepository.findAll(specification, page).map(scheduleMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ScheduleCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Schedule> specification = createSpecification(criteria);
        return scheduleRepository.count(specification);
    }

    /**
     * Function to convert {@link ScheduleCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Schedule> createSpecification(ScheduleCriteria criteria) {
        Specification<Schedule> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Schedule_.id));
            }
            if (criteria.getType() != null) {
                specification = specification.and(buildStringSpecification(criteria.getType(), Schedule_.type));
            }
            if (criteria.getTime() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTime(), Schedule_.time));
            }
            if (criteria.getDate() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDate(), Schedule_.date));
            }
            if (criteria.getMonth() != null) {
                specification = specification.and(buildStringSpecification(criteria.getMonth(), Schedule_.month));
            }
            if (criteria.getWeek() != null) {
                specification = specification.and(buildStringSpecification(criteria.getWeek(), Schedule_.week));
            }
            if (criteria.getLastRunDatetime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastRunDatetime(), Schedule_.lastRunDatetime));
            }
            if (criteria.getCrDatetime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCrDatetime(), Schedule_.crDatetime));
            }
            if (criteria.getCrUser() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCrUser(), Schedule_.crUser));
            }
            if (criteria.getTimezone() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTimezone(), Schedule_.timezone));
            }
            if (criteria.getBusinessId() != null) {
                specification = specification.and(buildStringSpecification(criteria.getBusinessId(), Schedule_.businessId));
            }
        }
        return specification;
    }
}
