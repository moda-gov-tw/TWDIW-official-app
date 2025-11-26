package gov.moda.dw.issuer.oidvci.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gov.moda.dw.issuer.oidvci.domain.CredentialIssuerConfigEntity;

@Repository
public interface CICRepository extends JpaRepository<CredentialIssuerConfigEntity, String>{

	List<CredentialIssuerConfigEntity> findByVcID(String vc_id);
}
