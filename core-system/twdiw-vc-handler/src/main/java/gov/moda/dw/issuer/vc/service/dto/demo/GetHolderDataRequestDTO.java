package gov.moda.dw.issuer.vc.service.dto.demo;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.moda.dw.issuer.vc.util.JsonUtils;

import java.io.Serial;
import java.io.Serializable;

/**
 * request of get holder's data (to demo service)
 *
 * @version 20240902
 */
public class GetHolderDataRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("credential_type")
    private String credentialType;
    private String nonce;
    private String sub;

    public GetHolderDataRequestDTO() {
    }

    public GetHolderDataRequestDTO(String credentialType, String nonce, String sub) {
        this.credentialType = credentialType;
        this.nonce = nonce;
        this.sub = sub;
    }

    public String getCredentialType() {
        return credentialType;
    }

    public GetHolderDataRequestDTO setCredentialType(String credentialType) {
        this.credentialType = credentialType;
        return this;
    }

    public String getNonce() {
        return nonce;
    }

    public GetHolderDataRequestDTO setNonce(String nonce) {
        this.nonce = nonce;
        return this;
    }

    public String getSub() {
        return sub;
    }

    public GetHolderDataRequestDTO setSub(String sub) {
        this.sub = sub;
        return this;
    }

    @Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }
}
