package gov.moda.dw.issuer.oidvci.repository;

import gov.moda.dw.issuer.oidvci.domain.OidvciSettingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OidvciSettingRepository extends JpaRepository<OidvciSettingEntity, String> {

    List<OidvciSettingEntity> findBySettingName(String setting_name);
}
