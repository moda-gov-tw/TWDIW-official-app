package gov.moda.dw.manager.service.mapper;

import gov.moda.dw.manager.domain.VPVerifyResult;
import gov.moda.dw.manager.service.dto.VPVerifyResultDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link VPVerifyResult} and its DTO {@link VPVerifyResultDTO}.
 */
@Mapper(componentModel = "spring")
public interface VPVerifyResultMapper extends EntityMapper<VPVerifyResultDTO, VPVerifyResult> {}
