package gov.moda.dw.issuer.vc.service.mapper;

import gov.moda.dw.issuer.vc.domain.ExtendedUser;
import gov.moda.dw.issuer.vc.service.dto.ExtendedUserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ExtendedUser} and its DTO {@link ExtendedUserDTO}.
 */
@Mapper(componentModel = "spring")
public interface ExtendedUserMapper extends EntityMapper<ExtendedUserDTO, ExtendedUser> {}
