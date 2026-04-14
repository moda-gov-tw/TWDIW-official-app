package gov.moda.dw.manager.service.mapper.custom;

import gov.moda.dw.manager.domain.VPItem;
import gov.moda.dw.manager.service.dto.VPItemDTO;
import gov.moda.dw.manager.service.dto.custom.VPItemCreateByVdrDTO;
import gov.moda.dw.manager.service.dto.custom.VPItemCreateDTO;
import gov.moda.dw.manager.service.mapper.EntityMapper;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link VPItem} and its DTO {@link VPItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface VPItemCreateByVdrMapper extends EntityMapper<VPItemCreateByVdrDTO, VPItem> {}
