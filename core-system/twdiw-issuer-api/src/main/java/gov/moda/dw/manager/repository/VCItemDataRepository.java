package gov.moda.dw.manager.repository;

import gov.moda.dw.manager.domain.VCItemData;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the VCItemData entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VCItemDataRepository extends JpaRepository<VCItemData, Long>, JpaSpecificationExecutor<VCItemData> {
    VCItemData findByTransactionId(String transactionId);
}
