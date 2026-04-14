package gov.moda.dw.manager.service.mapper;

import gov.moda.dw.manager.domain.Role;
import gov.moda.dw.manager.service.dto.RoleDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Role} and its DTO {@link RoleDTO}.
 */
@Mapper(componentModel = "spring")
public interface RoleMapper extends EntityMapper<RoleDTO, Role> {}
