package gov.moda.dw.manager.service.mapper;

import gov.moda.dw.manager.domain.AuthObj;
import gov.moda.dw.manager.service.dto.AuthObjDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AuthObj} and its DTO {@link AuthObjDTO}.
 */
@Mapper(componentModel = "spring")
public interface AuthObjMapper extends EntityMapper<AuthObjDTO, AuthObj> {}
