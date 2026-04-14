package gov.moda.dw.manager.service.mapper;

import gov.moda.dw.manager.domain.RoleLog;
import gov.moda.dw.manager.service.dto.RoleLogDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link RoleLog} and its DTO {@link RoleLogDTO}.
 */
@Mapper(componentModel = "spring")
public interface RoleLogMapper extends EntityMapper<RoleLogDTO, RoleLog> {}
