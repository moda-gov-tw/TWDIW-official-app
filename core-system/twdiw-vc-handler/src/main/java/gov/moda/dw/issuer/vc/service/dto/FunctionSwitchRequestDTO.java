package gov.moda.dw.issuer.vc.service.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashMap;

import gov.moda.dw.issuer.vc.util.JsonUtils;
import jakarta.validation.constraints.NotBlank;

/**
 * set function switch request
 *
 * @version 20250421
 */

public class FunctionSwitchRequestDTO implements Serializable {

	@Serial
    private static final long serialVersionUID = 1L;
	
	@NotBlank
	private LinkedHashMap<String, Object> switches;
	
	public FunctionSwitchRequestDTO() {
		
	}
	
	public FunctionSwitchRequestDTO(LinkedHashMap<String, Object> switches) {
		this.switches = switches;
	}
	
	public FunctionSwitchRequestDTO(String reqJson) {
		
		if(reqJson != null && !reqJson.isBlank()) {
			FunctionSwitchRequestDTO functionSwitchRequest = JsonUtils.jsToVo(reqJson, this.getClass());
			if(functionSwitchRequest != null) {
				this.switches = functionSwitchRequest.getSwitches();
			}
		}
	}
	
	public boolean validate() {
		return switches != null && !switches.isEmpty();
	}

	public LinkedHashMap<String, Object> getSwitches() {
		return switches;
	}

	public FunctionSwitchRequestDTO setSwitches(LinkedHashMap<String, Object> switches) {
		this.switches = switches;
		return this;
	}
	
	@Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }
}
