package gov.moda.dw.issuer.vc.vo;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import gov.moda.dw.issuer.vc.util.JsonUtils;
import jakarta.validation.constraints.NotBlank;

/**
 * credential issuer metadata
 *
 * @version 20241206
 */
public class CredentialIssuerMetadata implements Serializable{

	@Serial
    private static final long serialVersionUID = 1L;
	
	@JsonProperty("credential_issuer")
	@NotBlank
	private String credentialIssuer;
	
	@JsonProperty("credential_endpoint")
	@NotBlank
    private String credentialEndpoint;
    
	@JsonProperty("credential_configurations_supported")
	@NotBlank
	private Map<String, CredentialConfigurationSupported> credentialConfigurationsSupported;
	
	public CredentialIssuerMetadata() {
		
	}
	
	public CredentialIssuerMetadata(String credentialIssuer, String credentialEndpoint, Map<String, CredentialConfigurationSupported> credentialConfigurationsSupported) {
		this.credentialIssuer = credentialIssuer;
		this.credentialEndpoint = credentialEndpoint;
		this.credentialConfigurationsSupported = credentialConfigurationsSupported;
	}
	
	public CredentialIssuerMetadata(String metaJson) {
		if(metaJson != null && !metaJson.isBlank()) {
			CredentialIssuerMetadata credentialIssuerMetadata = JsonUtils.jsToVo(metaJson, this.getClass());
			if(credentialIssuerMetadata != null) {
				this.credentialIssuer = credentialIssuerMetadata.getCredentialIssuer();
				this.credentialEndpoint = credentialIssuerMetadata.getCredentialEndpoint();
				this.credentialConfigurationsSupported = credentialIssuerMetadata.getCredentialConfigurationsSupported();
			}
		}
	}

	public String getCredentialIssuer() {
		return credentialIssuer;
	}

	public CredentialIssuerMetadata setCredentialIssuer(String credentialIssuer) {
		this.credentialIssuer = credentialIssuer;
		return this;
	}

	public String getCredentialEndpoint() {
		return credentialEndpoint;
	}

	public CredentialIssuerMetadata setCredentialEndpoint(String credentialEndpoint) {
		this.credentialEndpoint = credentialEndpoint;
		return this;
	}

	public Map<String, CredentialConfigurationSupported> getCredentialConfigurationsSupported() {
		return credentialConfigurationsSupported;
	}

	public CredentialIssuerMetadata setCredentialConfigurationsSupported(
			Map<String, CredentialConfigurationSupported> credentialConfigurationsSupported) {
		this.credentialConfigurationsSupported = credentialConfigurationsSupported;
		return this;
	}
	
	public boolean validate() {
		return credentialIssuer != null && !credentialIssuer.isBlank() &&
				credentialEndpoint != null && !credentialEndpoint.isBlank() &&
				credentialConfigurationsSupported != null && !credentialConfigurationsSupported.isEmpty();
	}
	
	@Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }
	
	public static class CredentialConfigurationSupported {
        private String format;
        
        private String scope;
        
        @JsonProperty("cryptographic_binding_methods_supported")
        private List<String> cryptographicBindingMethodsSupported;
        
        @JsonProperty("credential_signing_alg_values_supported")
        private List<String> credentialSigningAlgValuesSupported;
        
        @JsonProperty("credential_definition")
        private CredentialDefinition credentialDefinition;
        
        @JsonProperty("proof_types_supported")
        private Map<String, Object> proofTypesSupported;
        
        private List<Object> display;

        public CredentialConfigurationSupported() {
    		
    	}
    	
    	public CredentialConfigurationSupported(String format, String scope, List<String> cryptographicBindingMethodsSupported, 
    			List<String> credentialSigningAlgValuesSupported, CredentialDefinition credentialDefinition,
    			Map<String, Object> proofTypesSupported, List<Object> display) {
    		this.format = format;
    		this.scope = scope;
    		this.cryptographicBindingMethodsSupported = cryptographicBindingMethodsSupported;
    		this.credentialSigningAlgValuesSupported = credentialSigningAlgValuesSupported;
    		this.credentialDefinition = credentialDefinition;
    		this.proofTypesSupported = proofTypesSupported;
    		this.display = display;
    	}
    	
    	public CredentialConfigurationSupported(String credentialConfigurationJson) {
    		if(credentialConfigurationJson != null && !credentialConfigurationJson.isBlank()) {
    			CredentialConfigurationSupported credentialConfigurationSupported = JsonUtils.jsToVo(credentialConfigurationJson, this.getClass());
    			if(credentialConfigurationSupported != null) {
    				this.format = credentialConfigurationSupported.getFormat();
    				this.scope = credentialConfigurationSupported.getScope();
    				this.cryptographicBindingMethodsSupported = credentialConfigurationSupported.getCryptographicBindingMethodsSupported();
    				this.credentialSigningAlgValuesSupported = credentialConfigurationSupported.getCredentialSigningAlgValuesSupported();
    				this.credentialDefinition = credentialConfigurationSupported.getCredentialDefinition();
    				this.proofTypesSupported = credentialConfigurationSupported.getProofTypesSupported();
    				this.display = credentialConfigurationSupported.getDisplay();
    			}
    		}
    	}

		public String getFormat() {
			return format;
		}

		public CredentialConfigurationSupported setFormat(String format) {
			this.format = format;
			return this;
		}

		public String getScope() {
			return scope;
		}

		public CredentialConfigurationSupported setScope(String scope) {
			this.scope = scope;
			return this;
		}

		public List<String> getCryptographicBindingMethodsSupported() {
			return cryptographicBindingMethodsSupported;
		}

		public CredentialConfigurationSupported setCryptographicBindingMethodsSupported(List<String> cryptographicBindingMethodsSupported) {
			this.cryptographicBindingMethodsSupported = cryptographicBindingMethodsSupported;
			return this;
		}

		public List<String> getCredentialSigningAlgValuesSupported() {
			return credentialSigningAlgValuesSupported;
		}

		public CredentialConfigurationSupported setCredentialSigningAlgValuesSupported(List<String> credentialSigningAlgValuesSupported) {
			this.credentialSigningAlgValuesSupported = credentialSigningAlgValuesSupported;
			return this;
		}

		public CredentialDefinition getCredentialDefinition() {
			return credentialDefinition;
		}

		public CredentialConfigurationSupported setCredentialDefinition(CredentialDefinition credentialDefinition) {
			this.credentialDefinition = credentialDefinition;
			return this;
		}

		public Map<String, Object> getProofTypesSupported() {
			return proofTypesSupported;
		}

		public CredentialConfigurationSupported setProofTypesSupported(Map<String, Object> proofTypesSupported) {
			this.proofTypesSupported = proofTypesSupported;
			return this;
		}

		public List<Object> getDisplay() {
			return display;
		}

		public CredentialConfigurationSupported setDisplay(List<Object> display) {
			this.display = display;
			return this;
		}
		
		public boolean validate() {
			return format != null && !format.isBlank() &&
					scope != null && !scope.isBlank() &&
					cryptographicBindingMethodsSupported != null && !cryptographicBindingMethodsSupported.isEmpty() &&
					credentialSigningAlgValuesSupported != null && !credentialSigningAlgValuesSupported.isEmpty() &&
					credentialDefinition != null && credentialDefinition.validate() &&
					proofTypesSupported != null && !proofTypesSupported.isEmpty() &&
					display != null && !display.isEmpty();
		}
		
		@Override
	    public String toString() {
	        return JsonUtils.voToJs(this);
	    }
    }
	
	public static class CredentialDefinition {
        private List<String> type;
        private LinkedHashMap<String, Object> credentialSubject;

        public CredentialDefinition() {
    		
    	}
    	
    	public CredentialDefinition(List<String> type, LinkedHashMap<String, Object> credentialSubject) {
    		this.type = type;
    		this.credentialSubject = credentialSubject;
    	}
    	
    	public CredentialDefinition(String credentialDefinitionJson) {
    		if(credentialDefinitionJson != null && !credentialDefinitionJson.isBlank()) {
    			CredentialDefinition credentialDefinition = JsonUtils.jsToVo(credentialDefinitionJson, this.getClass());
    			if(credentialDefinition != null) {
    				this.type = credentialDefinition.getType();
    				this.credentialSubject = credentialDefinition.getCredentialSubject();
    			}
    		}
    	}

		public List<String> getType() {
			return type;
		}

		public CredentialDefinition setType(List<String> type) {
			this.type = type;
			return this;
		}

		public LinkedHashMap<String, Object> getCredentialSubject() {
			return credentialSubject;
		}

		public CredentialDefinition setCredentialSubject(LinkedHashMap<String, Object> credentialSubject) {
			this.credentialSubject = credentialSubject;
			return this;
		}
		
		public boolean validate() {
			return type != null && !type.isEmpty() &&
					credentialSubject != null && !credentialSubject.isEmpty();
		}
		
		@Override
	    public String toString() {
	        return JsonUtils.voToJs(this);
	    }
    }
}
