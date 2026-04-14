package gov.moda.dw.manager.repository;

import gov.moda.dw.manager.domain.OtpVerify;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the OtpVerify entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OtpVerifyRepository extends JpaRepository<OtpVerify, Long>, JpaSpecificationExecutor<OtpVerify> {
}
