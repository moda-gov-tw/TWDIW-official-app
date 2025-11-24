package gov.moda.dw.issuer.vc.service.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import gov.moda.dw.issuer.vc.util.JsonUtils;

/**
 * check setting response
 *
 * @version 20250218
 */
public class SettingCheckResponseDTO implements Serializable {

	@Serial
    private static final long serialVersionUID = 1L;
	
	private Map<String, List<String>> data;
	
	public SettingCheckResponseDTO() {
		
	}
	
	public SettingCheckResponseDTO(Map<String, List<String>> data) {
		this.data = data;
	}
	
	public Map<String, List<String>> getData() {
		return data;
	}

	public SettingCheckResponseDTO setData(Map<String, List<String>> data) {
		this.data = data;
		return this;
	}

	@Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }
}
