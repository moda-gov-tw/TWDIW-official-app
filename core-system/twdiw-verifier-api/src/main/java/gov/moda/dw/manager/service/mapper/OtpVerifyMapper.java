package gov.moda.dw.manager.service.mapper;

import gov.moda.dw.manager.domain.OtpVerify;
import gov.moda.dw.manager.service.dto.OtpVerifyDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link OtpVerify} and its DTO {@link OtpVerifyDTO}.
 */
@Mapper(componentModel = "spring")
public interface OtpVerifyMapper extends EntityMapper<OtpVerifyDTO, OtpVerify> {
}
