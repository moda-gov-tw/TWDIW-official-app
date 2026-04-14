package gov.moda.dw.manager.repository;

import gov.moda.dw.manager.domain.LoginView;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the LoginView entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LoginViewRepository extends JpaRepository<LoginView, Long>, JpaSpecificationExecutor<LoginView> {}
