package gov.moda.dw.issuer.vc.service.dto;

import java.io.Serial;
import java.io.Serializable;

import gov.moda.dw.issuer.vc.util.JsonUtils;
import jakarta.validation.constraints.NotBlank;

/**
 * sequence delete response
 *
 * @version 20250206
 */
public class SequenceDelResponseDTO implements Serializable {

	@Serial
    private static final long serialVersionUID = 1L;
	
	@NotBlank
    private String credentialType;
	
	public SequenceDelResponseDTO() {
		
	}
	
	public SequenceDelResponseDTO(String credentialType) {
		this.credentialType = credentialType;
	}

	public String getCredentialType() {
		return credentialType;
	}

	public SequenceDelResponseDTO setCredentialType(String credentialType) {
		this.credentialType = credentialType;
		return this;
	}
	
	@Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }
}
