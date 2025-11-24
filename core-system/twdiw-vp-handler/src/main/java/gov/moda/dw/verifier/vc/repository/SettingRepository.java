package gov.moda.dw.verifier.vc.repository;

import gov.moda.dw.verifier.vc.domain.Setting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("SettingRepository")
public interface SettingRepository extends JpaRepository<Setting, String> {
}
