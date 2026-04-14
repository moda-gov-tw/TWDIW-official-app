package gov.moda.dw.manager.service.mapper;

import gov.moda.dw.manager.domain.Org;
import gov.moda.dw.manager.service.dto.OrgDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Org} and its DTO {@link OrgDTO}.
 */
@Mapper(componentModel = "spring")
public interface OrgMapper extends EntityMapper<OrgDTO, Org> {}
