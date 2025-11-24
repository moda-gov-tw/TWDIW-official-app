package gov.moda.dw.issuer.vc.service.dto.frontend;

import gov.moda.dw.issuer.vc.util.JsonUtils;
import jakarta.validation.constraints.NotBlank;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

/**
 * DID registration request (to frontend service)
 *
 * @version 20250428
 */
public class DidRegisterRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String did;
    private Map<String, Object> org;
    private int orgType;

    public DidRegisterRequestDTO() {
    }

    public DidRegisterRequestDTO(String did, Map<String, Object> org, int orgType) {
        this.did = did;
        this.org = org;
        this.orgType = orgType;
    }

    public String getDid() {
        return did;
    }

    public DidRegisterRequestDTO setDid(String did) {
        this.did = did;
        return this;
    }

    public Map<String, Object> getOrg() {
        return org;
    }

    public DidRegisterRequestDTO setOrg(Map<String, Object> org) {
        this.org = org;
        return this;
    }

    public int getOrgType() {
		return orgType;
	}

	public DidRegisterRequestDTO setOrgType(int orgType) {
		this.orgType = orgType;
		return this;
	}

	@Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }
}
