package gov.moda.dw.issuer.oidvci.domain;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity //標示為實體類別
@Table(name = "pre_auth_code") //DB_TABLE_NAME
public class PreAuthCodeEntity {

	@EmbeddedId
	private PreAuthCodeId pre_auth_code_id;

	@Column(name="client_id")
	private String clientID;

	@Column(name="create_time")
	private Timestamp createTime;

	@Column(name="expire_time")
	private Timestamp expireTime;

	@Column(name="code_status")
	private String codeStatus;

	@Column(name="credential_configuration_id")
	private String credentialConfigurationId;

	@Column(name="pre_code")
	private String preCode;

    @Column(name="tx_code")
    private String tx_code;

    @Column(name="CID")
    private String CID;

	public PreAuthCodeEntity() {

    }

    public PreAuthCodeEntity(PreAuthCodeId pre_auth_code_id) {
        this.pre_auth_code_id = pre_auth_code_id;
    }

	public PreAuthCodeId getPre_auth_code_id() {
		return pre_auth_code_id;
	}

	public String getClientID() {
		return clientID;
	}

	public void setClientID(String clientID) {
		this.clientID = clientID;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public Timestamp getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(Timestamp expireTime) {
		this.expireTime = expireTime;
	}

	public String getCodeStatus() {
		return codeStatus;
	}

	public void setCodeStatus(String codeStatus) {
		this.codeStatus = codeStatus;
	}

	public String getCredentialConfigurationId() {
		return credentialConfigurationId;
	}

	public void setCredentialConfigurationId(String credentialConfigurationId) {
		this.credentialConfigurationId = credentialConfigurationId;
	}

	public String getPreCode() {
		return preCode;
	}

	public void setPreCode(String preCode) {
		this.preCode = preCode;
	}

    public String getTxCode() {
        return tx_code;
    }

    public void setTxCode(String tx_code) {
        this.tx_code = tx_code;
    }

    public String getCID() {
        return CID;
    }

    public void setCID(String CID) {
        this.CID = CID;
    }


}
