package gov.moda.dw.manager.service.custom;

import gov.moda.dw.manager.domain.LoginCount;
import gov.moda.dw.manager.repository.LoginCountRepository;
import gov.moda.dw.manager.service.LoginCountQueryService;
import gov.moda.dw.manager.service.criteria.LoginCountCriteria;
import gov.moda.dw.manager.service.dto.LoginCountDTO;
import gov.moda.dw.manager.service.mapper.LoginCountMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CustomLoginCountQueryService extends LoginCountQueryService {

  private final Logger log = LoggerFactory.getLogger(CustomLoginCountQueryService.class);

  private final LoginCountRepository loginCountRepository;

  private final LoginCountMapper loginCountMapper;

  public CustomLoginCountQueryService(LoginCountRepository loginCountRepository, LoginCountMapper loginCountMapper) {
    super(loginCountRepository, loginCountMapper);
    this.loginCountRepository = loginCountRepository;
    this.loginCountMapper = loginCountMapper;
  }

  @Transactional(readOnly = true)
  public List<LoginCountDTO> findByCriteria(LoginCountCriteria criteria) {
    log.debug("find by criteria : {}", criteria);
    final Specification<LoginCount> specification = createSpecification(criteria);
    return loginCountRepository.findAll(specification).stream()
            .map(loginCountMapper::toDto)
            .collect(Collectors.toList());
  }

  /**
   * Return a {@link Page} of {@link LoginCountDTO} which matches the criteria from the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @param page The page, which should be returned.
   * @return the matching entities.
   */
  @Transactional(readOnly = true)
  public Page<LoginCountDTO> findByCriteria(LoginCountCriteria criteria, Pageable page) {
    log.debug("find by criteria : {}, page: {}", criteria, page);
    final Specification<LoginCount> specification = createSpecification(criteria);
    return loginCountRepository.findAll(specification, page).map(loginCountMapper::toDto);
  }

  /**
   * Return the number of matching entities in the database.
   * @param criteria The object which holds all the filters, which the entities should match.
   * @return the number of matching entities.
   */
  @Transactional(readOnly = true)
  public long countByCriteria(LoginCountCriteria criteria) {
    log.debug("count by criteria : {}", criteria);
    final Specification<LoginCount> specification = createSpecification(criteria);
    return loginCountRepository.count(specification);
  }

}
