package gov.moda.dw.issuer.vc.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gov.moda.dw.issuer.vc.domain.CredentialSuspensionEntity;

/**
 * Spring Data JPA repository for the {@link CredentialSuspensionEntity} entity.
 *
 * @version 20250715
 */
@Repository("CredentialSuspensionRepository")
public interface CredentialSuspensionRepository extends JpaRepository<CredentialSuspensionEntity, String>{

	Optional<CredentialSuspensionEntity> findByCid(String cid);
}
