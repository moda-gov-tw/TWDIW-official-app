package gov.moda.dw.issuer.vc.service.dto.frontend;

import gov.moda.dw.issuer.vc.util.JsonUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

/**
 * DID generation request (to frontend service)
 *
 * @version 20240902
 */
public class DidGenerateRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Map<String,Object> publicKeyJwk;

    public DidGenerateRequestDTO() {
    }

    public DidGenerateRequestDTO(Map<String, Object> publicKeyJwk) {
        this.publicKeyJwk = publicKeyJwk;
    }

    public DidGenerateRequestDTO(String reqJson) {

        if (reqJson != null && !reqJson.isBlank()) {
            DidGenerateRequestDTO didGenerateRequestDTO = JsonUtils.jsToVo(reqJson, this.getClass());
            if (didGenerateRequestDTO != null) {
                this.publicKeyJwk = didGenerateRequestDTO.getPublicKeyJwk();
            }
        }
    }

    public Map<String, Object> getPublicKeyJwk() {
        return publicKeyJwk;
    }

    public DidGenerateRequestDTO setPublicKeyJwk(Map<String, Object> publicKeyJwk) {
        this.publicKeyJwk = publicKeyJwk;
        return this;
    }

    @Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }
}
