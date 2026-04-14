package gov.moda.dw.manager.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * VPItem VP列表
 */
@Entity
@Table(name = "vp_item")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VPItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * VP編號
     */
    @NotNull
    @Size(max = 50)
    @Column(name = "serial_no", length = 50, nullable = false, unique = true)
    private String serialNo;

    /**
     * VP名稱
     */
    @NotNull
    @Size(max = 50)
    @Column(name = "name", length = 50, nullable = false)
    private String name;

    /**
     * 建立者
     */
    @NotNull
    @Column(name = "cr_user", nullable = false)
    private Long crUser;

    /**
     * 建立時間
     */
    @NotNull
    @Column(name = "cr_datetime", nullable = false)
    private Instant crDatetime;

    /**
     * 單位
     */
    @NotNull
    @Size(max = 255)
    @Column(name = "business_id", nullable = false)
    private String businessId;

    /**
     * 呈現定義
     */
    //@Lob
    @Column(name = "presentation_definition", nullable = false)
    private String presentationDefinition;

    /**
     * 驗證目的
     */
    @NotNull
    @Size(max = 100)
    @Column(name = "purpose", length = 100, nullable = false)
    private String purpose;

    /**
     * 使用者條款
     */
    //@Lob
    @Column(name = "terms", nullable = false)
    private String terms;

    /**
     * 修改者
     */
    @Column(name = "up_user")
    private Long upUser;

    /**
     * 修改時間
     */
    @Column(name = "up_datetime")
    private Instant upDatetime;

    /**
     * 建立群組資訊
     */
    //@Lob
    @Column(name = "group_info")
    private String groupInfo;

    /**
     * 是否為靜態 QRCode 模式
     */
    @Column(name = "is_static")
    private Boolean isStatic;

    /**
     * 是否為 Offline 模式
     */
    @Column(name = "is_offline")
    private Boolean isOffline;

    /**
     * 標籤
     */
    @Size(max = 100)
    @Column(name = "tag", length = 100)
    private String tag;

    /**
     * 欄位資訊
     */
    //@Lob
    @Column(name = "field_info")
    private String fieldInfo;

    /**
     * 驗證端模組 url
     */
//    @Lob
    @Column(name = "verifier_service_url")
    private String verifierServiceUrl;

    /**
     * 組織業務系統 url
     */
//    @Lob
    @Column(name = "callback_url")
    private String callbackUrl;

    /**
     * 模組加密
     */
    @Column(name = "is_encrypt_enabled")
    private Boolean isEncryptEnabled;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public VPItem id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSerialNo() {
        return this.serialNo;
    }

    public VPItem serialNo(String serialNo) {
        this.setSerialNo(serialNo);
        return this;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getName() {
        return this.name;
    }

    public VPItem name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCrUser() {
        return this.crUser;
    }

    public VPItem crUser(Long crUser) {
        this.setCrUser(crUser);
        return this;
    }

    public void setCrUser(Long crUser) {
        this.crUser = crUser;
    }

    public Instant getCrDatetime() {
        return this.crDatetime;
    }

    public VPItem crDatetime(Instant crDatetime) {
        this.setCrDatetime(crDatetime);
        return this;
    }

    public void setCrDatetime(Instant crDatetime) {
        this.crDatetime = crDatetime;
    }

    public String getBusinessId() {
        return this.businessId;
    }

    public VPItem businessId(String businessId) {
        this.setBusinessId(businessId);
        return this;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getPresentationDefinition() {
        return this.presentationDefinition;
    }

    public VPItem presentationDefinition(String presentationDefinition) {
        this.setPresentationDefinition(presentationDefinition);
        return this;
    }

    public void setPresentationDefinition(String presentationDefinition) {
        this.presentationDefinition = presentationDefinition;
    }

    public String getPurpose() {
        return this.purpose;
    }

    public VPItem purpose(String purpose) {
        this.setPurpose(purpose);
        return this;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getTerms() {
        return this.terms;
    }

    public VPItem terms(String terms) {
        this.setTerms(terms);
        return this;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

    public Long getUpUser() {
        return this.upUser;
    }

    public VPItem upUser(Long upUser) {
        this.setUpUser(upUser);
        return this;
    }

    public void setUpUser(Long upUser) {
        this.upUser = upUser;
    }

    public Instant getUpDatetime() {
        return this.upDatetime;
    }

    public VPItem upDatetime(Instant upDatetime) {
        this.setUpDatetime(upDatetime);
        return this;
    }

    public void setUpDatetime(Instant upDatetime) {
        this.upDatetime = upDatetime;
    }

    public String getGroupInfo() {
        return this.groupInfo;
    }

    public VPItem groupInfo(String groupInfo) {
        this.setGroupInfo(groupInfo);
        return this;
    }

    public void setGroupInfo(String groupInfo) {
        this.groupInfo = groupInfo;
	}

    public Boolean getIsStatic() {
        return this.isStatic;
    }

    public VPItem isStatic(Boolean isStatic) {
        this.setIsStatic(isStatic);
        return this;
    }

    public void setIsStatic(Boolean isStatic) {
        this.isStatic = isStatic;
    }

    public Boolean getIsOffline() {
        return this.isOffline;
    }

    public VPItem isOffline(Boolean isOffline) {
        this.setIsOffline(isOffline);
        return this;
    }

    public void setIsOffline(Boolean isOffline) {
        this.isOffline = isOffline;
    }

    public String getTag() {
        return this.tag;
    }

    public VPItem tag(String tag) {
        this.setTag(tag);
        return this;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getFieldInfo() {
        return this.fieldInfo;
    }

    public VPItem fieldInfo(String fieldInfo) {
        this.setFieldInfo(fieldInfo);
        return this;
    }

    public void setFieldInfo(String fieldInfo) {
        this.fieldInfo = fieldInfo;
    }

    public String getVerifierServiceUrl() {
        return this.verifierServiceUrl;
    }

    public VPItem verifierServiceUrl(String verifierServiceUrl) {
        this.setVerifierServiceUrl(verifierServiceUrl);
        return this;
    }

    public void setVerifierServiceUrl(String verifierServiceUrl) {
        this.verifierServiceUrl = verifierServiceUrl;
    }

    public String getCallbackUrl() {
        return this.callbackUrl;
    }

    public VPItem callbackUrl(String callbackUrl) {
        this.setCallbackUrl(callbackUrl);
        return this;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public Boolean getIsEncryptEnabled() {
        return this.isEncryptEnabled;
    }

    public VPItem isEncryptEnabled(Boolean isEncryptEnabled) {
        this.setIsEncryptEnabled(isEncryptEnabled);
        return this;
    }

    public void setIsEncryptEnabled(Boolean isEncryptEnabled) {
        this.isEncryptEnabled = isEncryptEnabled;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VPItem)) {
            return false;
        }
        return getId() != null && getId().equals(((VPItem) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VPItem{" +
            "id=" + getId() +
            ", serialNo='" + getSerialNo() + "'" +
            ", name='" + getName() + "'" +
            ", crUser=" + getCrUser() +
            ", crDatetime='" + getCrDatetime() + "'" +
            ", businessId='" + getBusinessId() + "'" +
            ", presentationDefinition='" + getPresentationDefinition() + "'" +
            ", purpose='" + getPurpose() + "'" +
            ", terms='" + getTerms() + "'" +
            ", upUser=" + getUpUser() +
            ", upDatetime='" + getUpDatetime() + "'" +
            ", groupInfo='" + getGroupInfo() + "'" +
            ", isStatic='" + getIsStatic() + "'" +
            ", isOffline='" + getIsOffline() + "'" +
            ", tag='" + getTag() + "'" +
            ", fieldInfo='" + getFieldInfo() + "'" +
            ", verifierServiceUrl='" + getVerifierServiceUrl() + "'" +
            ", callbackUrl='" + getCallbackUrl() + "'" +
            ", isEncryptEnabled='" + getIsEncryptEnabled() + "'" +
            "}";
    }
}
