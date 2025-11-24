package gov.moda.dw.issuer.vc.service.dto;

import java.io.Serial;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import gov.moda.dw.issuer.vc.util.JsonUtils;

/**
 * Credential transfer response
 *
 * @version 20240428
 */
public class CredentialTransferResponseDTO implements Serializable {

	@Serial
    private static final long serialVersionUID = 1L;
	
	@JsonProperty("qr_code")
	private String qrCode;
	
	private String link;
	
	public CredentialTransferResponseDTO() {
		
	}
	
	public CredentialTransferResponseDTO(String qrCode, String link) {
		this.qrCode = qrCode;
		this.link = link;
	}
	
	public CredentialTransferResponseDTO(String respJson) {
		if(respJson != null && !respJson.isBlank()) {
			CredentialTransferResponseDTO credentialTransferResponse = JsonUtils.jsToVo(respJson, this.getClass());
			if(credentialTransferResponse != null) {
				this.qrCode = credentialTransferResponse.getQrCode();
				this.link = credentialTransferResponse.getLink();
			}
		}
	}

	public String getQrCode() {
		return qrCode;
	}

	public String getLink() {
		return link;
	}

	public CredentialTransferResponseDTO setQrCode(String qrCode) {
		this.qrCode = qrCode;
		return this;
	}

	public CredentialTransferResponseDTO setLink(String link) {
		this.link = link;
		return this;
	}
	
	@Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }
}
