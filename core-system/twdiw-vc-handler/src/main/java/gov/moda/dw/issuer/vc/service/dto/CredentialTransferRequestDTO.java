package gov.moda.dw.issuer.vc.service.dto;

import java.io.Serial;
import java.io.Serializable;

import gov.moda.dw.issuer.vc.util.JsonUtils;

/**
 * Credential transfer request
 *
 * @version 20240428
 */
public class CredentialTransferRequestDTO  implements Serializable{

	@Serial
    private static final long serialVersionUID = 1L;
	
	private String vp;
	
	public CredentialTransferRequestDTO() {
		
	}
	
	public CredentialTransferRequestDTO(String vp) {
		this.vp = vp;
	}

	public String getVp() {
		return vp;
	}

	public CredentialTransferRequestDTO setVp(String vp) {
		this.vp = vp;
		return this;
	}
	
	public boolean validate() {
		return vp != null && !vp.isBlank();
	}
	
	@Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }
}
