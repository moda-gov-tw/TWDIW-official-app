package gov.moda.dw.issuer.vc.repository;

import gov.moda.dw.issuer.vc.domain.ExtendedUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/** Spring Data JPA repository for the ExtendedUser entity. */
@SuppressWarnings("unused")
@Repository
public interface ExtendedUserRepository
    extends JpaRepository<ExtendedUser, Long>, JpaSpecificationExecutor<ExtendedUser> {
  Optional<ExtendedUser> findOneByUserId(String userId);

  Optional<ExtendedUser> findByUserId(String userId);
}
