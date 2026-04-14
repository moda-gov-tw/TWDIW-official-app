package gov.moda.dw.manager.service.mapper;

import gov.moda.dw.manager.domain.Nonce;
import gov.moda.dw.manager.service.dto.NonceDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Nonce} and its DTO {@link NonceDTO}.
 */
@Mapper(componentModel = "spring")
public interface NonceMapper extends EntityMapper<NonceDTO, Nonce> {}
