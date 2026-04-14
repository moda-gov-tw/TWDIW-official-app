package gov.moda.dw.manager.repository;

import gov.moda.dw.manager.domain.VCItem;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the VCItem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VCItemRepository extends JpaRepository<VCItem, Long>, JpaSpecificationExecutor<VCItem> {}
