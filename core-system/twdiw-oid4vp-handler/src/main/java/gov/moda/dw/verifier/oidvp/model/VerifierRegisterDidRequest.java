package gov.moda.dw.verifier.oidvp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public class VerifierRegisterDidRequest {

    @JsonProperty("org")
    private Map<String, Object> org;

    @JsonProperty("p7data")
    private String p7data;

    @JsonIgnore
    private String tbd;

    public Map<String, Object> getOrg() {
        return org;
    }

    public void setOrg(Map<String, Object> org) {
        this.org = org;
    }

    public String getP7data() {
        return p7data;
    }

    public void setP7data(String p7data) {
        this.p7data = p7data;
    }

    @Override
    public String toString() {
        return "{" +
            "\"org\":" + org + "," +
            "\"p7data\":" + "\"" + p7data + "\"" +
            "}";
    }
}
