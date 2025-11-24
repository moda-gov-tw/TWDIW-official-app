package gov.moda.dw.issuer.vc.domain;

import java.sql.Timestamp;

import gov.moda.dw.issuer.vc.util.JsonUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * entity of credential_transfer
 *
 * @version 20250428
 */
@Entity //標示為實體類別
@Table(name = "credential_transfer") //DB_TABLE_NAME
public class CredentialTransferEntity {

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
	
	@Column(name="old_cid")
	private String oldCid;

	public String getTransactionID() {
		return transactionID;
	}

	public String getCredentialType() {
		return credentialType;
	}

	public String getData() {
		return data;
	}

	public Timestamp getCreatedTime() {
		return createdTime;
	}

	public Timestamp getExpiredTime() {
		return expiredTime;
	}

	public String getStatus() {
		return status;
	}

	public String getOldCid() {
		return oldCid;
	}

	public CredentialTransferEntity setOldCid(String oldCid) {
		this.oldCid = oldCid;
		return this;
	}

	public CredentialTransferEntity setTransactionID(String transactionID) {
		this.transactionID = transactionID;
		return this;
	}

	public CredentialTransferEntity setCredentialType(String credentialType) {
		this.credentialType = credentialType;
		return this;
	}

	public CredentialTransferEntity setData(String data) {
		this.data = data;
		return this;
	}

	public CredentialTransferEntity setCreatedTime(Timestamp createdTime) {
		this.createdTime = createdTime;
		return this;
	}

	public CredentialTransferEntity setExpiredTime(Timestamp expiredTime) {
		this.expiredTime = expiredTime;
		return this;
	}

	public CredentialTransferEntity setStatus(String status) {
		this.status = status;
		return this;
	}
	
	@Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }
}
