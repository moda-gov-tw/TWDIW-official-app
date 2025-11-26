package gov.moda.dw.issuer.oidvci.service.mapper;

import gov.moda.dw.issuer.oidvci.domain.ApiTrack;
import gov.moda.dw.issuer.oidvci.service.dto.ApiTrackDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ApiTrack} and its DTO {@link ApiTrackDTO}.
 */
@Mapper(componentModel = "spring")
public interface ApiTrackMapper extends EntityMapper<ApiTrackDTO, ApiTrack> {}
