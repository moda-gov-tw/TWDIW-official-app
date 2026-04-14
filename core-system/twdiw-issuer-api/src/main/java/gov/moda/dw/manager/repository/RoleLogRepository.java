package gov.moda.dw.manager.repository;

import gov.moda.dw.manager.domain.RoleLog;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the RoleLog entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RoleLogRepository extends JpaRepository<RoleLog, Long>, JpaSpecificationExecutor<RoleLog> {}
