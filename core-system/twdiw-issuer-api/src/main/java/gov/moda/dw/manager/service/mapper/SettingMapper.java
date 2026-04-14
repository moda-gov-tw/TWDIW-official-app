package gov.moda.dw.manager.service.mapper;

import gov.moda.dw.manager.domain.Setting;
import gov.moda.dw.manager.service.dto.SettingDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SettingMapper extends EntityMapper<SettingDTO, Setting> {}
