package gov.moda.dw.issuer.vc.service.dto.frontend;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

import gov.moda.dw.issuer.vc.util.JsonUtils;

/**
 * DID get issuer info response (from frontend service)
 *
 * @version 20241122
 */
public class DidGetIssuerInfoResponseDTO implements Serializable {

	@Serial
    private static final long serialVersionUID = 1L;
	
	private int code = -1;
    private String msg;
    private Data data;
    
    public DidGetIssuerInfoResponseDTO() {
    	
    }
    
    public DidGetIssuerInfoResponseDTO(int code, String msg, Data data) {
    	this.code = code;
    	this.msg = msg;
    	this.data = data;
    }
    
    public DidGetIssuerInfoResponseDTO(String respJson) {
    	if(respJson != null && !respJson.isBlank()) {
    		DidGetIssuerInfoResponseDTO didGetIssuerInfoResponseDTO = JsonUtils.jsToVo(respJson, this.getClass());
    		if(didGetIssuerInfoResponseDTO != null) {
    			this.code = didGetIssuerInfoResponseDTO.getCode();
    			this.msg = didGetIssuerInfoResponseDTO.getMsg();
    			this.data = didGetIssuerInfoResponseDTO.getData();
    		}
    	}
    }

	public int getCode() {
		return code;
	}

	public DidGetIssuerInfoResponseDTO setCode(int code) {
		this.code = code;
		return this;
	}

	public String getMsg() {
		return msg;
	}

	public DidGetIssuerInfoResponseDTO setMsg(String msg) {
		this.msg = msg;
		return this;
	}

	public Data getData() {
		return data;
	}

	public DidGetIssuerInfoResponseDTO setData(Data data) {
		this.data = data;
		return this;
	}
	
	@Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }
	
	public static class Data {
		private String did;
		private Map<String, Object> org;
		private int status = -1;
		private long createdAt;
		private long updatedAt;
		
		public Data() {
			
		}
		
		public Data(String did, Map<String, Object> org, int status, long createdAt, long updatedAt) {
			this.did = did;
			this.org = org;
			this.status = status;
			this.createdAt = createdAt;
			this.updatedAt = updatedAt;
		}
		
		
		
		public String getDid() {
			return did;
		}

		public Data setDid(String did) {
			this.did = did;
			return this;
		}

		public Map<String, Object> getOrg() {
			return org;
		}

		public Data setOrg(Map<String, Object> org) {
			this.org = org;
			return this;
		}

		public int getStatus() {
			return status;
		}

		public Data setStatus(int status) {
			this.status = status;
			return this;
		}

		public long getCreatedAt() {
			return createdAt;
		}

		public Data setCreatedAt(long createdAt) {
			this.createdAt = createdAt;
			return this;
		}

		public long getUpdatedAt() {
			return updatedAt;
		}

		public Data setUpdatedAt(long updatedAt) {
			this.updatedAt = updatedAt;
			return this;
		}

		@Override
	    public String toString() {
	        return JsonUtils.voToJs(this);
	    }
	}
}
