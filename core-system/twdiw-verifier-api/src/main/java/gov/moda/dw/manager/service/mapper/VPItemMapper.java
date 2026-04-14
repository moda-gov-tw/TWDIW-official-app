package gov.moda.dw.manager.service.mapper;

import gov.moda.dw.manager.domain.VPItem;
import gov.moda.dw.manager.service.dto.VPItemDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link VPItem} and its DTO {@link VPItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface VPItemMapper extends EntityMapper<VPItemDTO, VPItem> {}
