package gov.moda.dw.manager.repository.custom;

import gov.moda.dw.manager.domain.Setting;
import gov.moda.dw.manager.repository.SettingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomSettingRepository extends SettingRepository {
    Optional<Setting> findByPropName(String propName);

    void deleteByPropName(String propName);

    boolean existsByPropName(String propName);
}
