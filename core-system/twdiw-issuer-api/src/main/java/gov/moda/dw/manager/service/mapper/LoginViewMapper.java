package gov.moda.dw.manager.service.mapper;

import gov.moda.dw.manager.domain.LoginView;
import gov.moda.dw.manager.service.dto.LoginViewDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link LoginView} and its DTO {@link LoginViewDTO}.
 */
@Mapper(componentModel = "spring")
public interface LoginViewMapper extends EntityMapper<LoginViewDTO, LoginView> {}
