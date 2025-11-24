package gov.moda.dw.issuer.vc.repository;

import java.sql.Timestamp;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import gov.moda.dw.issuer.vc.domain.CredentialDataEntity;

/**
 * Spring Data JPA repository for the {@link CredentialDataEntity} entity.
 *
 * @version 20241016
 */
@Repository("CredentialDataRepository")
public interface CredentialDataRepository extends JpaRepository<CredentialDataEntity, String>{

	Optional<CredentialDataEntity> findByTransactionIDAndCredentialType(String transactionID, String credentialType);
	
	@Transactional
	@Modifying
	@Query(nativeQuery = true, value = "DELETE FROM credential_data c WHERE c.expired_time < :expiredTime")
	void deleteByExpiredTime(@Param("expiredTime") Timestamp expiredTime);
}
