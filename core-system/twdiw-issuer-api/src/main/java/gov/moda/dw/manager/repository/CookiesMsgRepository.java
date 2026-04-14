package gov.moda.dw.manager.repository;

import gov.moda.dw.manager.domain.CookiesMsg;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CookiesMsg entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CookiesMsgRepository extends JpaRepository<CookiesMsg, Long>, JpaSpecificationExecutor<CookiesMsg> {}
