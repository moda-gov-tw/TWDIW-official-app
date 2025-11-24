package gov.moda.dw.verifier.vc.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.moda.dw.verifier.vc.util.JsonUtils;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class PresentationValidationResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String clientId;
    private String nonce;
    private List<VcData> vcs;
    private String holderDid;


    public PresentationValidationResponseDTO() {
    }

    public PresentationValidationResponseDTO(String clientId, String nonce, List<VcData> vcs, String holderDid) {
        this.clientId = clientId;
        this.nonce = nonce;
        this.vcs = vcs;
        this.holderDid = holderDid;
    }

    public PresentationValidationResponseDTO(String respJson) {

        if (respJson != null && !respJson.isBlank()) {
            PresentationValidationResponseDTO presentationValidationResponseDTO = JsonUtils.jsToVo(respJson, getClass());
            if (presentationValidationResponseDTO != null) {
                clientId = presentationValidationResponseDTO.getClientId();
                nonce = presentationValidationResponseDTO.getNonce();
                vcs = presentationValidationResponseDTO.getVcs();
                holderDid = presentationValidationResponseDTO.getHolderDid();
            }
        }
    }

    public String getClientId() {
        return clientId;
    }

    public PresentationValidationResponseDTO setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public String getNonce() {
        return nonce;
    }

    public PresentationValidationResponseDTO setNonce(String nonce) {
        this.nonce = nonce;
        return this;
    }

    public List<VcData> getVcs() {
        return vcs;
    }

    public PresentationValidationResponseDTO setVcs(List<VcData> vcs) {
        this.vcs = vcs;
        return this;
    }

    public String getHolderDid() {
        return holderDid;
    }

    public PresentationValidationResponseDTO setHolderDid(String holderDid) {
        this.holderDid = holderDid;
        return this;
    }

    @Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }

    public static class VcData {

        private String vpPath;
        private String vcPath;
        private Boolean limitDisclosureSupported;
        private String credentialType;
        private Map<String, Object> credential;
        @Deprecated
        private Map<String, Object> claims;
        @JsonIgnore
        private Map<String, Object> holderPublicKey;
        @JsonIgnore
        private String sub;

        public VcData() {
        }

        public VcData(String vpPath, String vcPath, String credentialType, Map<String, Object> credential,
            Map<String, Object> claims, Map<String, Object> holderPublicKey)
        {
            this.vpPath = vpPath;
            this.vcPath = vcPath;
            this.credentialType = credentialType;
            this.credential = credential;
            this.claims = claims;
            this.holderPublicKey = holderPublicKey;
        }

        public String getVcPath() {
            return vcPath;
        }

        public VcData setVcPath(String vcPath) {
            this.vcPath = vcPath;
            return this;
        }

        public String getVpPath() {
            return vpPath;
        }

        public VcData setVpPath(String vpPath) {
            this.vpPath = vpPath;
            return this;
        }

        public String getCredentialType() {
            return credentialType;
        }

        public VcData setCredentialType(String credentialType) {
            this.credentialType = credentialType;
            return this;
        }

        public Map<String, Object> getClaims() {
            return claims;
        }

        public VcData setClaims(Map<String, Object> claims) {
            this.claims = claims;
            return this;
        }

        public Map<String, Object> getCredential() {
            return credential;
        }

        public VcData setCredential(Map<String, Object> credential) {
            this.credential = credential;
            return this;
        }

        public Map<String, Object> getHolderPublicKey() {
            return holderPublicKey;
        }

        public VcData setHolderPublicKey(Map<String, Object> holderPublicKey) {
            this.holderPublicKey = holderPublicKey;
            return this;
        }

        public Boolean getLimitDisclosureSupported() {
            return limitDisclosureSupported;
        }

        public VcData setLimitDisclosureSupported(Boolean limitDisclosureSupported) {
            this.limitDisclosureSupported = limitDisclosureSupported;
            return this;
        }

        public String getSub() {
            return sub;
        }

        public VcData setSub(String sub) {
            this.sub = sub;
            return this;
        }

        @Override
        public String toString() {
            return JsonUtils.voToJs(this);
        }
    }
}
