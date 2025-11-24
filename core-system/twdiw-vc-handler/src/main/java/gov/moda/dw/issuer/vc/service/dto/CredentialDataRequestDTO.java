package gov.moda.dw.issuer.vc.service.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import gov.moda.dw.issuer.vc.util.JsonUtils;
import jakarta.validation.constraints.NotBlank;

/**
 * set credential data request
 *
 * @version 20241017
 */
public class CredentialDataRequestDTO implements Serializable {

	@Serial
    private static final long serialVersionUID = 1L;
	
	@NotBlank
    private String transactionId;
	
	@NotBlank
    private String credentialType;
	
    private LinkedHashMap<String, Object> data;
    
    private LinkedHashMap<String, Object> options;
    
    private String reqJson;
    
    public CredentialDataRequestDTO() {
    	
    }
    
    public CredentialDataRequestDTO(String transactionId, String credentialType, LinkedHashMap<String, Object> data, LinkedHashMap<String, Object> options) {
    	this.transactionId = transactionId;
    	this.credentialType = credentialType;
    	this.data = data;
    	this.options = options;
    }
    
    public CredentialDataRequestDTO(String reqJson) {
    	
    	if (reqJson != null && !reqJson.isBlank()) {
    		CredentialDataRequestDTO credentialDataRequest = JsonUtils.jsToVo(reqJson, this.getClass());
    		if(credentialDataRequest != null) {
    			this.transactionId = credentialDataRequest.getTransactionId();
    			this.credentialType = credentialDataRequest.getCredentialType();
    			this.data = credentialDataRequest.getData();
    			this.options = credentialDataRequest.getOptions();
    		}
    	}
    }

	public String getTransactionId() {
		return transactionId;
	}

	public CredentialDataRequestDTO setTransactionId(String transactionId) {
		this.transactionId = transactionId;
		return this;
	}

	public String getCredentialType() {
		return credentialType;
	}

	public CredentialDataRequestDTO setCredentialType(String credentialType) {
		this.credentialType = credentialType;
		return this;
	}

	public LinkedHashMap<String, Object> getData() {
		return data;
	}

	public CredentialDataRequestDTO setData(LinkedHashMap<String, Object> data) {
		this.data = data;
		return this;
	}
	
	public LinkedHashMap<String, Object> getOptions() {
		return options;
	}

	public CredentialDataRequestDTO setOptions(LinkedHashMap<String, Object> options) {
		this.options = options;
		return this;
	}

	public boolean validate() {
		return transactionId != null && !transactionId.isBlank() &&
				credentialType != null && !credentialType.isBlank() &&
				data != null && !data.isEmpty();
	}
	
	@Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }
}
