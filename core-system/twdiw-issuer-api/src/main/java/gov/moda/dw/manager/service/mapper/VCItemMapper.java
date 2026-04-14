package gov.moda.dw.manager.service.mapper;

import gov.moda.dw.manager.domain.VCItem;
import gov.moda.dw.manager.service.dto.VCItemDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link VCItem} and its DTO {@link VCItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface VCItemMapper extends EntityMapper<VCItemDTO, VCItem> {}
