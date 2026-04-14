package gov.moda.dw.manager.service.mapper;

import gov.moda.dw.manager.domain.ExtendedUser;
import gov.moda.dw.manager.service.dto.ExtendedUserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ExtendedUser} and its DTO {@link ExtendedUserDTO}.
 */
@Mapper(componentModel = "spring")
public interface ExtendedUserMapper extends EntityMapper<ExtendedUserDTO, ExtendedUser> {}
