package gov.moda.dw.issuer.vc.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import gov.moda.dw.issuer.vc.domain.CredentialPolicyEntity;


/**
 * Spring Data JPA repository for the {@link CredentialPolicyEntity} entity.
 *
 * @version 20241016
 */
@Repository("CredentialPolicyRepository")
public interface CredentialPolicyRepository extends JpaRepository<CredentialPolicyEntity, String> {

	Optional<CredentialPolicyEntity> findByCredentialType(String credentialType);
	
	@Transactional
	@Modifying
	@Query(nativeQuery = true, value = "DELETE FROM credential_policy cp WHERE cp.credential_type = :credentialType")
	int deleteByCredentialType(@Param("credentialType") String credentialType);
	
	@Query(nativeQuery = true, value = "SELECT func_switch FROM credential_policy WHERE credential_type = :credential_type")
	String queryFuncSwitchByCredentialType(@Param("credential_type") String credentialType);
}
