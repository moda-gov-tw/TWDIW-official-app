package gov.moda.dw.issuer.oidvci.repository;

import gov.moda.dw.issuer.oidvci.domain.Rel;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Rel entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RelRepository extends JpaRepository<Rel, Long>, JpaSpecificationExecutor<Rel> {}
