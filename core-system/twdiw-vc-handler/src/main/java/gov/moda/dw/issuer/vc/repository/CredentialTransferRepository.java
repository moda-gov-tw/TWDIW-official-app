package gov.moda.dw.issuer.vc.repository;

import java.sql.Timestamp;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import gov.moda.dw.issuer.vc.domain.CredentialTransferEntity;

/**
 * Spring Data JPA repository for the {@link CredentialTransferEntity} entity.
 *
 * @version 20250428
 */
@Repository("CredentialTransferRepository")
public interface CredentialTransferRepository extends JpaRepository<CredentialTransferEntity, String>{

	Optional<CredentialTransferEntity> findByTransactionIDAndCredentialType(String transactionID, String credentialType);
	
	@Transactional
	@Modifying
	@Query(nativeQuery = true, value = "DELETE FROM credential_transfer c WHERE c.expired_time < :expiredTime")
	void deleteByExpiredTime(@Param("expiredTime") Timestamp expiredTime);
}
