package gov.moda.dw.manager.repository;

import gov.moda.dw.manager.domain.LoginCount;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/** Spring Data JPA repository for the LoginCount entity. */
@SuppressWarnings("unused")
@Repository
public interface LoginCountRepository
    extends JpaRepository<LoginCount, Long>, JpaSpecificationExecutor<LoginCount> {
  Optional<LoginCount> findOneByUserId(String userId);
}
