package gov.moda.dw.issuer.vc.service.dto;

import gov.moda.dw.issuer.vc.util.JsonUtils;
import jakarta.validation.constraints.NotBlank;

import java.io.Serial;
import java.io.Serializable;

/**
 * Issuer DID registration response
 *
 * @version 20240902
 */
public class IssuerDidResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank
    private String did;

    public IssuerDidResponseDTO() {
    }

    public IssuerDidResponseDTO(String did) {
        this.did = did;
    }

    public String getDid() {
        return did;
    }

    public IssuerDidResponseDTO setDid(String did) {
        this.did = did;
        return this;
    }

    @Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }
}
