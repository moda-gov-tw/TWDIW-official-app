package gov.moda.dw.manager.repository.outside;

import gov.moda.dw.manager.domain.outside.VcManagerOrg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface VcManagerOrgRepository extends JpaRepository<VcManagerOrg, Long>, JpaSpecificationExecutor<VcManagerOrg> {}
