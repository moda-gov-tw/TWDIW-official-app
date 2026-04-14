package gov.moda.dw.manager.service.dto.custom;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DwIssuerVc504iReq {

	@JsonProperty("switches")
	private SwitchesDTO switches;

	public SwitchesDTO getSwitches() {
		return switches;
	}

	public void setSwitches(SwitchesDTO switches) {
		this.switches = switches;
	}

	
    
}
