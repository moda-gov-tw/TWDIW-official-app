package gov.moda.dw.manager.service.mapper;

import gov.moda.dw.manager.domain.ExtendedUserLog;
import gov.moda.dw.manager.service.dto.ExtendedUserLogDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ExtendedUserLog} and its DTO {@link ExtendedUserLogDTO}.
 */
@Mapper(componentModel = "spring")
public interface ExtendedUserLogMapper extends EntityMapper<ExtendedUserLogDTO, ExtendedUserLog> {}
