package gov.moda.dw.issuer.oidvci.repository;

import gov.moda.dw.issuer.oidvci.domain.ApiTrack;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ApiTrack entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ApiTrackRepository extends JpaRepository<ApiTrack, Long>, JpaSpecificationExecutor<ApiTrack> {}
