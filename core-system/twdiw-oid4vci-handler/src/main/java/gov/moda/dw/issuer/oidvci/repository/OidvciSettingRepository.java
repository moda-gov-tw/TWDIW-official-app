package gov.moda.dw.issuer.oidvci.repository;

import gov.moda.dw.issuer.oidvci.domain.OidvciSettingEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OidvciSettingRepository extends JpaRepository<OidvciSettingEntity, String> {

  List<OidvciSettingEntity> findBySettingName(String setting_name);
}
