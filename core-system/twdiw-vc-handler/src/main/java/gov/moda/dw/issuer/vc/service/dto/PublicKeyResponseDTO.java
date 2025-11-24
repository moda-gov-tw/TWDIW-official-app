package gov.moda.dw.issuer.vc.service.dto;

import gov.moda.dw.issuer.vc.util.JsonUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * public key response
 *
 * @version 20240902
 */
public class PublicKeyResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<Map<String, Object>> keys;

    public PublicKeyResponseDTO() {
    }

    public PublicKeyResponseDTO(Map<String, Object> key) {
        this.keys = Collections.singletonList(key);
    }

    public PublicKeyResponseDTO(List<Map<String, Object>> keys) {
        this.keys = keys;
    }

    public List<Map<String, Object>> getKeys() {
        return keys;
    }

    public PublicKeyResponseDTO setKeys(List<Map<String, Object>> keys) {
        this.keys = keys;
        return this;
    }

    @Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }
}
