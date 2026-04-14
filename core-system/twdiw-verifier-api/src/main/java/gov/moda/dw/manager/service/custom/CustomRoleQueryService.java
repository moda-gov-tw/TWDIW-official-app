package gov.moda.dw.manager.service.custom;

import java.util.List;

import gov.moda.dw.manager.domain.Role;
import gov.moda.dw.manager.repository.RoleRepository;
import gov.moda.dw.manager.service.RoleQueryService;
import gov.moda.dw.manager.service.criteria.RoleCriteria;
import gov.moda.dw.manager.service.dto.RoleDTO;
import gov.moda.dw.manager.service.mapper.RoleMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for executing complex queries for {@link Role} entities in the database.
 * The main input is a {@link RoleCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link RoleDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CustomRoleQueryService extends RoleQueryService {

    private final Logger log = LoggerFactory.getLogger(CustomRoleQueryService.class);

    private final RoleRepository roleRepository;

    private final RoleMapper roleMapper;

    public CustomRoleQueryService(RoleRepository roleRepository, RoleMapper roleMapper) {
        super(roleRepository, roleMapper);
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    /**
     * Return a {@link List} of {@link RoleDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional
    public List<RoleDTO> findByCriteria(RoleCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Role> specification = createSpecification(criteria);
        return roleMapper.toDto(roleRepository.findAll(specification));
    }
}
