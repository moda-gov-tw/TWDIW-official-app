package gov.moda.dw.manager.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * OrgKeySetting 組織金鑰設定
 */
@Entity
@Table(name = "org_key_setting")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrgKeySetting implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * 組織代碼
     */
    @NotNull
    @Size(max = 255)
    @Column(name = "org_id", length = 255, nullable = false)
    private String orgId;

    /**
     * 金鑰代碼
     */
    @NotNull
    @Size(max = 50)
    @Column(name = "key_id", length = 50, nullable = false)
    private String keyId;

    /**
     * 金鑰備註
     */
    @Size(max = 100)
    @Column(name = "description", length = 100)
    private String description;

    /**
     * Curve25519 公鑰
     */
//    @Lob
    @Column(name = "public_key", nullable = false)
    private String publicKey;

    /**
     * Curve25519 私鑰
     */
//    @Lob
    @Column(name = "private_key")
    private String privateKey;

    /**
     * TOTP 金鑰
     */
//    @Lob
    @Column(name = "totp_key", nullable = false)
    private String totpKey;

    /**
     * HMAC 金鑰
     */
//    @Lob
    @Column(name = "hmac_key", nullable = false)
    private String hmacKey;

    /**
     * 是否啟用
     */
    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    /**
     * 建立時間
     */
    @NotNull
    @Column(name = "cr_datetime", nullable = false)
    private Instant crDatetime;

    /**
     * 修改時間
     */
    @Column(name = "up_datetime")
    private Instant upDatetime;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public OrgKeySetting id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrgId() {
        return this.orgId;
    }

    public OrgKeySetting orgId(String orgId) {
        this.setOrgId(orgId);
        return this;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getKeyId() {
        return this.keyId;
    }

    public OrgKeySetting keyId(String keyId) {
        this.setKeyId(keyId);
        return this;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getDescription() {
        return this.description;
    }

    public OrgKeySetting description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublicKey() {
        return this.publicKey;
    }

    public OrgKeySetting publicKey(String publicKey) {
        this.setPublicKey(publicKey);
        return this;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return this.privateKey;
    }

    public OrgKeySetting privateKey(String privateKey) {
        this.setPrivateKey(privateKey);
        return this;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getTotpKey() {
        return this.totpKey;
    }

    public OrgKeySetting totpKey(String totpKey) {
        this.setTotpKey(totpKey);
        return this;
    }

    public void setTotpKey(String totpKey) {
        this.totpKey = totpKey;
    }

    public String getHmacKey() {
        return this.hmacKey;
    }

    public OrgKeySetting hmacKey(String hmacKey) {
        this.setHmacKey(hmacKey);
        return this;
    }

    public void setHmacKey(String hmacKey) {
        this.hmacKey = hmacKey;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public OrgKeySetting isActive(Boolean isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Instant getCrDatetime() {
        return this.crDatetime;
    }

    public OrgKeySetting crDatetime(Instant crDatetime) {
        this.setCrDatetime(crDatetime);
        return this;
    }

    public void setCrDatetime(Instant crDatetime) {
        this.crDatetime = crDatetime;
    }

    public Instant getUpDatetime() {
        return this.upDatetime;
    }

    public OrgKeySetting upDatetime(Instant upDatetime) {
        this.setUpDatetime(upDatetime);
        return this;
    }

    public void setUpDatetime(Instant upDatetime) {
        this.upDatetime = upDatetime;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrgKeySetting)) {
            return false;
        }
        return getId() != null && getId().equals(((OrgKeySetting) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrgKeySetting{" +
            "id=" + getId() +
            ", orgId='" + getOrgId() + "'" +
            ", keyId='" + getKeyId() + "'" +
            ", description='" + getDescription() + "'" +
            ", publicKey='" + getPublicKey() + "'" +
            ", privateKey='" + getPrivateKey() + "'" +
            ", totpKey='" + getTotpKey() + "'" +
            ", hmacKey='" + getHmacKey() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", crDatetime='" + getCrDatetime() + "'" +
            ", upDatetime='" + getUpDatetime() + "'" +
            "}";
    }
}
