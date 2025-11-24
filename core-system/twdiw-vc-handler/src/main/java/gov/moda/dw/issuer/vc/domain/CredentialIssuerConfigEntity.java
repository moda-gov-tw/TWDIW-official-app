package gov.moda.dw.issuer.vc.domain;

import gov.moda.dw.issuer.vc.util.JsonUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * entity of credential_issuer_config
 *
 * @version 20241028
 */
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
	private String db5ecret;

	@Column(name="db_iv")
	private String dbIv;

	@Column(name="app_url_scheme")
	private String appUrlScheme;

	@Column(name="credential_url")
	private String credentialUrl;

	public String getVcID() {
		return vcID;
	}

	public CredentialIssuerConfigEntity setVcID(String vcID) {
		this.vcID = vcID;
		return this;
	}

	public String getCredentialOffer() {
		return credentialOffer;
	}

	public CredentialIssuerConfigEntity setCredentialOffer(String credentialOffer) {
		this.credentialOffer = credentialOffer;
		return this;
	}

	public String getCredentialIssuerMeta() {
		return credentialIssuerMeta;
	}

	public CredentialIssuerConfigEntity setCredentialIssuerMeta(String credentialIssuerMeta) {
		this.credentialIssuerMeta = credentialIssuerMeta;
		return this;
	}

	public String getCredentialIssuerIdentifier() {
		return credentialIssuerIdentifier;
	}

	public CredentialIssuerConfigEntity setCredentialIssuerIdentifier(String credentialIssuerIdentifier) {
		this.credentialIssuerIdentifier = credentialIssuerIdentifier;
		return this;
	}

	public String getCredentialOfferUriPath() {
		return credentialOfferUriPath;
	}

	public CredentialIssuerConfigEntity setCredentialOfferUriPath(String credentialOfferUriPath) {
		this.credentialOfferUriPath = credentialOfferUriPath;
		return this;
	}

	public String getDb5ecret() {
		return db5ecret;
	}

	public CredentialIssuerConfigEntity setDb5ecret(String db5ecret) {
		this.db5ecret = db5ecret;
		return this;
	}

	public String getDbIv() {
		return dbIv;
	}

	public CredentialIssuerConfigEntity setDbIv(String dbIv) {
		this.dbIv = dbIv;
		return this;
	}

	public String getAppUrlScheme() {
		return appUrlScheme;
	}

	public CredentialIssuerConfigEntity setAppUrlScheme(String appUrlScheme) {
		this.appUrlScheme = appUrlScheme;
		return this;
	}

	public String getCredentialUrl() {
		return credentialUrl;
	}

	public CredentialIssuerConfigEntity setCredentialUrl(String credentialUrl) {
		this.credentialUrl = credentialUrl;
		return this;
	}

	@Override
    public String toString() {
        return JsonUtils.voToJs(this);
    }
}
