package gov.moda.dw.issuer.oidvci.service.mapper;

import gov.moda.dw.issuer.oidvci.domain.AccessToken;
import gov.moda.dw.issuer.oidvci.service.dto.AccessTokenDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AccessToken} and its DTO {@link AccessTokenDTO}.
 */
@Mapper(componentModel = "spring")
public interface AccessTokenMapper extends EntityMapper<AccessTokenDTO, AccessToken> {}
