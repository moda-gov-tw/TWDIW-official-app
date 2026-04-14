package gov.moda.dw.manager.repository.custom;

import gov.moda.dw.manager.domain.Schedule;
import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Schedule entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CustomScheduleRepository extends JpaRepository<Schedule, Long>, JpaSpecificationExecutor<Schedule> {
    @Modifying
    @Query(
        value = "UPDATE schedule  SET last_run_datetime = :lastRunDatetime WHERE id =:id and ( last_run_datetime is null or ( :lastRunDatetime - last_run_datetime  >= interval '5 minutes')  or (last_run_datetime - :lastRunDatetime  >= interval '5 minutes')  ) ",
        nativeQuery = true
    )
    int updateLastRunDatetime(@Param("id") Long id, @Param("lastRunDatetime") Instant lastRunDatetime);
}
