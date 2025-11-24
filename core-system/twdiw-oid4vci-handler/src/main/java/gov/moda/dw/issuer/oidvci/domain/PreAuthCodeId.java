package gov.moda.dw.issuer.oidvci.domain;

import java.io.Serializable;

import jakarta.persistence.Embeddable;

@Embeddable
public class PreAuthCodeId implements Serializable {

	private static final long serialVersionUID = 1L;
    private String user_id;
    private String nonce;
    
    public PreAuthCodeId() {

    }
    
    public PreAuthCodeId(String user_id, String nonce) {
        this.user_id = user_id;
        this.nonce = nonce;
    }

	public String getUser_id() {
		return user_id;
	}

	public String getNonce() {
		return nonce;
	}

	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((nonce == null) ? 0 : nonce.hashCode());
        result = prime * result + ((user_id == null) ? 0 : user_id.hashCode());
        return result;
    }
    
	@Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PreAuthCodeId other = (PreAuthCodeId) obj;
        if (nonce == null) {
            if (other.nonce != null)
                return false;
        } else if (!nonce.equals(other.nonce))
            return false;
        if (user_id == null) {
            if (other.user_id != null)
                return false;
        } else if (!user_id.equals(other.user_id))
            return false;
        return true;
    }
}
