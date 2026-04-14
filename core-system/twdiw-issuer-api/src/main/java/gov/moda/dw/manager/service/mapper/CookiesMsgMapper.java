package gov.moda.dw.manager.service.mapper;

import gov.moda.dw.manager.domain.CookiesMsg;
import gov.moda.dw.manager.service.dto.CookiesMsgDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CookiesMsg} and its DTO {@link CookiesMsgDTO}.
 */
@Mapper(componentModel = "spring")
public interface CookiesMsgMapper extends EntityMapper<CookiesMsgDTO, CookiesMsg> {}
