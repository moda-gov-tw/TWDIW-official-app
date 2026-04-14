package gov.moda.dw.manager.repository;

import gov.moda.dw.manager.domain.OrgKeySetting;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the OrgKeySetting entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrgKeySettingRepository extends JpaRepository<OrgKeySetting, Long>, JpaSpecificationExecutor<OrgKeySetting> {}
