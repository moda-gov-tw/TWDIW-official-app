package gov.moda.dw.issuer.vc.service.dto;

import java.io.Serial;
import java.io.Serializable;

import gov.moda.dw.issuer.vc.util.JsonUtils;
import jakarta.validation.constraints.NotBlank;

/**
 * set credential data response
 *
 * @version 20241017
 */
public class CredentialDataResponseDTO implements Serializable {

	@Serial
    private static final long serialVersionUID = 1L;

    @NotBlank
    private String transactionId;
    
    public CredentialDataResponseDTO() {
    	
    }
    
    public CredentialDataResponseDTO(String transactionId) {
    	this.transactionId = transactionId;
    }

	public String getTransactionId() {
		return transactionId;
	}

	public CredentialDataResponseDTO setTransactionId(String transactionId) {
		this.transactionId = transactionId;
		return this;
	}
	
	public boolean validate() {
		return transactionId != null && !transactionId.isBlank();
	}
	
	@Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }
}
