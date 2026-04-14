package gov.moda.dw.manager.service.mapper;

import gov.moda.dw.manager.domain.BwdHistory;
import gov.moda.dw.manager.service.dto.BwdHistoryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link BwdHistory} and its DTO {@link BwdHistoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface BwdHistoryMapper extends EntityMapper<BwdHistoryDTO, BwdHistory> {}
