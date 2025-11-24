package gov.moda.dw.issuer.oidvci.service.mapper;

import gov.moda.dw.issuer.oidvci.domain.ExtendedUser;
import gov.moda.dw.issuer.oidvci.service.dto.ExtendedUserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ExtendedUser} and its DTO {@link ExtendedUserDTO}.
 */
@Mapper(componentModel = "spring")
public interface ExtendedUserMapper extends EntityMapper<ExtendedUserDTO, ExtendedUser> {}
