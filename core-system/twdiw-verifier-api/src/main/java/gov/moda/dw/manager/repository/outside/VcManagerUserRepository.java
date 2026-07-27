package gov.moda.dw.manager.repository.outside;

import gov.moda.dw.manager.domain.outside.VcManagerUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface VcManagerUserRepository
    extends JpaRepository<VcManagerUser, Long>, JpaSpecificationExecutor<VcManagerUser> {

  Optional<VcManagerUser> findOneByLogin(String login);
}
