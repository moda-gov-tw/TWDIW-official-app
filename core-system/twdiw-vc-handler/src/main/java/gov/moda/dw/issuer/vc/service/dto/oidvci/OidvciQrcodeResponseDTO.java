package gov.moda.dw.issuer.vc.service.dto.oidvci;

import java.io.Serial;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import gov.moda.dw.issuer.vc.util.JsonUtils;
import jakarta.validation.constraints.NotBlank;

/**
 * Oidvci qrcode response (from oidvci service)
 *
 * @version 20240428
 */
public class OidvciQrcodeResponseDTO implements Serializable {

	@Serial
    private static final long serialVersionUID = 1L;
	
	@NotBlank
	@JsonProperty("resp_code")
	private int respCode;
	
	@NotBlank
	@JsonProperty("resp_message")
	private String respMessage;
	
	@JsonProperty("qr_code")
	private String qrCode;
	
	private String link;
	
	public OidvciQrcodeResponseDTO() {
		
	}
	
	public OidvciQrcodeResponseDTO(int respCode, String respMessage, String qrCode, String link) {
		this.respCode = respCode;
		this.respMessage = respMessage;
		this.qrCode = qrCode;
		this.link = link;
	}
	
	public OidvciQrcodeResponseDTO(String respJson) {
		if(respJson != null && !respJson.isBlank()) {
			OidvciQrcodeResponseDTO oidvciQrcodeResponse = JsonUtils.jsToVo(respJson, this.getClass());
			if(oidvciQrcodeResponse != null) {
				this.respCode = oidvciQrcodeResponse.getRespCode();
				this.respMessage = oidvciQrcodeResponse.getRespMessage();
				this.qrCode = oidvciQrcodeResponse.getQrCode();
				this.link = oidvciQrcodeResponse.getLink();
			}
		}
	}

	public int getRespCode() {
		return respCode;
	}

	public String getRespMessage() {
		return respMessage;
	}

	public String getQrCode() {
		return qrCode;
	}

	public String getLink() {
		return link;
	}

	public void setRespCode(int respCode) {
		this.respCode = respCode;
	}

	public void setRespMessage(String respMessage) {
		this.respMessage = respMessage;
	}

	public void setQrCode(String qrCode) {
		this.qrCode = qrCode;
	}

	public void setLink(String link) {
		this.link = link;
	}
	
	public boolean validate() {
		return respMessage != null && !respMessage.isBlank() &&
				qrCode != null && !qrCode.isBlank() &&
				link != null && !link.isBlank();
	}
	
	@Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }
}
