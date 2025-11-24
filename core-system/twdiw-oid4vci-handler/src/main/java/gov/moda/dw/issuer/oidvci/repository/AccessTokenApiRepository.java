package gov.moda.dw.issuer.oidvci.repository;

import gov.moda.dw.issuer.oidvci.domain.AccessToken;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the AccessToken entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AccessTokenApiRepository extends JpaRepository<AccessToken, Long>, JpaSpecificationExecutor<AccessToken> {}
