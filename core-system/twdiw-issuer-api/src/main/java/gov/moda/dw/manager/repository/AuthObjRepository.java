package gov.moda.dw.manager.repository;

import gov.moda.dw.manager.domain.AuthObj;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the AuthObj entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AuthObjRepository extends JpaRepository<AuthObj, String>, JpaSpecificationExecutor<AuthObj> {}
