package gov.moda.dw.issuer.vc.service.dto.demo;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.moda.dw.issuer.vc.util.JsonUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * response of get holder's data (from demo service)
 *
 * @version 20250526
 */
public class GetHolderDataResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("credential_type")
    private String credentialType;
    private String nonce;
    private Map<String, Object> data;
    private LinkedHashMap<String, Object> options;

    public GetHolderDataResponseDTO() {
    }

    public GetHolderDataResponseDTO(String credentialType, String nonce, Map<String, Object> data, LinkedHashMap<String, Object> options) {
        this.credentialType = credentialType;
        this.nonce = nonce;
        this.data = data;
        this.options = options;
    }

    public GetHolderDataResponseDTO(String respJson) {

        if (respJson != null && !respJson.isBlank()) {
            GetHolderDataResponseDTO getHolderDataResponseDTO = JsonUtils.jsToVo(respJson, this.getClass());
            if (getHolderDataResponseDTO != null) {
                this.credentialType = getHolderDataResponseDTO.getCredentialType();
                this.nonce = getHolderDataResponseDTO.getNonce();
                this.data = getHolderDataResponseDTO.getData();
                this.options = getHolderDataResponseDTO.getOptions();
            }
        }
    }

    public String getCredentialType() {
        return credentialType;
    }

    public GetHolderDataResponseDTO setCredentialType(String credentialType) {
        this.credentialType = credentialType;
        return this;
    }

    public String getNonce() {
        return nonce;
    }

    public GetHolderDataResponseDTO setNonce(String nonce) {
        this.nonce = nonce;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public GetHolderDataResponseDTO setData(Map<String, Object> data) {
        this.data = data;
        return this;
    }

    public LinkedHashMap<String, Object> getOptions() {
		return options;
	}

	public GetHolderDataResponseDTO setOptions(LinkedHashMap<String, Object> options) {
		this.options = options;
		return this;
	}

	@Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }
}
