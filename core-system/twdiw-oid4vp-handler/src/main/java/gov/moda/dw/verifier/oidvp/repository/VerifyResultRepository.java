package gov.moda.dw.verifier.oidvp.repository;

import gov.moda.dw.verifier.oidvp.domain.VerifyResultJpa;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface VerifyResultRepository extends JpaRepository<VerifyResultJpa, String> {

    Optional<VerifyResultJpa> findByResponseCode(String code);

    @Modifying
    @Transactional
    @Query(value = "delete from verify_result where transaction_id=?", nativeQuery = true)
    int deleteVerifyResultById(String id);

    @Modifying
    @Transactional
    @Query(value = "delete from verify_result where response_time < ?", nativeQuery = true)
    int deleteExpiredVerifyResult(LocalDateTime nowMinusAliveTime);
}
