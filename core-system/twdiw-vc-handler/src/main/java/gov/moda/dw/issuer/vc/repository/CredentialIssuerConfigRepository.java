package gov.moda.dw.issuer.vc.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gov.moda.dw.issuer.vc.domain.CredentialIssuerConfigEntity;

/**
 * Spring Data JPA repository for the {@link CredentialIssuerConfigEntity} entity.
 *
 * @version 20241028
 */
@Repository("CredentialIssuerConfigRepository")
public interface CredentialIssuerConfigRepository extends JpaRepository<CredentialIssuerConfigEntity, String>{

	Optional<CredentialIssuerConfigEntity> findByVcID(String vc_ld);
}
