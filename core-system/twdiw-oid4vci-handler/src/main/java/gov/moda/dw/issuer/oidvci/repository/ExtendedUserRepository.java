package gov.moda.dw.issuer.oidvci.repository;

import java.util.Optional;
import gov.moda.dw.issuer.oidvci.domain.ExtendedUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ExtendedUser entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ExtendedUserRepository extends JpaRepository<ExtendedUser, Long>, JpaSpecificationExecutor<ExtendedUser> {
  Optional<ExtendedUser> findOneByUserId(String userId);
}
