package gov.moda.dw.issuer.oidvci.repository;


import java.util.List;

import gov.moda.dw.issuer.oidvci.domain.MetaDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MetaDataRepository extends JpaRepository<MetaDataEntity, String>{

    List<MetaDataEntity> findByOrgId(String org_id);
}
