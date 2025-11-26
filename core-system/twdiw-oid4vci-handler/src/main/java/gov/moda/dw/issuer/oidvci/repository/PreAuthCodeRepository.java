package gov.moda.dw.issuer.oidvci.repository;

import java.sql.Timestamp;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import gov.moda.dw.issuer.oidvci.domain.PreAuthCodeEntity;
import gov.moda.dw.issuer.oidvci.domain.PreAuthCodeId;

public interface PreAuthCodeRepository extends JpaRepository<PreAuthCodeEntity, PreAuthCodeId>{
	Optional<PreAuthCodeEntity> findById(PreAuthCodeId id);
	
	Optional<PreAuthCodeEntity> findByClientIDAndPreCode(String client_id, String pre_code);
	
	@Modifying
	@Query("DELETE FROM PreAuthCodeEntity e WHERE e.expireTime < :expiredTime")
	void deleteByExpiredTime(@Param("expiredTime") Timestamp expire_time);
}
