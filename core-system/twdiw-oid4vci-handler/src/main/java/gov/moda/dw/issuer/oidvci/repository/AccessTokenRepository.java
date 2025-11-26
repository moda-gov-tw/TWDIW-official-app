package gov.moda.dw.issuer.oidvci.repository;

import java.sql.Timestamp;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gov.moda.dw.issuer.oidvci.domain.AccessTokenEntity;

@Repository
public interface AccessTokenRepository extends JpaRepository<AccessTokenEntity, String>{
	Optional<AccessTokenEntity> findById(String token_value);

	@Modifying
	@Query("DELETE FROM AccessTokenEntity e WHERE e.expireTime < :expiredTime")
	void deleteByExpiredTime(@Param("expiredTime") Timestamp expire_time);
}
