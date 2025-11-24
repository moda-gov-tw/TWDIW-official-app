package gov.moda.dw.issuer.vc.service.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

import gov.moda.dw.issuer.vc.util.JsonUtils;
import jakarta.validation.constraints.NotBlank;

/**
 * sequence generation request
 *
 * @version 20241017
 */
public class SequenceRequestDTO implements Serializable {

	@Serial
    private static final long serialVersionUID = 1L;
	
	@NotBlank
    private String schemaId;
	
    private Map<String, Object> vcSchema;
	
	@NotBlank
    private String effectiveTimeUnit;
	
    private int effectiveTimeValue;
    
    private Map<String, Object> metadata;
	
	private String reqJson;
	
	public SequenceRequestDTO() {
		
	}
	
	public SequenceRequestDTO(String schemaId, 
							Map<String, Object> vcSchema,
							String effectiveTimeUnit,
							int effectiveTimeValue,
							Map<String, Object> metadata) {
		this.schemaId = schemaId;
		this.vcSchema = vcSchema;
		this.effectiveTimeUnit = effectiveTimeUnit;
		this.effectiveTimeValue = effectiveTimeValue;
		this.metadata = metadata;
	}
	
	public SequenceRequestDTO(String reqJson) {
		if (reqJson != null && !reqJson.isBlank()) {
			SequenceRequestDTO sequenceRequest = JsonUtils.jsToVo(reqJson, this.getClass());
			if(sequenceRequest != null) {
				this.schemaId = sequenceRequest.getSchemaId();
				this.vcSchema = sequenceRequest.getVcSchema();
				this.effectiveTimeUnit = sequenceRequest.getEffectiveTimeUnit();
				this.effectiveTimeValue = sequenceRequest.getEffectiveTimeValue();
				this.metadata = sequenceRequest.getMetadata();
			}
		}
	}

	public String getSchemaId() {
		return schemaId;
	}

	public SequenceRequestDTO setSchemaId(String schemaId) {
		this.schemaId = schemaId;
		return this;
	}

	public Map<String, Object> getVcSchema() {
		return vcSchema;
	}

	public SequenceRequestDTO setVcSchema(Map<String, Object> vcSchema) {
		this.vcSchema = vcSchema;
		return this;
	}

	public String getEffectiveTimeUnit() {
		return effectiveTimeUnit;
	}

	public SequenceRequestDTO setEffectiveTimeUnit(String effectiveTimeUnit) {
		this.effectiveTimeUnit = effectiveTimeUnit;
		return this;
	}

	public int getEffectiveTimeValue() {
		return effectiveTimeValue;
	}

	public SequenceRequestDTO setEffectiveTimeValue(int effectiveTimeValue) {
		this.effectiveTimeValue = effectiveTimeValue;
		return this;
	}
	
	public Map<String, Object> getMetadata() {
		return metadata;
	}

	public SequenceRequestDTO setMetadata(Map<String, Object> metadata) {
		this.metadata = metadata;
		return this;
	}

	public boolean validate() {
		return schemaId != null && !schemaId.isBlank() &&
				vcSchema != null && !vcSchema.isEmpty() &&
				effectiveTimeUnit != null && !effectiveTimeUnit.isBlank() &&
				effectiveTimeValue > 0 &&
				metadata != null && !metadata.isEmpty();
	}
	
	@Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }
}
