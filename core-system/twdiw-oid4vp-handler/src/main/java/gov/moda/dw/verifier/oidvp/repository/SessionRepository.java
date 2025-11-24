package gov.moda.dw.verifier.oidvp.repository;

import gov.moda.dw.verifier.oidvp.domain.SessionJpa;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface SessionRepository extends JpaRepository<SessionJpa, String> {

    Optional<SessionJpa> findByState(String state);

    @Modifying
    @Transactional
    @Query(value = "update session set validated='true' where state=? and validated='false'", nativeQuery = true)
    int updateValidated(String state);

    @Modifying
    @Transactional
    @Query(value = "delete from session where transaction_id=?", nativeQuery = true)
    int deleteSessionById(String id);

    @Modifying
    @Transactional
    @Query(value = "delete from session where expired_time < ?", nativeQuery = true)
    int deleteExpiredSession(LocalDateTime now);
}
