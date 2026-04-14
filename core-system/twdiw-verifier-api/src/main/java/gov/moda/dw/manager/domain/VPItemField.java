package gov.moda.dw.manager.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * VPItemField VP欄位
 */
@Entity
@Table(name = "vp_item_field")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VPItemField implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * VP id 外鍵
     */
    @NotNull
    @Column(name = "vp_item_id", nullable = false)
    private Long vpItemId;

    /**
     * VC類別描述
     */
    @Size(max = 50)
    @Column(name = "vc_category_description", length = 50)
    private String vcCategoryDescription;

    /**
     * VC名稱
     */
    @NotNull
    @Size(max = 50)
    @Column(name = "vc_name", length = 50, nullable = false)
    private String vcName;

    /**
     * 欄位類別(BASIC NORMAL NORMAL)
     */
    @Size(max = 20)
    @Column(name = "vc_item_field_type", length = 20)
    private String vcItemFieldType;

    /**
     * 欄位對外名稱(中/英)
     */
    @NotNull
    @Size(max = 50)
    @Column(name = "cname", length = 50, nullable = false)
    private String cname;

    /**
     * 欄位名稱(英)
     */
    @NotNull
    @Size(max = 50)
    @Column(name = "ename", length = 50, nullable = false)
    private String ename;

    /**
     * VC編號
     */
    @NotNull
    @Size(max = 50)
    @Column(name = "vc_serial_no", length = 50, nullable = false)
    private String vcSerialNo;

    /**
     * VC單位
     */
    @Size(max = 255)
    @Column(name = "vc_business_id")
    private String vcBusinessId;

    /**
     * VP單位
     */
    @Size(max = 255)
    @Column(name = "vp_business_id")
    private String vpBusinessId;

    /**
     * 是否必填
     */
    @Column(name = "is_required")
    private Boolean isRequired;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public VPItemField id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVpItemId() {
        return this.vpItemId;
    }

    public VPItemField vpItemId(Long vpItemId) {
        this.setVpItemId(vpItemId);
        return this;
    }

    public void setVpItemId(Long vpItemId) {
        this.vpItemId = vpItemId;
    }

    public String getVcCategoryDescription() {
        return this.vcCategoryDescription;
    }

    public VPItemField vcCategoryDescription(String vcCategoryDescription) {
        this.setVcCategoryDescription(vcCategoryDescription);
        return this;
    }

    public void setVcCategoryDescription(String vcCategoryDescription) {
        this.vcCategoryDescription = vcCategoryDescription;
    }

    public String getVcName() {
        return this.vcName;
    }

    public VPItemField vcName(String vcName) {
        this.setVcName(vcName);
        return this;
    }

    public void setVcName(String vcName) {
        this.vcName = vcName;
    }

    public String getVcItemFieldType() {
        return this.vcItemFieldType;
    }

    public VPItemField vcItemFieldType(String vcItemFieldType) {
        this.setVcItemFieldType(vcItemFieldType);
        return this;
    }

    public void setVcItemFieldType(String vcItemFieldType) {
        this.vcItemFieldType = vcItemFieldType;
    }

    public String getCname() {
        return this.cname;
    }

    public VPItemField cname(String cname) {
        this.setCname(cname);
        return this;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getEname() {
        return this.ename;
    }

    public VPItemField ename(String ename) {
        this.setEname(ename);
        return this;
    }

    public void setEname(String ename) {
        this.ename = ename;
    }

    public String getVcSerialNo() {
        return this.vcSerialNo;
    }

    public VPItemField vcSerialNo(String vcSerialNo) {
        this.setVcSerialNo(vcSerialNo);
        return this;
    }

    public void setVcSerialNo(String vcSerialNo) {
        this.vcSerialNo = vcSerialNo;
    }

    public String getVcBusinessId() {
        return this.vcBusinessId;
    }

    public VPItemField vcBusinessId(String vcBusinessId) {
        this.setVcBusinessId(vcBusinessId);
        return this;
    }

    public void setVcBusinessId(String vcBusinessId) {
        this.vcBusinessId = vcBusinessId;
    }

    public String getVpBusinessId() {
        return this.vpBusinessId;
    }

    public VPItemField vpBusinessId(String vpBusinessId) {
        this.setVpBusinessId(vpBusinessId);
        return this;
    }

    public void setVpBusinessId(String vpBusinessId) {
        this.vpBusinessId = vpBusinessId;
    }

    public Boolean getIsRequired() {
        return this.isRequired;
    }

    public VPItemField isRequired(Boolean isRequired) {
        this.setIsRequired(isRequired);
        return this;
    }

    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VPItemField)) {
            return false;
        }
        return getId() != null && getId().equals(((VPItemField) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VPItemField{" +
            "id=" + getId() +
            ", vpItemId=" + getVpItemId() +
            ", vcCategoryDescription='" + getVcCategoryDescription() + "'" +
            ", vcName='" + getVcName() + "'" +
            ", vcItemFieldType='" + getVcItemFieldType() + "'" +
            ", cname='" + getCname() + "'" +
            ", ename='" + getEname() + "'" +
            ", vcSerialNo='" + getVcSerialNo() + "'" +
            ", vcBusinessId='" + getVcBusinessId() + "'" +
            ", vpBusinessId='" + getVpBusinessId() + "'" +
            ", isRequired='" + getIsRequired() + "'" +
            "}";
    }
}
