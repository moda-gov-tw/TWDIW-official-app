package gov.moda.dw.manager.service.mapper;

import gov.moda.dw.manager.domain.VCDataStatusLog;
import gov.moda.dw.manager.service.dto.VCDataStatusLogDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link VCDataStatusLog} and its DTO
 * {@link VCDataStatusLogDTO}.
 */
@Mapper(componentModel = "spring")
public interface VCDataStatusLogMapper extends EntityMapper<VCDataStatusLogDTO, VCDataStatusLog> {
}
