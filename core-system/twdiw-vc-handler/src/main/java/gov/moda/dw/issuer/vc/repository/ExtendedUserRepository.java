package gov.moda.dw.issuer.vc.repository;

import gov.moda.dw.issuer.vc.domain.ExtendedUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the ExtendedUser entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ExtendedUserRepository extends JpaRepository<ExtendedUser, Long>, JpaSpecificationExecutor<ExtendedUser> {
  Optional<ExtendedUser> findOneByUserId(String userId);
  Optional<ExtendedUser> findByUserId(String userId);
}
