package gov.moda.dw.manager.repository;

import gov.moda.dw.manager.domain.VCItemField;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the VCItemField entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VCItemFieldRepository extends JpaRepository<VCItemField, Long>, JpaSpecificationExecutor<VCItemField> {}
