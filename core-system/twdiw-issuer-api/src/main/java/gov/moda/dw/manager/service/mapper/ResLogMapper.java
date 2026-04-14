package gov.moda.dw.manager.service.mapper;

import gov.moda.dw.manager.domain.ResLog;
import gov.moda.dw.manager.service.dto.ResLogDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ResLog} and its DTO {@link ResLogDTO}.
 */
@Mapper(componentModel = "spring")
public interface ResLogMapper extends EntityMapper<ResLogDTO, ResLog> {}
