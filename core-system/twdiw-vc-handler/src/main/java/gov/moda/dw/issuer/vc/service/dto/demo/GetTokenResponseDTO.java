package gov.moda.dw.issuer.vc.service.dto.demo;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import gov.moda.dw.issuer.vc.util.JsonUtils;

/**
 * response of get authorization token api
 *
 * @version 20250210
 */
public class GetTokenResponseDTO implements Serializable {

	@Serial
    private static final long serialVersionUID = 1L;
	
	@JsonProperty("id_token")
	private String idToken;
	
	public GetTokenResponseDTO() {
		
	}
	
	public GetTokenResponseDTO(String respJson) {
		if(respJson != null & !respJson.isBlank()) {
			GetTokenResponseDTO getTokenResponseDTO = JsonUtils.jsToVo(respJson, this.getClass());
			if(getTokenResponseDTO != null) {
				this.idToken = getTokenResponseDTO.getIdToken();
			}
		}
	}

	public String getIdToken() {
		return idToken;
	}

	public GetTokenResponseDTO setIdToken(String idToken) {
		this.idToken = idToken;
		return this;
	}
	
	@Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }
}
