package gov.moda.dw.manager.service.mapper;

import gov.moda.dw.manager.domain.Field;
import gov.moda.dw.manager.service.dto.FieldDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Field} and its DTO {@link FieldDTO}.
 */
@Mapper(componentModel = "spring")
public interface FieldMapper extends EntityMapper<FieldDTO, Field> {}
