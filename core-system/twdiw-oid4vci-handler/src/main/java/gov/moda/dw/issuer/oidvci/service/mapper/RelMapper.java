package gov.moda.dw.issuer.oidvci.service.mapper;

import gov.moda.dw.issuer.oidvci.domain.Rel;
import gov.moda.dw.issuer.oidvci.service.dto.RelDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Rel} and its DTO {@link RelDTO}.
 */
@Mapper(componentModel = "spring")
public interface RelMapper extends EntityMapper<RelDTO, Rel> {}
