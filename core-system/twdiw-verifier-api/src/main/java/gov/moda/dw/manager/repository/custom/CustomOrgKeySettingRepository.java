package gov.moda.dw.manager.repository.custom;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import gov.moda.dw.manager.domain.OrgKeySetting;

/**
 * Spring Data JPA repository for the OrgKeySetting entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CustomOrgKeySettingRepository
        extends JpaRepository<OrgKeySetting, Long>, JpaSpecificationExecutor<OrgKeySetting> {

    Optional<OrgKeySetting> findById(Long id);

    Optional<OrgKeySetting> findByIsActiveTrue();

    List<OrgKeySetting> findByOrgIdAndIsActiveTrue(String orgId);

    List<OrgKeySetting> findByKeyIdAndOrgIdAndIsActiveTrue(String keyId, String orgId);

    List<OrgKeySetting> findByKeyIdAndIsActiveTrue(String keyId);

    Optional<OrgKeySetting> findByKeyId(String keyId);
    
    Optional<OrgKeySetting> findByOrgIdAndKeyId(String orgId, String keyId);

    long countByOrgId(String orgId);

    Optional<OrgKeySetting> findByIdAndOrgId(Long id, String orgId);
}
