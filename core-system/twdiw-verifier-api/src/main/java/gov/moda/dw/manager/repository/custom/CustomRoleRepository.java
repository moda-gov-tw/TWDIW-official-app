package gov.moda.dw.manager.repository.custom;

import java.util.Optional;
import gov.moda.dw.manager.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Role entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CustomRoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {
  Optional<Role> findByRoleId(@Param("roleId") String roleId);
}
