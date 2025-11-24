package gov.moda.dw.issuer.vc.service.dto;

import gov.moda.dw.issuer.vc.domain.Credential;
import gov.moda.dw.issuer.vc.util.JsonUtils;
import jakarta.validation.constraints.NotBlank;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * credential list query response
 *
 * @version 20240902
 */
public class CredentialListResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<CredentialInfo> credentials;
    
    int totalPages;

    public CredentialListResponseDTO() {
    }

    public CredentialListResponseDTO(List<CredentialInfo> credentials, int totalPages) {
        this.credentials = credentials;
        this.totalPages = totalPages;
    }

    public List<CredentialInfo> getCredentials() {
        return credentials;
    }

    public CredentialListResponseDTO setCredentials(List<CredentialInfo> credentials) {
        this.credentials = credentials;
        return this;
    }

    public int getTotalPages() {
		return totalPages;
	}

	public CredentialListResponseDTO setTotalPages(int totalPages) {
		this.totalPages = totalPages;
		return this;
	}

	@Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }

    public static class CredentialInfo {

        private String cid;
        private String content;
        private String credentialStatus;

        public CredentialInfo() {
        }

        public CredentialInfo(String cid, String content, String credentialStatus) {
            this.cid = cid;
            this.content = content;
            this.credentialStatus = credentialStatus;
        }

        public CredentialInfo(Credential credential) {
            if (credential != null) {
                this.cid = credential.getCid();
                this.content = credential.getContent();
                this.credentialStatus = credential.getCredentialStatus();
            }
        }

        public String getCid() {
            return cid;
        }

        public CredentialInfo setCid(String cid) {
            this.cid = cid;
            return this;
        }

        public String getContent() {
            return content;
        }

        public CredentialInfo setContent(String content) {
            this.content = content;
            return this;
        }

        public String getCredentialStatus() {
            return credentialStatus;
        }

        public CredentialInfo setCredentialStatus(String credentialStatus) {
            this.credentialStatus = credentialStatus;
            return this;
        }

        @Override
        public String toString() {
            return JsonUtils.voToJs(this);
        }
    }
}
