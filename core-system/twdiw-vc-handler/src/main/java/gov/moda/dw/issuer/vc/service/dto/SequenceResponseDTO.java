package gov.moda.dw.issuer.vc.service.dto;

import java.io.Serial;
import java.io.Serializable;

import gov.moda.dw.issuer.vc.util.JsonUtils;
import jakarta.validation.constraints.NotBlank;

/**
 * sequence generation response
 *
 * @version 20241017
 */
public class SequenceResponseDTO implements Serializable {

	@Serial
    private static final long serialVersionUID = 1L;

    @NotBlank
    private String credentialType;
    
    public SequenceResponseDTO() {
    	
    }
    
    public SequenceResponseDTO(String credentialType) {
    	this.credentialType = credentialType;
    }

	public String getCredentialType() {
		return credentialType;
	}

	public SequenceResponseDTO setCredentialType(String credentialType) {
		this.credentialType = credentialType;
		return this;
	}
	
	@Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }
}
