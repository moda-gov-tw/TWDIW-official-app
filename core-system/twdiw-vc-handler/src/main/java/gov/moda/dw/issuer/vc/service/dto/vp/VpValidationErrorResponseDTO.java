package gov.moda.dw.issuer.vc.service.dto.vp;

import java.io.Serial;
import java.io.Serializable;

import gov.moda.dw.issuer.vc.util.JsonUtils;
import jakarta.validation.constraints.NotBlank;

/**
 * VP validation error response (from vp service)
 *
 * @version 20240428
 */
public class VpValidationErrorResponseDTO implements Serializable {

	@Serial
    private static final long serialVersionUID = 1L;
	
	private Integer code;
	
	private String message;
	
	public VpValidationErrorResponseDTO() {
		
	}
	
	public VpValidationErrorResponseDTO(Integer code, String message) {
		this.code = code;
		this.message = message;
	}
	
	public VpValidationErrorResponseDTO(String respJson) {
		if(respJson != null && !respJson.isBlank()) {
			VpValidationErrorResponseDTO vpValidationErrorResponse = JsonUtils.jsToVo(respJson, this.getClass());
			if(vpValidationErrorResponse != null) {
				this.code = vpValidationErrorResponse.getCode();
				this.message = vpValidationErrorResponse.getMessage();
			}
		}
	}

	public Integer getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public VpValidationErrorResponseDTO setCode(Integer code) {
		this.code = code;
		return this;
	}

	public VpValidationErrorResponseDTO setMessage(String message) {
		this.message = message;
		return this;
	}
	
	public boolean validate() {
		return code != null && message != null && !message.isBlank();
	}
	
	@Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }
}
