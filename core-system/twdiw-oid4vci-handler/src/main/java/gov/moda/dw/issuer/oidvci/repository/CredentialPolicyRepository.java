package gov.moda.dw.issuer.oidvci.repository;

import gov.moda.dw.issuer.oidvci.domain.CredentialPolicyEntity;
import gov.moda.dw.issuer.oidvci.domain.OidvciSettingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CredentialPolicyRepository extends JpaRepository<CredentialPolicyEntity, String> {

    List<CredentialPolicyEntity> findByCredentialType(String credential_type);
}

