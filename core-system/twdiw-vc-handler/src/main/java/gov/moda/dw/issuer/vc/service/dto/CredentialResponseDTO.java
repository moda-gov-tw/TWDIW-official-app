package gov.moda.dw.issuer.vc.service.dto;

import gov.moda.dw.issuer.vc.util.JsonUtils;
import jakarta.validation.constraints.NotBlank;

import java.io.Serial;
import java.io.Serializable;

/**
 * credential generation / query response
 *
 * @version 20240902
 */
public class CredentialResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank
    private String credential;

    public CredentialResponseDTO() {
    }

    public CredentialResponseDTO(String credential) {
        this.credential = credential;
    }

    public String getCredential() {
        return credential;
    }

    public CredentialResponseDTO setCredential(String credential) {
        this.credential = credential;
        return this;
    }

    @Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }
}
