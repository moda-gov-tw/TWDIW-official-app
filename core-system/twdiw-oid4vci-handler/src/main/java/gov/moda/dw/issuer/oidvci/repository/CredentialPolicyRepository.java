package gov.moda.dw.issuer.oidvci.repository;

import gov.moda.dw.issuer.oidvci.domain.CredentialPolicyEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CredentialPolicyRepository extends JpaRepository<CredentialPolicyEntity, String> {

  List<CredentialPolicyEntity> findByCredentialType(String credential_type);
}
