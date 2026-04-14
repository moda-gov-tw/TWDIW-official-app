package gov.moda.dw.manager.service.dto.custom;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class SwitchesDTO {

	@JsonProperty("enable_tx_code")
	private boolean enableTxCode;

	@JsonProperty("enable_vc_transfer")
	private boolean enableVcTransfer;

	public boolean isEnableTxCode() {
		return enableTxCode;
	}

	public void setEnableTxCode(boolean enableTxCode) {
		this.enableTxCode = enableTxCode;
	}

	public boolean isEnableVcTransfer() {
		return enableVcTransfer;
	}

	public void setEnableVcTransfer(boolean enableVcTransfer) {
		this.enableVcTransfer = enableVcTransfer;
	}

}
