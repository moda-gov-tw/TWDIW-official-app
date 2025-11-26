package gov.moda.dw.issuer.oidvci.domain;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity //標示為實體類別
@Table(name = "credential_access_token") //DB_TABLE_NAME
public class AccessTokenEntity {

	@Id //標示何者為Primary Key
	@Column(name="token_value")
	private String tokenValue;

	@Column(name="client_id")
	private String clientID;

	@Column(name="user_id")
	private String userID;

	@Column(name="create_time")
	private Timestamp createTime;

	@Column(name="expire_time")
	private Timestamp expireTime;

	@Column(name="nonce_from_idp")
	private String nonceFromIdp;

	@Column(name="c_nonce")
	private String cNonce;

	@Column(name="c_nonce_create_time")
	private Timestamp cNonceCreateTime;

	@Column(name="c_nonce_expire_time")
	private Timestamp cNonceExpireTime;

	@Column(name="authorization_details")
	private String authorizationDetails;

	@Column(name="pre_auth_code")
	private String preAuthCode;

	@Column(name="token_status")
	private String tokenStatus;

	@Column(name="additional_info")
	private String additionalInfo;

	public String getTokenValue() {
		return tokenValue;
	}

	public void setTokenValue(String tokenValue) {
		this.tokenValue = tokenValue;
	}

	public String getClientID() {
		return clientID;
	}

	public void setClientID(String clientID) {
		this.clientID = clientID;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
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

	public String getNonceFromIdp() {
		return nonceFromIdp;
	}

	public void setNonceFromIdp(String nonceFromIdp) {
		this.nonceFromIdp = nonceFromIdp;
	}

	public String getcNonce() {
		return cNonce;
	}

	public void setcNonce(String cNonce) {
		this.cNonce = cNonce;
	}

	public Timestamp getcNonceCreateTime() {
		return cNonceCreateTime;
	}

	public void setcNonceCreateTime(Timestamp cNonceCreateTime) {
		this.cNonceCreateTime = cNonceCreateTime;
	}

	public Timestamp getcNonceExpireTime() {
		return cNonceExpireTime;
	}

	public void setcNonceExpireTime(Timestamp cNonceExpireTime) {
		this.cNonceExpireTime = cNonceExpireTime;
	}

	public String getAuthorizationDetails() {
		return authorizationDetails;
	}

	public void setAuthorizationDetails(String authorizationDetails) {
		this.authorizationDetails = authorizationDetails;
	}

	public String getPreAuthCode() {
		return preAuthCode;
	}

	public void setPreAuthCode(String preAuthCode) {
		this.preAuthCode = preAuthCode;
	}

	public String getTokenStatus() {
		return tokenStatus;
	}

	public void setTokenStatus(String tokenStatus) {
		this.tokenStatus = tokenStatus;
	}

	public String getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}



}
