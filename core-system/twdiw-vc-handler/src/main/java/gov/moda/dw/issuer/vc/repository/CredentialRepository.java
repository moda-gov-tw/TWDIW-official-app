package gov.moda.dw.issuer.vc.repository;

import gov.moda.dw.issuer.vc.domain.Credential;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

/**
 * Spring Data JPA repository for the {@link Credential} entity.
 *
 * @version 20240902
 */
@Repository("CredentialRepository")
public interface CredentialRepository extends JpaRepository<Credential, String> {

    Credential findByCid(String cid);

    // only accept the newest one when nonce collision occurs
    Credential findTopByNonceOrderByIssuanceDateDesc(String nonce);

    // List<Credential> findAllByOrderByIssuanceDateDesc();

    @Query(nativeQuery = true, value = "SELECT * FROM credential WHERE credential_type = :credential_type")
    Page<Credential> findAllByPages(@Param("credential_type") String credentialType, Pageable pageable);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "UPDATE credential SET credential_status = :credential_status, last_update_time = :last_update_time WHERE cid = :cid")
    int updateStatusByCid(@Param("cid") String cid, @Param("credential_status") String credentialStatus, @Param("last_update_time") Timestamp lastUpdateTime);
}
