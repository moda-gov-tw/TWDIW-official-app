package gov.moda.dw.issuer.vc.vo;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import gov.moda.dw.issuer.vc.util.JsonUtils;
import jakarta.validation.constraints.NotBlank;

public class VcSchema implements Serializable{
	
	@Serial
    private static final long serialVersionUID = 1L;
	
	@JsonProperty("$id")
	@NotBlank
	private String id;
	
	@JsonProperty("$schema")
	@NotBlank
	private String schema;
	
	@NotBlank
	private String title;
	
	@NotBlank
	private String description;
	
	@NotBlank
	private String type;
	
	private PropertiesDef properties;
	
	public VcSchema() {
		
	}
	
	public VcSchema(String id, String schema, String title, String description, String type, PropertiesDef properties) {
		this.id = id;
		this.schema = schema;
		this.title = title;
		this.description = description;
		this.type = type;
		this.properties = properties;
	}
	
	public VcSchema(String reqJson) {
		
		if(reqJson != null && !reqJson.isBlank()) {
			VcSchema vcSchema = JsonUtils.jsToVo(reqJson, this.getClass());
			if(vcSchema != null) {
				this.id = vcSchema.getId();
				this.schema = vcSchema.getSchema();
				this.title = vcSchema.getTitle();
				this.description = vcSchema.getDescription();
				this.type = vcSchema.getType();
				this.properties = vcSchema.getProperties();
			}
		}
	}

	public String getId() {
		return id;
	}

	public VcSchema setId(String id) {
		this.id = id;
		return this;
	}

	public String getSchema() {
		return schema;
	}

	public VcSchema setSchema(String schema) {
		this.schema = schema;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public VcSchema setTitle(String title) {
		this.title = title;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public VcSchema setDescription(String description) {
		this.description = description;
		return this;
	}

	public String getType() {
		return type;
	}

	public VcSchema setType(String type) {
		this.type = type;
		return this;
	}

	public PropertiesDef getProperties() {
		return properties;
	}

	public VcSchema setProperties(PropertiesDef properties) {
		this.properties = properties;
		return this;
	}
	
	public boolean validate() {
		return id != null && !id.isBlank() &&
				schema != null && !schema.isBlank() &&
				title != null && !title.isBlank() &&
				description != null && !description.isBlank() &&
				type != null && !type.isBlank() &&
				properties != null && properties.validate();
	}
	
	@Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }
	
	public static class PropertiesDef{
		private CredentialSubject credentialSubject;
		
		public PropertiesDef() {
			
		}
		
		public PropertiesDef(CredentialSubject credentialSubject) {
			this.credentialSubject = credentialSubject;
		}
		
		public PropertiesDef(String propertiesDefJson) {
			if(propertiesDefJson != null && !propertiesDefJson.isBlank()) {
				PropertiesDef propertiesDef = JsonUtils.jsToVo(propertiesDefJson, this.getClass());
				if(propertiesDef != null) {
					this.credentialSubject = propertiesDef.getCredentialSubject();
				}
			}
		}

		public CredentialSubject getCredentialSubject() {
			return credentialSubject;
		}

		public PropertiesDef setCredentialSubject(CredentialSubject credentialSubject) {
			this.credentialSubject = credentialSubject;
			return this;
		}
		
		public boolean validate() {
			return credentialSubject != null && credentialSubject.validate();
		}
		
		@Override
	    public String toString() {
	        return JsonUtils.voToJs(this);
	    }
	}
	
	public static class CredentialSubject{
		private String title;
		
		private String type;
		
		private LinkedHashMap<String, Object> properties;
		
		private Boolean additionalProperties;
		
		private List<String> required;
		
		public CredentialSubject() {
			
		}
		
		public CredentialSubject(String title, String type, LinkedHashMap<String, Object> properties, Boolean additionalProperties, List<String> required) {
			this.title = title;
			this.type = type;
			this.properties = properties;
			this.additionalProperties = additionalProperties;
			this.required = required;
		}
		
		public CredentialSubject(String credentialSubjectJson) {
			if(credentialSubjectJson != null && !credentialSubjectJson.isBlank()) {
				CredentialSubject credentialSubject = JsonUtils.jsToVo(credentialSubjectJson, this.getClass());
				if(credentialSubject != null) {
					this.title = credentialSubject.getTitle();
					this.type = credentialSubject.getType();
					this.properties = credentialSubject.getProperties();
					this.additionalProperties = credentialSubject.isAdditionalProperties();
					this.required = credentialSubject.getRequired();
				}
			}
		}

		public String getTitle() {
			return title;
		}

		public CredentialSubject setTitle(String title) {
			this.title = title;
			return this;
		}

		public String getType() {
			return type;
		}

		public CredentialSubject setType(String type) {
			this.type = type;
			return this;
		}

		public LinkedHashMap<String, Object> getProperties() {
			return properties;
		}

		public CredentialSubject setProperties(LinkedHashMap<String, Object> properties) {
			this.properties = properties;
			return this;
		}

		public Boolean isAdditionalProperties() {
			return additionalProperties;
		}

		public CredentialSubject setAdditionalProperties(Boolean additionalProperties) {
			this.additionalProperties = additionalProperties;
			return this;
		}

		public List<String> getRequired() {
			return required;
		}

		public CredentialSubject setRequired(List<String> required) {
			this.required = required;
			return this;
		}
		
		public boolean validate() {
			return title != null && !title.isBlank() &&
					type != null && !type.isBlank() &&
					properties != null && !properties.isEmpty() &&
					additionalProperties != null &&
					required != null && !required.isEmpty();
		}
		
		@Override
	    public String toString() {
	        return JsonUtils.voToJs(this);
	    }
	}
}
