package gov.moda.dw.verifier.vc.service.dto.vc;

import gov.moda.dw.verifier.vc.util.JsonUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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

    public PublicKeyResponseDTO(String respJson) {

        if (respJson != null && !respJson.isBlank()) {
            PublicKeyResponseDTO publicKeyResponseDTO = JsonUtils.jsToVo(respJson, this.getClass());
            if (publicKeyResponseDTO != null) {
                this.keys = publicKeyResponseDTO.getKeys();
            }
        }
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
