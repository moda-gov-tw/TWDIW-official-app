package gov.moda.dw.manager.service.mapper;

import gov.moda.dw.manager.domain.ApiTrack;
import gov.moda.dw.manager.service.dto.ApiTrackDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ApiTrack} and its DTO {@link ApiTrackDTO}.
 */
@Mapper(componentModel = "spring")
public interface ApiTrackMapper extends EntityMapper<ApiTrackDTO, ApiTrack> {}
