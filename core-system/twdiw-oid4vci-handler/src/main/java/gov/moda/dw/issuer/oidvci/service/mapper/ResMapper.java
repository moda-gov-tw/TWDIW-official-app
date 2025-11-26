package gov.moda.dw.issuer.oidvci.service.mapper;

import gov.moda.dw.issuer.oidvci.domain.Res;
import gov.moda.dw.issuer.oidvci.service.dto.ResDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Res} and its DTO {@link ResDTO}.
 */
@Mapper(componentModel = "spring")
public interface ResMapper extends EntityMapper<ResDTO, Res> {}
