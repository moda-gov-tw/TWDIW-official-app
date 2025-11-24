package gov.moda.dw.issuer.vc.service.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

import gov.moda.dw.issuer.vc.util.JsonUtils;

/**
 * update setting request
 *
 * @version 20250225
 */
public class SettingUpdateRequestDTO implements Serializable {

	@Serial
    private static final long serialVersionUID = 1L;
	
	private Map<String, String> updates;
	
	public SettingUpdateRequestDTO() {
		
	}
	
	public SettingUpdateRequestDTO(Map<String, String> updates) {
		this.updates = updates;
	}
	
	public SettingUpdateRequestDTO(String reqJson) {
		
		if(reqJson != null && !reqJson.isBlank()) {
			SettingUpdateRequestDTO settingUpdateRequest = JsonUtils.jsToVo(reqJson, this.getClass());
			if(settingUpdateRequest != null) {
				this.updates = settingUpdateRequest.getUpdates();
			}
		}
	}
	
	public boolean validate() {
		return updates != null && !updates.isEmpty();
	}

	public Map<String, String> getUpdates() {
		return updates;
	}

	public SettingUpdateRequestDTO setUpdates(Map<String, String> updates) {
		this.updates = updates;
		return this;
	}

	@Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }
}
