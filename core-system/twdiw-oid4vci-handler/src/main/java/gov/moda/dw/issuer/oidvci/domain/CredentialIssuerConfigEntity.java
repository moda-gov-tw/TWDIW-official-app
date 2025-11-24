package gov.moda.dw.issuer.oidvci.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity //標示為實體類別
@Table(name = "credential_issuer_config") //DB_TABLE_NAME
public class CredentialIssuerConfigEntity {

	@Id //標示何者為Primary Key
	@Column(name="vc_id")
	private String vcID;

	@Column(name="credential_offer")
	private String credentialOffer;

	@Column(name="credential_issuer_meta")
	private String credentialIssuerMeta;

	@Column(name="credential_issuer_identifier")
	private String credentialIssuerIdentifier;

	@Column(name="credential_offer_uri_path")
	private String credentialOfferUriPath;

	@Column(name="db_secret")
	private String dbSecret;

	@Column(name="db_iv")
	private String dbIv;

	@Column(name="app_url_scheme")
	private String appUrlScheme;

    @Column(name="credential_url")
    private String credentialUrl;

	public String getVcID() {
		return vcID;
	}

	public void setVcID(String vcID) {
		this.vcID = vcID;
	}

	public String getCredentialOffer() {
		return credentialOffer;
	}

	public void setCredentialOffer(String credentialOffer) {
		this.credentialOffer = credentialOffer;
	}

	public String getCredentialIssuerMeta() {
		return credentialIssuerMeta;
	}

	public void setCredentialIssuerMeta(String credentialIssuerMeta) {
		this.credentialIssuerMeta = credentialIssuerMeta;
	}

	public String getCredentialIssuerIdentifier() {
		return credentialIssuerIdentifier;
	}

	public void setCredentialIssuerIdentifier(String credentialIssuerIdentifier) {
		this.credentialIssuerIdentifier = credentialIssuerIdentifier;
	}

	public String getCredentialOfferUriPath() {
		return credentialOfferUriPath;
	}

	public void setCredentialOfferUriPath(String credentialOfferUriPath) {
		this.credentialOfferUriPath = credentialOfferUriPath;
	}

	public String getDbSecret() {
		return dbSecret;
	}

	public void setDbSecret(String dbSecret) {
		this.dbSecret = dbSecret;
	}

	public String getDbIv() {
		return dbIv;
	}

	public void setDbIv(String dbIv) {
		this.dbIv = dbIv;
	}

	public String getAppUrlScheme() {
		return appUrlScheme;
	}

	public void setAppUrlScheme(String appUrlScheme) {
		this.appUrlScheme = appUrlScheme;
	}

    public String getCredentialUrl() {
        return credentialUrl;
    }

    public void setCredentialUrl(String credentialUrl) {
        this.credentialUrl = credentialUrl;
    }
}
