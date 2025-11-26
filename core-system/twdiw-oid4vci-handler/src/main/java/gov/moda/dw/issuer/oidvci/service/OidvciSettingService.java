package gov.moda.dw.issuer.oidvci.service;

import gov.moda.dw.issuer.oidvci.domain.OidvciSettingEntity;
import gov.moda.dw.issuer.oidvci.repository.OidvciSettingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OidvciSettingService {
    private static final Logger log = LoggerFactory.getLogger(OidvciSettingService.class);

    private final OidvciSettingRepository oidvciSettingRepository;

    public OidvciSettingService(OidvciSettingRepository oidvciSettingRepository) {
        this.oidvciSettingRepository = oidvciSettingRepository;
    }

    public List<OidvciSettingEntity> getOidvciSetting(String setting_name) {
        return oidvciSettingRepository.findBySettingName(setting_name);
    }

}
