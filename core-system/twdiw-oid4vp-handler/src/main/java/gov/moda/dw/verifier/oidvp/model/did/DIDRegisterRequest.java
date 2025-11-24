package gov.moda.dw.verifier.oidvp.model.did;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.Map;

@JsonInclude(Include.NON_NULL)
public class DIDRegisterRequest {

    private String did;

    private Integer orgType;

    private Map<String, Object> org;

    public DIDRegisterRequest() {
    }

    public DIDRegisterRequest(String did, Integer orgType, Map<String, Object> org) {
        this.did = did;
        this.orgType = orgType;
        this.org = org;
    }

    public String getDid() {
        return did;
    }

    public DIDRegisterRequest setDid(String did) {
        this.did = did;
        return this;
    }

    public Integer getOrgType() {
        return orgType;
    }

    public DIDRegisterRequest setOrgType(Integer orgType) {
        this.orgType = orgType;
        return this;
    }

    public Map<String, Object> getOrg() {
        return org;
    }

    public DIDRegisterRequest setOrg(Map<String, Object> org) {
        this.org = org;
        return this;
    }
}
