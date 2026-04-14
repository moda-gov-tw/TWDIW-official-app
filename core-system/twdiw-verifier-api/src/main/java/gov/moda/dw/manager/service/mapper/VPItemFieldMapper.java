package gov.moda.dw.manager.service.mapper;

import gov.moda.dw.manager.domain.VPItemField;
import gov.moda.dw.manager.service.dto.VPItemFieldDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link VPItemField} and its DTO {@link VPItemFieldDTO}.
 */
@Mapper(componentModel = "spring")
public interface VPItemFieldMapper extends EntityMapper<VPItemFieldDTO, VPItemField> {}
