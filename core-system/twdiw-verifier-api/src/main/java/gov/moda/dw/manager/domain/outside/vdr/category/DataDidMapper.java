package gov.moda.dw.manager.domain.outside.vdr.category;

import org.mapstruct.Mapper;

import gov.moda.dw.manager.domain.Rel;
import gov.moda.dw.manager.service.dto.RelDTO;
import gov.moda.dw.manager.service.mapper.EntityMapper;


/**
 * Mapper for the entity {@link Rel} and its DTO {@link RelDTO}.
 */
@Mapper(componentModel = "spring")
public interface DataDidMapper extends EntityMapper<DataDidDto, DataDid> {}
