package gov.moda.dw.issuer.vc.service.dto;

import gov.moda.dw.issuer.vc.util.JsonUtils;
import jakarta.validation.constraints.NotBlank;

import java.io.Serial;
import java.io.Serializable;

/**
 * credential status update response
 *
 * @version 20240902
 */
public class CredentialStatusResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank
    private String credentialStatus;

    public CredentialStatusResponseDTO() {
    }

    public CredentialStatusResponseDTO(String credentialStatus) {
        this.credentialStatus = credentialStatus;
    }

    public String getCredentialStatus() {
        return credentialStatus;
    }

    public CredentialStatusResponseDTO setCredentialStatus(String credentialStatus) {
        this.credentialStatus = credentialStatus;
        return this;
    }
    
    public boolean validate() {
    	return credentialStatus != null && !credentialStatus.isBlank();
    }

    @Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }
}
