package gov.moda.dw.manager.repository;

import gov.moda.dw.manager.domain.VCDataStatusLog;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the VCDataStatusLog entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VCDataStatusLogRepository
        extends JpaRepository<VCDataStatusLog, Long>, JpaSpecificationExecutor<VCDataStatusLog> {
}
