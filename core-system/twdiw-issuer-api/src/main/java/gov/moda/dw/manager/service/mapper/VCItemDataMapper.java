package gov.moda.dw.manager.service.mapper;

import gov.moda.dw.manager.domain.VCItem;
import gov.moda.dw.manager.domain.VCItemData;
import gov.moda.dw.manager.service.dto.VCItemDTO;
import gov.moda.dw.manager.service.dto.VCItemDataDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link VCItemData} and its DTO {@link VCItemDataDTO}.
 */
@Mapper(componentModel = "spring")
public interface VCItemDataMapper extends EntityMapper<VCItemDataDTO, VCItemData> {
    @Mapping(target = "vcItem", source = "vcItem", qualifiedByName = "vCItemId")
    VCItemDataDTO toDto(VCItemData s);

    @Named("vCItemId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    VCItemDTO toDtoVCItemId(VCItem vCItem);
}
