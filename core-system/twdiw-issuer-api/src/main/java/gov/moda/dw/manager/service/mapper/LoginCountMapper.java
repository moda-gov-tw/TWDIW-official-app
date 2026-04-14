package gov.moda.dw.manager.service.mapper;

import gov.moda.dw.manager.domain.LoginCount;
import gov.moda.dw.manager.service.dto.LoginCountDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link LoginCount} and its DTO {@link LoginCountDTO}.
 */
@Mapper(componentModel = "spring")
public interface LoginCountMapper extends EntityMapper<LoginCountDTO, LoginCount> {}
