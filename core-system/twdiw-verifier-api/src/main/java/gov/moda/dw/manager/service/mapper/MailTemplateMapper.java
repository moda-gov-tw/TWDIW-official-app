package gov.moda.dw.manager.service.mapper;

import gov.moda.dw.manager.domain.MailTemplate;
import gov.moda.dw.manager.service.dto.MailTemplateDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MailTemplate} and its DTO {@link MailTemplateDTO}.
 */
@Mapper(componentModel = "spring")
public interface MailTemplateMapper extends EntityMapper<MailTemplateDTO, MailTemplate> {}
