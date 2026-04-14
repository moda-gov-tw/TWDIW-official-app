package gov.moda.dw.manager.repository.custom;

import java.util.List;
import gov.moda.dw.manager.repository.RoleRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AmsRoleRepository extends RoleRepository {
  @Query( //User::Role
    value = "select distinct rel.rightCode \n" +
    "from Rel rel \n" +
    "where (rel.leftTbl = 'User' and rel.rightTbl = 'Role' and rel.leftCode = :login) \n"
  )
  List<String> findByLogin(@Param("login") String login);

  @Query( //User::Role
    value = "select distinct rel.leftCode \n" +
    "from Rel rel \n" +
    "where (rel.leftTbl = 'User' and rel.rightTbl = 'Role' and rel.rightCode = :roleId) \n"
  )
  List<String> findByRoleId(@Param("roleId") String roleId);
}
