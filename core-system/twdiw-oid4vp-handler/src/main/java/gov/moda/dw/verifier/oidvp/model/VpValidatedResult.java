package gov.moda.dw.verifier.oidvp.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * Object of vp token validate result from {@code verifier-vp-service}
 */
@JsonInclude(Include.NON_NULL)
public class VpValidatedResult {

    @NotNull private String clientId;

    @NotNull private String nonce;

    @NotNull private String holderDid;

    @NotEmpty
    @JsonInclude(Include.NON_EMPTY)
    private List<VcData> vcs;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public List<VcData> getVcs() {
        return vcs;
    }

    public void setVcs(List<VcData> vcs) {
        this.vcs = vcs;
    }

    public String getHolderDid() {
        return holderDid;
    }

    public void setHolderDid(String holderDid) {
        this.holderDid = holderDid;
    }

    @JsonInclude(Include.NON_NULL)
    public static class VcData {

        private String credentialType;
        private String vpPath;
        private String vcPath;
        private JsonNode credential;
        @Deprecated
        private JsonNode claims;
        private Boolean limitDisclosureSupported;

        public JsonNode getClaims() {
            return claims;
        }

        public void setClaims(JsonNode claims) {
            this.claims = claims;
        }

        public JsonNode getCredential() {
            return credential;
        }

        public void setCredential(JsonNode credential) {
            this.credential = credential;
        }

        public String getCredentialType() {
            return credentialType;
        }

        public void setCredentialType(String credentialType) {
            this.credentialType = credentialType;
        }

        public String getVcPath() {
            return vcPath;
        }

        public void setVcPath(String vcPath) {
            this.vcPath = vcPath;
        }

        public String getVpPath() {
            return vpPath;
        }

        public void setVpPath(String vpPath) {
            this.vpPath = vpPath;
        }

        public void setLimitDisclosureSupported(Boolean limitDisclosureSupported) {
            this.limitDisclosureSupported = limitDisclosureSupported;
        }

        public Boolean getLimitDisclosureSupported() {
            return limitDisclosureSupported;
        }
    }
}
