package gov.moda.dw.manager.repository;

import java.util.List;
import java.util.Optional;
import gov.moda.dw.manager.domain.ExtendedUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ExtendedUser entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ExtendedUserRepository extends JpaRepository<ExtendedUser, Long>, JpaSpecificationExecutor<ExtendedUser> {
  Optional<ExtendedUser> findOneByUserId(String userId);

    long countByOrgIdIn(List<String> orgIds);
    long countByOrgIdNotIn(List<String> orgIds);
    @Query("SELECT u.userId FROM ExtendedUser u")
    List<String> findAllUserIds();

    List<ExtendedUser> findAllByOrgId(String orgId);
}
