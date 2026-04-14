package gov.moda.dw.manager.repository;

import gov.moda.dw.manager.domain.AccessToken;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the AccessToken entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AccessTokenRepository extends JpaRepository<AccessToken, Long>, JpaSpecificationExecutor<AccessToken> {}
