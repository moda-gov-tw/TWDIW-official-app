package gov.moda.dw.manager.repository;

import gov.moda.dw.manager.domain.VPVerifyResult;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the VPVerifyResult entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VPVerifyResultRepository extends JpaRepository<VPVerifyResult, Long>, JpaSpecificationExecutor<VPVerifyResult> {}
