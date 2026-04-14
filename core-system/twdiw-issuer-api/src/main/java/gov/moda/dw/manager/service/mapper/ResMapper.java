package gov.moda.dw.manager.service.mapper;

import gov.moda.dw.manager.domain.Res;
import gov.moda.dw.manager.service.dto.ResDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Res} and its DTO {@link ResDTO}.
 */
@Mapper(componentModel = "spring")
public interface ResMapper extends EntityMapper<ResDTO, Res> {}
