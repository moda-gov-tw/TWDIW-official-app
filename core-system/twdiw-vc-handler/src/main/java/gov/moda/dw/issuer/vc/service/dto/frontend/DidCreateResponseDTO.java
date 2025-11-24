package gov.moda.dw.issuer.vc.service.dto.frontend;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

import gov.moda.dw.issuer.vc.util.JsonUtils;

/**
 * DID create response (to frontend service)
 *
 * @version 20250603
 */
public class DidCreateResponseDTO implements Serializable {

	@Serial
    private static final long serialVersionUID = 1L;
	
	private int code = -1;
    private String msg;
    private Map<String, Object> data;
    
    public DidCreateResponseDTO() {
    	
    }
    
    public DidCreateResponseDTO(int code, String msg, Map<String, Object> data) {
    	this.code = code;
    	this.msg = msg;
    	this.data = data;
    }
    
    public DidCreateResponseDTO(String respJson) {
    	
    	if(respJson != null && !respJson.isBlank()) {
    		DidCreateResponseDTO didCreateResponse = JsonUtils.jsToVo(respJson, this.getClass());
    		if(didCreateResponse != null) {
    			this.code = didCreateResponse.getCode();
    			this.msg = didCreateResponse.getMsg();
    			this.data = didCreateResponse.getData();
    		}
    	}
    }

	public int getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public DidCreateResponseDTO setCode(int code) {
		this.code = code;
		return this;
	}

	public DidCreateResponseDTO setMsg(String msg) {
		this.msg = msg;
		return this;
	}

	public DidCreateResponseDTO setData(Map<String, Object> data) {
		this.data = data;
		return this;
	}
	
	@Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }
}
