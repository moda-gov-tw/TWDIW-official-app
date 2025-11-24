package gov.moda.dw.issuer.vc.service.dto.vp;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import gov.moda.dw.issuer.vc.util.JsonUtils;
import jakarta.validation.constraints.NotBlank;

/**
 * VP validation response (from vp service)
 *
 * @version 20240428
 */
public class VpValidationResponseDTO implements Serializable {

	@Serial
    private static final long serialVersionUID = 1L;
	
	@NotBlank
	private String clientId;
	
	@NotBlank
	private String nonce;
	
	private List<Vcs> vcs;
	
	public VpValidationResponseDTO() {
		
	}
	
	public VpValidationResponseDTO(String clientId, String nonce, List<Vcs> vcs) {
		this.clientId = clientId;
		this.nonce = nonce;
		this.vcs = vcs;
	}
	
	public VpValidationResponseDTO(String respJson) {
		if(respJson != null && !respJson.isBlank()) {
			VpValidationResponseDTO vpValidationResponseDTO = JsonUtils.jsToVo(respJson, this.getClass());
			if(vpValidationResponseDTO != null) {
				this.clientId = vpValidationResponseDTO.getClientId();
				this.nonce = vpValidationResponseDTO.getNonce();
				this.vcs = vpValidationResponseDTO.getVcs();
			}
		}
	}

	public String getClientId() {
		return clientId;
	}

	public String getNonce() {
		return nonce;
	}

	public List<Vcs> getVcs() {
		return vcs;
	}

	public VpValidationResponseDTO setClientId(String clientId) {
		this.clientId = clientId;
		return this;
	}

	public VpValidationResponseDTO setNonce(String nonce) {
		this.nonce = nonce;
		return this;
	}

	public VpValidationResponseDTO setVcs(List<Vcs> vcs) {
		this.vcs = vcs;
		return this;
	}
	
	public boolean validate() {
		return clientId != null && !clientId.isBlank() &&
				nonce != null && !nonce.isBlank() &&
				vcs != null && !vcs.isEmpty();
	}
	
	@Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }
	
	public static class Vcs {
		
		private String vpPath;
		
		private String vcPath;
		
		private Credential credential;
		
		private boolean limitDisclosureSupported;
		
		public Vcs() {
			
		}
		
		public Vcs(String vpPath, String vcPath, Credential credential, boolean limitDisclosureSupported) {
			this.vpPath = vpPath;
			this.vcPath = vcPath;
			this.credential = credential;
			this.limitDisclosureSupported = limitDisclosureSupported;
		}
		
		public Vcs(String vcsJson) {
			if(vcsJson != null && !vcsJson.isBlank()) {
				Vcs vcs = JsonUtils.jsToVo(vcsJson, this.getClass());
				if(vcs != null) {
					vpPath = vcs.getVpPath();
					vcPath = vcs.getVcPath();
					credential = vcs.getCredential();
					limitDisclosureSupported = vcs.isLimitDisclosureSupported();
				}
			}
		}
		
		public String getVpPath() {
			return vpPath;
		}

		public String getVcPath() {
			return vcPath;
		}

		public Credential getCredential() {
			return credential;
		}

		public boolean isLimitDisclosureSupported() {
			return limitDisclosureSupported;
		}

		public Vcs setVpPath(String vpPath) {
			this.vpPath = vpPath;
			return this;
		}

		public Vcs setVcPath(String vcPath) {
			this.vcPath = vcPath;
			return this;
		}

		public Vcs setCredential(Credential credential) {
			this.credential = credential;
			return this;
		}

		public Vcs setLimitDisclosureSupported(boolean limitDisclosureSupported) {
			this.limitDisclosureSupported = limitDisclosureSupported;
			return this;
		}

		public boolean validate() {
			return vpPath != null && !vpPath.isBlank() &&
					vcPath != null && !vcPath.isBlank() &&
					credential != null && credential.validate();
		}
		
		@Override
	    public String toString() {
	        return JsonUtils.voToJs(this);
	    }
		
		public static class Credential{
			
			@JsonProperty("@context")
			private List<String> context;
			
			private List<String> type;
			
			private LinkedHashMap<String, Object> credentialStatus;
			
			private LinkedHashMap<String, Object> credentialSchema;
			
			private String id;
			
			private String issuer;
			
			private String issuanceDate;
			
			private String expirationDate;
			
			private LinkedHashMap<String, Object> credentialSubject;
			
			public Credential() {
				
			}
			
			public Credential(List<String> context, List<String> type, LinkedHashMap<String, Object> credentialStatus,
					LinkedHashMap<String, Object> credentialSchema, String id, String issuer, String issuanceDate,
					String expirationDate, LinkedHashMap<String, Object> credentialSubject) {
				this.context = context;
				this.type = type;
				this.credentialStatus = credentialStatus;
				this.credentialSchema = credentialSchema;
				this.id = id;
				this.issuer = issuer;
				this.issuanceDate = issuanceDate;
				this.expirationDate = expirationDate;
				this.credentialSubject = credentialSubject;
			}
			
			public Credential(String credentialJson) {
				if(credentialJson != null && !credentialJson.isBlank()) {
					Credential credential = JsonUtils.jsToVo(credentialJson, this.getClass());
					if(credential != null) {
						context = credential.getContext();
						type = credential.getType();
						credentialStatus = credential.getCredentialStatus();
						credentialSchema = credential.getCredentialSchema();
						id = credential.getId();
						issuer = credential.getIssuer();
						issuanceDate = credential.getIssuanceDate();
						expirationDate = credential.getExpirationDate();
						credentialSubject = credential.getCredentialSubject();
					}
				}
			}

			public List<String> getContext() {
				return context;
			}

			public List<String> getType() {
				return type;
			}

			public LinkedHashMap<String, Object> getCredentialStatus() {
				return credentialStatus;
			}

			public LinkedHashMap<String, Object> getCredentialSchema() {
				return credentialSchema;
			}

			public String getId() {
				return id;
			}

			public String getIssuer() {
				return issuer;
			}

			public String getIssuanceDate() {
				return issuanceDate;
			}

			public String getExpirationDate() {
				return expirationDate;
			}

			public LinkedHashMap<String, Object> getCredentialSubject() {
				return credentialSubject;
			}

			public Credential setContext(List<String> context) {
				this.context = context;
				return this;
			}

			public Credential setType(List<String> type) {
				this.type = type;
				return this;
			}

			public Credential setCredentialStatus(LinkedHashMap<String, Object> credentialStatus) {
				this.credentialStatus = credentialStatus;
				return this;
			}

			public Credential setCredentialSchema(LinkedHashMap<String, Object> credentialSchema) {
				this.credentialSchema = credentialSchema;
				return this;
			}

			public Credential setId(String id) {
				this.id = id;
				return this;
			}

			public Credential setIssuer(String issuer) {
				this.issuer = issuer;
				return this;
			}

			public Credential setIssuanceDate(String issuanceDate) {
				this.issuanceDate = issuanceDate;
				return this;
			}

			public Credential setExpirationDate(String expirationDate) {
				this.expirationDate = expirationDate;
				return this;
			}

			public Credential setCredentialSubject(LinkedHashMap<String, Object> credentialSubject) {
				this.credentialSubject = credentialSubject;
				return this;
			}
			
			public boolean validate() {
				return context != null && !context.isEmpty() &&
						type != null && !type.isEmpty() &&
						credentialStatus != null && !credentialStatus.isEmpty() &&
						credentialSchema != null && !credentialSchema.isEmpty() &&
						id != null && !id.isBlank() &&
						issuer != null && !issuer.isBlank() &&
						issuanceDate != null && !issuanceDate.isBlank() &&
						expirationDate != null && !expirationDate.isBlank() &&
						credentialSubject != null && !credentialSubject.isEmpty();
			}
			
			@Override
		    public String toString() {
		        return JsonUtils.voToJs(this);
		    }
		}
	}
}
