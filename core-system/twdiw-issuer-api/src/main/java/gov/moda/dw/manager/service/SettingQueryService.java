package gov.moda.dw.manager.service;

import gov.moda.dw.manager.domain.*;
import gov.moda.dw.manager.domain.Setting;
import gov.moda.dw.manager.repository.SettingRepository;
import gov.moda.dw.manager.service.criteria.SettingCriteria;
import gov.moda.dw.manager.service.dto.SettingDTO;
import gov.moda.dw.manager.service.mapper.SettingMapper;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Setting} entities in the database.
 * The main input is a {@link SettingCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Setting} of {@link SettingDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SettingQueryService extends QueryService<Setting> {

    private final Logger log = LoggerFactory.getLogger(SettingQueryService.class);

    private final SettingRepository settingRepository;

    private final SettingMapper settingMapper;

    public SettingQueryService(SettingRepository settingRepository, SettingMapper settingMapper) {
        this.settingRepository = settingRepository;
        this.settingMapper = settingMapper;
    }

    @Transactional(readOnly = true)
    public List<Setting> findByCriteria(SettingCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final var specification = createSpecification(criteria);
        return settingRepository.findAll(specification);
    }

    @Transactional(readOnly = true)
    public Page<Setting> findByCriteria(SettingCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final var specification = createSpecification(criteria);
        return settingRepository.findAll(specification, page);
    }

    @Transactional(readOnly = true)
    public long countByCriteria(SettingCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final var specification = createSpecification(criteria);
        return settingRepository.count(specification);
    }

    protected Specification<Setting> createSpecification(SettingCriteria criteria) {
        Specification<Setting> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getPropName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPropName(), Setting_.propName));
            }
            if (criteria.getPropValue() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPropValue(), Setting_.propValue));
            }
        }
        return specification;
    }
}
