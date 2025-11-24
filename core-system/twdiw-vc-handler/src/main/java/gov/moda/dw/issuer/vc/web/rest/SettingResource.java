package gov.moda.dw.issuer.vc.web.rest;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.moda.dw.issuer.vc.service.SettingService;
import gov.moda.dw.issuer.vc.service.dto.SettingUpdateRequestDTO;
import gov.moda.dw.issuer.vc.util.Console;
import gov.moda.dw.issuer.vc.util.Tuple;

/**
 * setting controller
 *
 * @version 20250214
 */
@RestController
@RequestMapping("/api")
public class SettingResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(SettingResource.class);
	
	private final SettingService settingService;
	
	public SettingResource(SettingService settingService) {
		this.settingService = settingService;
	}
	
	/**
     * check setting
     *
     * @return preload setting value & if it is identical with DB value
     */
	@GetMapping(path = "/checksetting", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> checkSetting(){
		
		Tuple.Pair<String, HttpStatus> result = settingService.checkSetting();
		
		LOGGER.info("[HTTP status]:: {}", result.getB().toString());
        LOGGER.info("[checkSetting result]:: {}\n", result.getA());
        Console.showJson(result.getA(), "setting check response");
        
        // with response content when http status = 201/400/404/500
        if (result.getB() == HttpStatus.OK ||
            result.getB() == HttpStatus.BAD_REQUEST ||
            result.getB() == HttpStatus.NOT_FOUND ||
            result.getB() == HttpStatus.INTERNAL_SERVER_ERROR) {
            return new ResponseEntity<>(result.getA(), result.getB());
        } else {
            return new ResponseEntity<>(result.getB());
        }
	}
	
	/**
     * update setting
     * @param settingUpdateRequest, name-value json
     * @return DB setting
     */
	@PostMapping(path = "/updatesetting", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> updateSetting(@RequestBody SettingUpdateRequestDTO settingUpdateRequest){
		
		Map<String, String> updates = null;

		if(settingUpdateRequest != null && settingUpdateRequest.validate()) {
			LOGGER.info("TEST");
			updates = settingUpdateRequest.getUpdates();
		}
		
		LOGGER.info("""
                [updatesetting request]::
                updates = {}
                """, updates);
		
		Tuple.Pair<String, HttpStatus> result = settingService.updateSetting(settingUpdateRequest);
		
		LOGGER.info("[HTTP status]:: {}", result.getB().toString());
        LOGGER.info("[updateSetting result]:: {}\n", result.getA());
        Console.showJson(result.getA(), "update setting response");
        
     // with response content when http status = 201/400/404/500
        if (result.getB() == HttpStatus.OK ||
            result.getB() == HttpStatus.BAD_REQUEST ||
            result.getB() == HttpStatus.NOT_FOUND ||
            result.getB() == HttpStatus.INTERNAL_SERVER_ERROR) {
            return new ResponseEntity<>(result.getA(), result.getB());
        } else {
            return new ResponseEntity<>(result.getB());
        }
	}
}
