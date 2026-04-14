package gov.moda.dw.manager.repository;

import gov.moda.dw.manager.domain.ExtendedUserLog;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ExtendedUserLog entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ExtendedUserLogRepository extends JpaRepository<ExtendedUserLog, Long>, JpaSpecificationExecutor<ExtendedUserLog> {}
