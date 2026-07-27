package gov.moda.dw.manager.repository.custom;

import gov.moda.dw.manager.domain.Setting;
import gov.moda.dw.manager.repository.SettingRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomSettingRepository extends SettingRepository {
  Optional<Setting> findByPropName(String propName);

  void deleteByPropName(String propName);

  boolean existsByPropName(String propName);
}
