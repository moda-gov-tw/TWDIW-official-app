package gov.moda.dw.manager.repository;

import gov.moda.dw.manager.domain.VPItemField;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the VPItemField entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VPItemFieldRepository extends JpaRepository<VPItemField, Long>, JpaSpecificationExecutor<VPItemField> {}
