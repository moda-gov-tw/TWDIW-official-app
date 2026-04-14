package gov.moda.dw.manager.repository.custom;

import java.util.List;
import gov.moda.dw.manager.repository.AuthorityRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AmsAuthorityRepository extends AuthorityRepository {
  @Query(value = "select distinct rel.rightCode \n" + "from Rel rel \n" + "where (rel.leftCode = :login) \n")
  List<String> findByLogin(@Param("login") String login);

  @Query(value = "select distinct rel.rightCode \n" + "from Rel rel \n" + "where (rel.leftCode = :roleId) \n")
  List<String> findByRole(@Param("roleId") String roleId);

  @Query(value = "select distinct rel.rightCode \n" + "from Rel rel \n" + "where (rel.leftCode = :accessToken) \n")
  List<String> findByAccessToken(@Param("accessToken") String accessToken);

  @Query(
    value = "select distinct rel.rightCode \n" +
    "from Rel rel \n" +
    "where (rel.leftTbl = 'Role' and rel.rightTbl = 'Res' and rel.leftCode = :roleId) \n"
  )
  List<String> findByRoleByRoleAndRes(@Param("roleId") String roleId);
}
