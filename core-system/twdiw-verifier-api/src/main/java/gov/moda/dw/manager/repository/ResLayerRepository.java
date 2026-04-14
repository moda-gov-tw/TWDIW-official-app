package gov.moda.dw.manager.repository;

import gov.moda.dw.manager.domain.ResLayer;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ResLayer entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ResLayerRepository extends JpaRepository<ResLayer, Long>, JpaSpecificationExecutor<ResLayer> {}
