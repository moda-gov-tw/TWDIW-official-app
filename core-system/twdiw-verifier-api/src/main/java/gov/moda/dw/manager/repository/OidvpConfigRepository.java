package gov.moda.dw.manager.repository;

import gov.moda.dw.manager.domain.OidvpConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface OidvpConfigRepository extends JpaRepository<OidvpConfig, Long>, JpaSpecificationExecutor<OidvpConfig> {
}
