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

    List<Org> findAllByOrderByOrgId();

    Optional<Org> findByOrgIdOrderByOrgId(String orgId);

    @Query("SELECT o.orgTwName FROM Org o")
    List<String> findAllOrgTwNames();

    @Query(value = "SELECT o.org_id FROM vc_manager.org o "
            + "WHERE o.org_tw_name LIKE '%' || CAST(:orgName AS TEXT) || '%'", nativeQuery = true)
    List<String> findOrgIdsByKeyword(String orgName);
}
