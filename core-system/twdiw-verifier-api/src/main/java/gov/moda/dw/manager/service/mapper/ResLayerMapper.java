package gov.moda.dw.manager.service.mapper;

import gov.moda.dw.manager.domain.ResLayer;
import gov.moda.dw.manager.service.dto.ResLayerDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ResLayer} and its DTO {@link ResLayerDTO}.
 */
@Mapper(componentModel = "spring")
public interface ResLayerMapper extends EntityMapper<ResLayerDTO, ResLayer> {}
