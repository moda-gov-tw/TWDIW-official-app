package gov.moda.dw.manager.service.mapper;

import gov.moda.dw.manager.domain.Schedule;
import gov.moda.dw.manager.service.dto.ScheduleDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Schedule} and its DTO {@link ScheduleDTO}.
 */
@Mapper(componentModel = "spring")
public interface ScheduleMapper extends EntityMapper<ScheduleDTO, Schedule> {}
