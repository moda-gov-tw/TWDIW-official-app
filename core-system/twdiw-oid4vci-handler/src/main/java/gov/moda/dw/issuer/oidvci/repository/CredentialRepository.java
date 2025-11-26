package gov.moda.dw.issuer.oidvci.repository;

import gov.moda.dw.issuer.oidvci.domain.CredentialEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CredentialRepository extends JpaRepository<CredentialEntity, String>{

    List<CredentialEntity> findByCID(String cid);
}
