package gov.moda.dw.manager.service.mapper;

import gov.moda.dw.manager.domain.*;
import gov.moda.dw.manager.service.dto.RegularExpressionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link RegularExpression} and its DTO {@link RegularExpression}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface RegularExpressionMapper extends EntityMapper<RegularExpressionDTO, RegularExpression> {}
