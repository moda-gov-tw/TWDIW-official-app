package gov.moda.dw.manager.repository;

import gov.moda.dw.manager.domain.Org;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Org entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrgRepository
  extends JpaRepository<Org, Long>, JpaSpecificationExecutor<Org> {}
