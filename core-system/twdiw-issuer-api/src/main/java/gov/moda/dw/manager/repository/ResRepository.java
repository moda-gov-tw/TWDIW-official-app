package gov.moda.dw.manager.repository;

import gov.moda.dw.manager.domain.Res;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Res entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ResRepository extends JpaRepository<Res, Long>, JpaSpecificationExecutor<Res> {}
