package gov.moda.dw.manager.repository.custom;

import gov.moda.dw.manager.domain.Org;
import gov.moda.dw.manager.repository.OrgRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomOrgRepository extends OrgRepository {
  Optional<Org> findByOrgId(String orgId);

  void deleteByOrgId(String orgId);

  @Query("SELECT o.orgTwName FROM Org o")
  List<String> findAllOrgTwNames();

  Optional<Org> findByOrgIdOrderByOrgId(String orgId);
}
