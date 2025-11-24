package gov.moda.dw.issuer.vc.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.moda.dw.issuer.vc.service.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gov.moda.dw.issuer.vc.domain.Setting;
import gov.moda.dw.issuer.vc.repository.SettingRepository;
import gov.moda.dw.issuer.vc.util.Tuple;
import gov.moda.dw.issuer.vc.vo.PreloadSetting;
import gov.moda.dw.issuer.vc.vo.VcException;

/**
 * setting service
 *
 * @version 20250218
 */
@Service
public class SettingService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SettingService.class);
	
	private final PreloadSetting preloadSetting;
	
	private final SettingRepository settingRepository;
	
	public SettingService(PreloadSetting preloadSetting, SettingRepository settingRepository) {
		this.preloadSetting = preloadSetting;
		this.settingRepository = settingRepository;
	}
	
	public Tuple.Pair<String, HttpStatus> checkSetting(){
		
		String result;
        HttpStatus httpStatus;
        
        try {
        	Map<String, List<String>> settingComparisonMap = preloadSetting.getPreloadSettingValueAndDBValue();
        	if(settingComparisonMap == null || settingComparisonMap.isEmpty()) {
        		throw new VcException(VcException.ERR_SYS_GET_PRELOAD_SETTING_ERROR, "fail to get preload setting value and db value");
        	}
        	
        	result = new SettingCheckResponseDTO().setData(settingComparisonMap).toString();
        	httpStatus = HttpStatus.OK;
        } catch (VcException e) {

            LOGGER.error(e.getMessage(), e);
			ErrorResponseProperty e1 = (ErrorResponseProperty) e;
            result = new ErrorResponseDTO(e1).toString();
            httpStatus = e.toHttpStatus();

        } catch (Exception e) {

            LOGGER.error(e.getMessage(), e);
            VcException vcException = new VcException(VcException.ERR_SYS_CHECK_SETTING_ERROR, "fail to check setting value");
            result = new ErrorResponseDTO(vcException).toString();
            httpStatus = vcException.toHttpStatus();
        }
        
        return Tuple.collect(result, httpStatus);
	}
	
	public Tuple.Pair<String, HttpStatus> updateSetting(SettingUpdateRequestDTO settingUpdateRequest){
		
		String result;
        HttpStatus httpStatus;
        
        try {
        	// step 1: validate request
        	if(settingUpdateRequest == null || !settingUpdateRequest.validate()) {
        		throw new VcException(VcException.ERR_SETTING_INVALID_UPDATE_SETTING_REQUEST, "invalid update setting request");
        	}
        	
        	Map<String, String> updates = settingUpdateRequest.getUpdates();
        	Map<String, String> updatedData = new HashMap<>();
        	
        	LOGGER.info("""
                    [updatesetting request]::
                    updates = {}
                    """, updates);
        	
        	for (Map.Entry<String, String> entry : updates.entrySet()) {
        		settingRepository.updatePropValueByPropName(entry.getKey(), entry.getValue());
        	}
        	
        	for (Map.Entry<String, String> entry : updates.entrySet()) {
        		updatedData.put(entry.getKey(), settingRepository.queryByPropName(entry.getKey()));
        	}
        	
        	result = new SettingUpdateResponseDTO().setUpdatedData(updatedData).toString();
        	httpStatus = HttpStatus.OK;
        	
        } catch (VcException e) {

            LOGGER.error(e.getMessage(), e);
			ErrorResponseProperty e1 = (ErrorResponseProperty) e;
            result = new ErrorResponseDTO(e1).toString();
            httpStatus = e.toHttpStatus();

        } catch (Exception e) {

            LOGGER.error(e.getMessage(), e);
            VcException vcException = new VcException(VcException.ERR_SYS_UPDATE_SETTING_ERROR, "fail to update setting value");
            result = new ErrorResponseDTO(vcException).toString();
            httpStatus = vcException.toHttpStatus();
        }
        
        return Tuple.collect(result, httpStatus);
	}
}
