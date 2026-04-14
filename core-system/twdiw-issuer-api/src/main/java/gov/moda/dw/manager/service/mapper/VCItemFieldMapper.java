package gov.moda.dw.manager.service.mapper;

import gov.moda.dw.manager.domain.VCItemField;
import gov.moda.dw.manager.service.dto.VCItemFieldDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link VCItemField} and its DTO {@link VCItemFieldDTO}.
 */
@Mapper(componentModel = "spring")
public interface VCItemFieldMapper extends EntityMapper<VCItemFieldDTO, VCItemField> {}
