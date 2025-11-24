package gov.moda.dw.issuer.vc.service.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

import gov.moda.dw.issuer.vc.util.JsonUtils;

/**
 * update setting response
 *
 * @version 20250225
 */
public class SettingUpdateResponseDTO implements Serializable {

	@Serial
    private static final long serialVersionUID = 1L;
	
	private Map<String, String> updatedData;
	
	public SettingUpdateResponseDTO() {
		
	}
	
	public SettingUpdateResponseDTO(Map<String, String> updatedData) {
		this.updatedData = updatedData;
	}

	public Map<String, String> getUpdatedData() {
		return updatedData;
	}

	public SettingUpdateResponseDTO setUpdatedData(Map<String, String> updatedData) {
		this.updatedData = updatedData;
		return this;
	}

	@Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }
}
