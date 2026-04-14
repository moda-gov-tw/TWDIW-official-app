package gov.moda.dw.manager.service.mapper;

import gov.moda.dw.manager.domain.ImgVerifyCode;
import gov.moda.dw.manager.service.dto.ImgVerifyCodeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ImgVerifyCode} and its DTO {@link ImgVerifyCodeDTO}.
 */
@Mapper(componentModel = "spring")
public interface ImgVerifyCodeMapper extends EntityMapper<ImgVerifyCodeDTO, ImgVerifyCode> {}
