package gov.moda.dw.manager.repository;

import gov.moda.dw.manager.domain.ApiTrack;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ApiTrack entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ApiTrackRepository extends JpaRepository<ApiTrack, Long>, JpaSpecificationExecutor<ApiTrack> {}
