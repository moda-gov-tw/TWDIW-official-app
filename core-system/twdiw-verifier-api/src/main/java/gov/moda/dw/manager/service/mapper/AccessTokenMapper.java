package gov.moda.dw.manager.service.mapper;

import gov.moda.dw.manager.domain.AccessToken;
import gov.moda.dw.manager.service.dto.AccessTokenDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AccessToken} and its DTO {@link AccessTokenDTO}.
 */
@Mapper(componentModel = "spring")
public interface AccessTokenMapper extends EntityMapper<AccessTokenDTO, AccessToken> {}
