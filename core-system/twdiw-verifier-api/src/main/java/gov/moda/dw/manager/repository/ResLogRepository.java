package gov.moda.dw.manager.repository;

import gov.moda.dw.manager.domain.ResLog;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ResLog entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ResLogRepository extends JpaRepository<ResLog, Long>, JpaSpecificationExecutor<ResLog> {}
