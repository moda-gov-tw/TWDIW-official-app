package gov.moda.dw.issuer.vc.service.dto;

import java.io.Serial;
import java.io.Serializable;

import gov.moda.dw.issuer.vc.util.JsonUtils;
import jakarta.validation.constraints.NotBlank;

/**
 * set function switch response
 *
 * @version 20250421
 */

public class FunctionSwitchResponseDTO implements Serializable {

	@Serial
    private static final long serialVersionUID = 1L;
	
	@NotBlank
    private String credentialType;
	
	public FunctionSwitchResponseDTO() {
		
	}
	
	public FunctionSwitchResponseDTO(String credentialType) {
		this.credentialType = credentialType;
	}

	public String getCredentialType() {
		return credentialType;
	}

	public FunctionSwitchResponseDTO setCredentialType(String credentialType) {
		this.credentialType = credentialType;
		return this;
	}
	
	@Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }
}
