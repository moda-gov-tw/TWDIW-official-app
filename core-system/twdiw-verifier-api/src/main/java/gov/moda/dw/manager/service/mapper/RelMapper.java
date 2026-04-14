package gov.moda.dw.manager.service.mapper;

import gov.moda.dw.manager.domain.Rel;
import gov.moda.dw.manager.service.dto.RelDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Rel} and its DTO {@link RelDTO}.
 */
@Mapper(componentModel = "spring")
public interface RelMapper extends EntityMapper<RelDTO, Rel> {}
