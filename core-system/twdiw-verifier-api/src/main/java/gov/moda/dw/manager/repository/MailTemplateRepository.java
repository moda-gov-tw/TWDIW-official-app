package gov.moda.dw.manager.repository;

import gov.moda.dw.manager.domain.MailTemplate;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the MailTemplate entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MailTemplateRepository extends JpaRepository<MailTemplate, Long>, JpaSpecificationExecutor<MailTemplate> {}
