package gov.moda.dw.issuer.vc.service.dto.demo;

import java.io.Serial;
import java.io.Serializable;

import gov.moda.dw.issuer.vc.util.JsonUtils;

/**
 * request of get authorization token api
 *
 * @version 20250210
 */
public class GetTokenRequestDTO implements Serializable {

	@Serial
    private static final long serialVersionUID = 1L;
	
	private String username;
    
	private String password;
    
	public GetTokenRequestDTO() {
		
	}
	
	public GetTokenRequestDTO(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public GetTokenRequestDTO setUsername(String username) {
		this.username = username;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public GetTokenRequestDTO setPassword(String password) {
		this.password = password;
		return this;
	}
	
	@Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }
}
