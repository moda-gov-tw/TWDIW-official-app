package gov.moda.dw.issuer.vc.service.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

import gov.moda.dw.issuer.vc.util.JsonUtils;

/**
 * sequence delete request
 *
 * @version 20250205
 */
public class SequenceDelRequestDTO implements Serializable {

	@Serial
    private static final long serialVersionUID = 1L;
	
	private Map<String, Object> metadata;
	
	private String reqJson;
	
	public SequenceDelRequestDTO() {
		
	}
	
	public SequenceDelRequestDTO(Map<String, Object> metadata) {
		this.metadata = metadata;
	}
	
	public SequenceDelRequestDTO(String reqJson) {
		if(reqJson != null && !reqJson.isBlank()) {
			SequenceDelRequestDTO sequenceDelRequest = JsonUtils.jsToVo(reqJson, this.getClass());
			if(sequenceDelRequest != null) {
				this.metadata = sequenceDelRequest.getMetadata();
			}
		}
	}

	public Map<String, Object> getMetadata() {
		return metadata;
	}

	public SequenceDelRequestDTO setMetadata(Map<String, Object> metadata) {
		this.metadata = metadata;
		return this;
	}
	
	public boolean validate() {
		return metadata != null && !metadata.isEmpty();
	}
	
	@Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }
}
