package gov.moda.dw.issuer.vc.web.rest;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.moda.dw.issuer.vc.service.SequenceService;
import gov.moda.dw.issuer.vc.service.dto.ErrorResponseDTO;
import gov.moda.dw.issuer.vc.service.dto.FunctionSwitchRequestDTO;
import gov.moda.dw.issuer.vc.service.dto.SequenceDelRequestDTO;
import gov.moda.dw.issuer.vc.service.dto.SequenceRequestDTO;
import gov.moda.dw.issuer.vc.util.Console;
import gov.moda.dw.issuer.vc.util.Tuple;
import gov.moda.dw.issuer.vc.vo.VcException;

/**
 * sequence controller
 *
 * @version 20241015
 */
@RestController
@RequestMapping("/api")
public class SequenceResource {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SequenceResource.class);
	
	private final SequenceService sequenceService;
	
	public SequenceResource(SequenceService sequenceService) {
		this.sequenceService = sequenceService;
	}
	
	/**
     * generate sequence
     *
     * @param sequence generate request
     * @return execute result
     */
    @PostMapping(path = "/setseq/{credentialType}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addSeq(@PathVariable String credentialType, @RequestBody String sequenceRequestStr) {
    	Tuple.Pair<String, HttpStatus> result = null;
    	String schemaId = null;
    	Map<String, Object> vcSchema = null;
    	String effectiveTimeUnit = null;
    	int effectiveTimeValue = 0;
    	Map<String, Object> metadata = null;
    	
    	LOGGER.info("[addSeq]:: sequenceRequest string = {}", sequenceRequestStr);
    	
    	SequenceRequestDTO sequenceRequest = new SequenceRequestDTO(sequenceRequestStr);
    	
    	if(sequenceRequest != null && sequenceRequest.validate()) {
    		schemaId = sequenceRequest.getSchemaId();
    		vcSchema = sequenceRequest.getVcSchema();
    		effectiveTimeUnit = sequenceRequest.getEffectiveTimeUnit();
    		effectiveTimeValue = sequenceRequest.getEffectiveTimeValue();
    		metadata = sequenceRequest.getMetadata();
    	}
    	
    	LOGGER.info("[addSeq]::credentialType = {}, schemaId = {}, vcSchema = {}, effectiveTimeUnit = {}, effectiveTimeValue = {}, metadata = {}", 
    			credentialType,	schemaId, vcSchema, effectiveTimeUnit, effectiveTimeValue, metadata);
    	
    	try {
    		result = sequenceService.createSeqAndPolicyAndUpdateCredentialIssuerConfig(credentialType, 1, 1, sequenceRequest, sequenceRequestStr);
    	} catch (Exception e) {			// 建立序列失敗時，會產生rollback exception，在這邊才能夠處理
    		LOGGER.error(e.getMessage(), e);
    		VcException vcException = new VcException(VcException.ERR_DB_INSERT_ERROR, "fail to create sequence or policy");
            String msg = new ErrorResponseDTO(vcException).toString();
            HttpStatus httpStatus = vcException.toHttpStatus();
    		result = Tuple.collect(msg, httpStatus);
    	}
    	LOGGER.info("[HTTP status]:: {}", result.getB().toString());
        LOGGER.info("[create result]:: {}\n", result.getA());
        Console.showJson(result.getA(), "create sequence response");

        // with response content when http status = 201/400/404/500
        if (result.getB() == HttpStatus.CREATED ||
            result.getB() == HttpStatus.BAD_REQUEST ||
            result.getB() == HttpStatus.NOT_FOUND ||
            result.getB() == HttpStatus.INTERNAL_SERVER_ERROR) {
            return new ResponseEntity<>(result.getA(), result.getB());
        } else {
            return new ResponseEntity<>(result.getB());
        }
    }
    
    /**
     * delete sequence
     *
     * @param sequence delete request
     * @return execute result
     */
    @PostMapping(path = "/delseq/{credentialType}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> delSeq(@PathVariable String credentialType, @RequestBody SequenceDelRequestDTO sequenceDelRequest) {
    	Tuple.Pair<String, HttpStatus> result = null;
    	Map<String, Object> metadata = null;
    	
    	if(sequenceDelRequest != null && sequenceDelRequest.validate()) {
    		metadata = sequenceDelRequest.getMetadata();
    	}
    	
    	LOGGER.info("[delSeq]::credentialType = {}, metadata = {}", credentialType,	metadata);
    	
    	try {
    		result = sequenceService.deleteSeqAndPolicyAndUpdateCredentialIssuerConfig(credentialType, sequenceDelRequest);
    	} catch (Exception e) {			// 刪除序列失敗時，會產生rollback exception，在這邊才能夠處理
    		LOGGER.error(e.getMessage(), e);
    		VcException vcException = new VcException(VcException.ERR_DB_DELETE_ERROR, "fail to delete sequence or policy");
            String msg = new ErrorResponseDTO(vcException).toString();
            HttpStatus httpStatus = vcException.toHttpStatus();
    		result = Tuple.collect(msg, httpStatus);
    	}
    	LOGGER.info("[HTTP status]:: {}", result.getB().toString());
        LOGGER.info("[delete result]:: {}\n", result.getA());
        Console.showJson(result.getA(), "delete sequence response");

        // with response content when http status = 200/400/404/500
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
     * set function switch
     *
     * @param func_switch setting request
     * @return execute result
     */
    @PostMapping(path = "/funcswitch/{credentialType}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> setFuncSwitch(@PathVariable String credentialType, @RequestBody FunctionSwitchRequestDTO functionSwitchRequest) {
    	Tuple.Pair<String, HttpStatus> result = null;
    	LinkedHashMap<String, Object> switches = null;
    	
    	if(functionSwitchRequest != null && functionSwitchRequest.validate()) {
    		switches = functionSwitchRequest.getSwitches();
    	}
    	
    	LOGGER.info("""
                [setFuncSwitch]::
                credentialType = {}
                switches = {}
                """, credentialType, switches);
    	
    	result = sequenceService.setFunctionSwitch(credentialType, functionSwitchRequest);
    	
    	LOGGER.info("[HTTP status]:: {}", result.getB().toString());
        LOGGER.info("[set function switch result]:: {}\n", result.getA());
        Console.showJson(result.getA(), "set function switch response");
        
        // with response content when http status = 200/400/404/500
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
