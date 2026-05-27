package gov.moda.dw.issuer.oidvci.repository;

import gov.moda.dw.issuer.oidvci.domain.CredentialIssuerConfigEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CICRepository extends JpaRepository<CredentialIssuerConfigEntity, String> {

  List<CredentialIssuerConfigEntity> findByVcID(String vc_id);
}
