package gov.moda.dw.verifier.oidvp.model.did;

import gov.moda.dw.verifier.oidvp.util.JsonUtils;
import java.util.Collections;
import java.util.Map;

public class DIDCreateRequest {

    private final String did;

    private final Integer orgType;

    private final Map<String, Object> org;

    private final String p7data;

    public DIDCreateRequest(String did, Integer orgType, Map<String, Object> org, String p7data) {
        this.did = did;
        this.orgType = orgType;
        this.org = org == null ? Collections.emptyMap() : org;
        this.p7data = p7data == null ? "" : p7data;
    }

    public String getDid() {
        return did;
    }

    public Map<String, Object> getOrg() {
        return org;
    }

    public Integer getOrgType() {
        return orgType;
    }

    public String getP7data() {
        return p7data;
    }

    public String toJson() {
        return "{" +
            "\"did\":\"" + did + '\"' +
            ",\"orgType\":" + orgType +
            ",\"org\":" + JsonUtils.toJsonString(this.org) +
            ",\"p7data\":\"" + p7data + '\"' +
            "}";
    }

    @Override
    public String toString() {
        return toJson();
    }
}
