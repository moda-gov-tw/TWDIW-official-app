package gov.moda.dw.issuer.vc.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import gov.moda.dw.issuer.vc.domain.Setting;
import gov.moda.dw.issuer.vc.repository.SettingRepository;
import gov.moda.dw.issuer.vc.service.dto.ErrorResponseDTO;
import gov.moda.dw.issuer.vc.util.KeyUtils;
import gov.moda.dw.issuer.vc.vo.VcException;

/**
 * Startup check
 *
 * @version 20250609
 */
@Component
public class StartupCheckService implements ApplicationRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(StartupCheckService.class);
	
	private final SettingRepository settingRepository;
	
	@Value("${vc.key.enc}")
    private String vcKeyEnc;
	
	private static final List<String> TARGET_KEYS = List.of("status.list.key", "issuer.key");
	
	public StartupCheckService(SettingRepository settingRepository) {
		this.settingRepository = settingRepository;
	}
	
	@Transactional
    @Override
	public void run(ApplicationArguments args) throws VcException{
		
		try {
			settingRepository.createProcedure();
			List<Setting> settings = settingRepository.queryTwoPropValuesByPropNameForUpdate(TARGET_KEYS);
			String iv = KeyUtils.genIV();
			
			for(Setting setting : settings) {
				String propValue = setting.getPropValue();
				if(propValue == null || propValue.isBlank()) {
					LOGGER.warn("[StartupCheckService]: issuer has not yet set the {}", setting.getPropName());
				} else {
					try {
						if(KeyUtils.isPossiblyJwk(propValue)) {
							String propValueCipher = KeyUtils.GCMEncrypt(propValue, vcKeyEnc, iv);
							setting.setPropValue(propValueCipher);
							LOGGER.info("[StartupCheckService]: 已加密並更新: {}", setting.getPropName());
						}else {
							LOGGER.info("[StartupCheckService]: {} 已加密，無需更新", setting.getPropName());
						}
					}catch(Exception e) {
						LOGGER.error(e.getMessage(), e);
		            	throw new VcException(VcException.ERR_SYS_ENCRYPT_ERROR, "Encrypt key error");
					}
				}
			}
			
			settingRepository.saveAll(settings);
		} catch (PessimisticLockingFailureException e) {
			LOGGER.warn("目前已由另一個pod在執行檢查作業，跳過本次作業");
		} catch (VcException e) {

        	LOGGER.error(e.getMessage(), e);
        	throw new VcException(VcException.ERR_SYS_CHECK_KEYS_VALUE_ERROR, "fail to check keys value");

        } catch (Exception e) {

            LOGGER.error(e.getMessage(), e);
            throw new VcException(VcException.ERR_SYS_CHECK_KEYS_VALUE_ERROR, "fail to check keys value");
        }
	}
}
