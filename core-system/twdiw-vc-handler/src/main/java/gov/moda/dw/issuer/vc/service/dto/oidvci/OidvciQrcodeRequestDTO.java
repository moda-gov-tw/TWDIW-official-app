package gov.moda.dw.issuer.vc.service.dto.oidvci;

import java.io.Serial;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import gov.moda.dw.issuer.vc.util.JsonUtils;
import jakarta.validation.constraints.NotBlank;

/**
 * Oidvci qrcode request
 *
 * @version 20240428
 */
public class OidvciQrcodeRequestDTO implements Serializable {

	@Serial
    private static final long serialVersionUID = 1L;
	
	@NotBlank
    private Boolean authenticated;
	
	@JsonProperty("id_token")
	private String idToken;
	
	public OidvciQrcodeRequestDTO() {
		
	}
	
	public OidvciQrcodeRequestDTO(Boolean authenticated, String idToken) {
		this.authenticated = authenticated;
		this.idToken = idToken;
	}
	
	public OidvciQrcodeRequestDTO(String reqJson) {
		if(reqJson != null && !reqJson.isBlank()) {
			OidvciQrcodeRequestDTO oidvciQrcodeRequest = JsonUtils.jsToVo(reqJson, this.getClass());
			if(oidvciQrcodeRequest != null) {
				this.authenticated = oidvciQrcodeRequest.getAuthenticated();
				this.idToken = oidvciQrcodeRequest.getIdToken();
			}
		}
	}

	public Boolean getAuthenticated() {
		return authenticated;
	}

	public String getIdToken() {
		return idToken;
	}

	public OidvciQrcodeRequestDTO setAuthenticated(Boolean authenticated) {
		this.authenticated = authenticated;
		return this;
	}

	public OidvciQrcodeRequestDTO setIdToken(String idToken) {
		this.idToken = idToken;
		return this;
	}
	
	public boolean validate() {
		return authenticated != null && idToken != null && !idToken.isBlank();
	}
	
	@Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }
}
