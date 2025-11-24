package gov.moda.dw.issuer.vc.domain;

import java.sql.Timestamp;

import gov.moda.dw.issuer.vc.util.JsonUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * entity of credential_data
 *
 * @version 20250526
 */
@Entity //標示為實體類別
@Table(name = "credential_data") //DB_TABLE_NAME
public class CredentialDataEntity {

	@Id //標示何者為Primary Key
	@Column(name="transaction_id")
	private String transactionID;
	
	@Column(name="credential_type")
	private String credentialType;
	
	@Column(name="data")
	private String data;
	
	@Column(name="created_time")
	private Timestamp createdTime;
	
	@Column(name="expired_time")
	private Timestamp expiredTime;
	
	@Column(name="status")
	private String status;
	
	@Column(name="options")
	private String options;

	public String getTransactionID() {
		return transactionID;
	}

	public CredentialDataEntity setTransactionID(String transactionID) {
		this.transactionID = transactionID;
		return this;
	}

	public String getCredentialType() {
		return credentialType;
	}

	public CredentialDataEntity setCredentialType(String credentialType) {
		this.credentialType = credentialType;
		return this;
	}

	public String getData() {
		return data;
	}

	public CredentialDataEntity setData(String data) {
		this.data = data;
		return this;
	}

	public Timestamp getCreatedTime() {
		return createdTime;
	}

	public CredentialDataEntity setCreatedTime(Timestamp createdTime) {
		this.createdTime = createdTime;
		return this;
	}

	public Timestamp getExpiredTime() {
		return expiredTime;
	}

	public CredentialDataEntity setExpiredTime(Timestamp expiredTime) {
		this.expiredTime = expiredTime;
		return this;
	}

	public String getStatus() {
		return status;
	}

	public CredentialDataEntity setStatus(String status) {
		this.status = status;
		return this;
	}
	
	public String getOptions() {
		return options;
	}

	public CredentialDataEntity setOptions(String options) {
		this.options = options;
		return this;
	}

	@Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }
}
