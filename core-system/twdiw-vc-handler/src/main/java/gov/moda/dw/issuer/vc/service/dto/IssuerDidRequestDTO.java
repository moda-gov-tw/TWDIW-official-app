package gov.moda.dw.issuer.vc.service.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

import gov.moda.dw.issuer.vc.util.JsonUtils;

/**
 * Issuer DID registration request
 *
 * @version 20241024
 */
public class IssuerDidRequestDTO implements Serializable {

	@Serial
    private static final long serialVersionUID = 1L;
	
	private Map<String, Object> org;
	
	private String p7data;
	
	private String reqJson;
	
	public IssuerDidRequestDTO() {
		
	}
	
	public IssuerDidRequestDTO(Map<String, Object> org, String p7data) {
		this.org = org;
		this.p7data = p7data;
	}
	
	public IssuerDidRequestDTO(String reqJson) {
		
		if (reqJson != null && !reqJson.isBlank()) {
			IssuerDidRequestDTO issuerDidRequest = JsonUtils.jsToVo(reqJson, this.getClass());
			if(issuerDidRequest != null) {
				this.org = issuerDidRequest.getOrg();
				this.p7data = issuerDidRequest.getP7data();
			}
		}
	}
	
	public Map<String, Object> getOrg() {
		return org;
	}

	public IssuerDidRequestDTO setOrg(Map<String, Object> org) {
		this.org = org;
		return this;
	}
	
	public String getP7data() {
		return p7data;
	}

	public IssuerDidRequestDTO setP7data(String p7data) {
		this.p7data = p7data;
		return this;
	}

	public boolean validate() {
		return org != null && !org.isEmpty();
	}

	@Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }
}
