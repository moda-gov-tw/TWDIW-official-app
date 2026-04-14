package gov.moda.dw.manager.service.mapper;

import gov.moda.dw.manager.domain.BwdParam;
import gov.moda.dw.manager.service.dto.BwdParamDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link BwdParam} and its DTO {@link BwdParamDTO}.
 */
@Mapper(componentModel = "spring")
public interface BwdParamMapper extends EntityMapper<BwdParamDTO, BwdParam> {}
