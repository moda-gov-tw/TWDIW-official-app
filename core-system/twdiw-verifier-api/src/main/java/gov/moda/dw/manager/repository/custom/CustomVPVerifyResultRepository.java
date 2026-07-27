package gov.moda.dw.manager.repository.custom;

import gov.moda.dw.manager.domain.VPVerifyResult;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/** Spring Data JPA repository for the VPVerifyResult entity. */
@SuppressWarnings("unused")
@Repository
public interface CustomVPVerifyResultRepository
    extends JpaRepository<VPVerifyResult, Long>, JpaSpecificationExecutor<VPVerifyResult> {

  List<VPVerifyResult> findByTransactionId(String transactionId);
}
