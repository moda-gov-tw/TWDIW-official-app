package gov.moda.dw.issuer.vc.service.dto;

import gov.moda.dw.issuer.vc.util.JsonUtils;
import jakarta.validation.constraints.NotBlank;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

/**
 * credential generation request
 *
 * @version 20240902
 */
public class CredentialRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank
    private String credentialType;

    @NotBlank
    private String holderUid;

    @NotBlank
    private String holderDid;

    private Map<String, Object> holderPublicKey;

    @NotBlank
    private String nonce;

    private String reqJson;

    public CredentialRequestDTO() {
    }

    public CredentialRequestDTO(
        String credentialType,
        String holderUid,
        String holderDid,
        Map<String, Object> holderPublicKey) {

        this.credentialType = credentialType;
        this.holderUid = holderUid;
        this.holderDid = holderDid;
        this.holderPublicKey = holderPublicKey;
    }

    public CredentialRequestDTO(String reqJson) {

        if (reqJson != null && !reqJson.isBlank()) {
            CredentialRequestDTO credentialRequest = JsonUtils.jsToVo(reqJson, this.getClass());
            if (credentialRequest != null) {
                this.credentialType = credentialRequest.getCredentialType();
                this.holderUid = credentialRequest.getHolderUid();
                this.holderDid = credentialRequest.getHolderDid();
                this.holderPublicKey = credentialRequest.getHolderPublicKey();
                this.nonce = credentialRequest.getNonce();
            }
        }
    }

    public boolean validate() {
        // TODO: add check rule for all fields
        return credentialType != null && !credentialType.isBlank() &&
            holderUid != null && !holderUid.isBlank() &&
            holderDid != null && !holderDid.isBlank() &&
            holderPublicKey != null && !holderPublicKey.isEmpty() &&
            nonce != null && !nonce.isBlank();
    }

    public String getCredentialType() {
        return credentialType;
    }

    public CredentialRequestDTO setCredentialType(String credentialType) {
        this.credentialType = credentialType;
        return this;
    }

    public String getHolderUid() {
        return holderUid;
    }

    public CredentialRequestDTO setHolderUid(String holderUid) {
        this.holderUid = holderUid;
        return this;
    }

    public String getHolderDid() {
        return holderDid;
    }

    public CredentialRequestDTO setHolderDid(String holderDid) {
        this.holderDid = holderDid;
        return this;
    }

    public Map<String, Object> getHolderPublicKey() {
        return holderPublicKey;
    }

    public CredentialRequestDTO setHolderPublicKey(Map<String, Object> holderPublicKey) {
        this.holderPublicKey = holderPublicKey;
        return this;
    }

    public String getNonce() {
        return nonce;
    }

    public CredentialRequestDTO setNonce(String nonce) {
        this.nonce = nonce;
        return this;
    }

    @Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }
}
