package gov.moda.dw.issuer.oidvci.repository;

import gov.moda.dw.issuer.oidvci.domain.Res;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Res entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ResRepository extends JpaRepository<Res, Long>, JpaSpecificationExecutor<Res> {}
