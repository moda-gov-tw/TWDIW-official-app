package gov.moda.dw.issuer.vc.service.dto.frontend;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

import gov.moda.dw.issuer.vc.util.JsonUtils;

/**
 * DID create request (to frontend service)
 *
 * @version 20250603
 */
public class DidCreateRequestDTO implements Serializable {

	@Serial
    private static final long serialVersionUID = 1L;
	
	private String did;
	private Map<String, Object> org;
	private int orgType;
	private String p7data;
	
	public DidCreateRequestDTO() {
		
	}
	
	public DidCreateRequestDTO(String did, Map<String, Object> org, int orgType, String p7data) {
		this.did = did;
		this.org = org;
		this.orgType = orgType;
		this.p7data = p7data;
	}

	public String getDid() {
		return did;
	}

	public Map<String, Object> getOrg() {
		return org;
	}

	public int getOrgType() {
		return orgType;
	}

	public String getP7data() {
		return p7data;
	}

	public DidCreateRequestDTO setDid(String did) {
		this.did = did;
		return this;
	}

	public DidCreateRequestDTO setOrg(Map<String, Object> org) {
		this.org = org;
		return this;
	}

	public DidCreateRequestDTO setOrgType(int orgType) {
		this.orgType = orgType;
		return this;
	}

	public DidCreateRequestDTO setP7data(String p7data) {
		this.p7data = p7data;
		return this;
	}
	
	@Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }
}
