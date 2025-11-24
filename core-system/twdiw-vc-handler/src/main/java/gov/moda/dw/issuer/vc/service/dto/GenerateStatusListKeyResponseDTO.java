package gov.moda.dw.issuer.vc.service.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

import gov.moda.dw.issuer.vc.util.JsonUtils;

/**
 * generate status list signing key response
 *
 * @version 20241024
 */
public class GenerateStatusListKeyResponseDTO implements Serializable {

	@Serial
    private static final long serialVersionUID = 1L;
	
	private Map<String, Object> key;
	
	public GenerateStatusListKeyResponseDTO() {
		
	}
	
	public GenerateStatusListKeyResponseDTO(Map<String, Object> key) {
		this.key = key;
	}
	
	public Map<String, Object> getKey() {
		return key;
	}

	public GenerateStatusListKeyResponseDTO setKey(Map<String, Object> key) {
		this.key = key;
		return this;
	}

	@Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }
}
