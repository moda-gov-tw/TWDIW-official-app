package gov.moda.dw.manager.service.mapper;

import gov.moda.dw.manager.domain.OrgKeySetting;
import gov.moda.dw.manager.service.dto.OrgKeySettingDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link OrgKeySetting} and its DTO {@link OrgKeySettingDTO}.
 */
@Mapper(componentModel = "spring")
public interface OrgKeySettingMapper extends EntityMapper<OrgKeySettingDTO, OrgKeySetting> {}
