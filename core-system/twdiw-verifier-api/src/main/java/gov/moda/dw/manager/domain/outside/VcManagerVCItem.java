package gov.moda.dw.manager.domain.outside;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * VCItem VC列表
 */
@Entity
@Table(name = "vc_item",schema = "vc_manager")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VcManagerVCItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * VC編號
     */
    @NotNull
    @Size(max = 50)
    @Column(name = "serial_no", length = 50, nullable = false)
    private String serialNo;

    /**
     * VC名稱
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
     * 類別Id
     */
    @Column(name = "category_id")
    private Long categoryId;

    /**
     * 單位
     */
    @NotNull
    @Size(max = 255)
    @Column(name = "business_id", length = 255, nullable = false)
    private String businessId;

    /**
     * 上鏈 schema id
     */
    @Size(max = 255)
    @Column(name = "schema_id", length = 255)
    private String schemaId;

    /**
     * 上鏈 schema
     */
    @Column(name = "schema")
    //    @Column(name = "schema")
    private String schema;

    /**
     * 上鏈 txhash
     */
    @Column(name = "tx_hash")
    private String txHash;

    /**
     * 上鏈 metadata
     */
    @Column(name = "metadata")
    private String metadata;

    /**
     * 過期時間單位類型
     */
    @NotNull
    @Size(max = 10)
    @Column(name = "unit_type_expire", length = 10, nullable = false)
    private String unitTypeExpire;

    /**
     * 過期時間單位長度
     */
    @NotNull
    @Column(name = "length_expire", nullable = false)
    private Integer lengthExpire;


    /**
     * 是否公開
     */
    @NotNull
    @Column(name = "expose", nullable = true)
    private Boolean expose;



    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public VcManagerVCItem id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSerialNo() {
        return this.serialNo;
    }

    public VcManagerVCItem serialNo(String serialNo) {
        this.setSerialNo(serialNo);
        return this;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getName() {
        return this.name;
    }

    public VcManagerVCItem name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCrUser() {
        return this.crUser;
    }

    public VcManagerVCItem crUser(Long crUser) {
        this.setCrUser(crUser);
        return this;
    }

    public void setCrUser(Long crUser) {
        this.crUser = crUser;
    }

    public Instant getCrDatetime() {
        return this.crDatetime;
    }

    public VcManagerVCItem crDatetime(Instant crDatetime) {
        this.setCrDatetime(crDatetime);
        return this;
    }

    public void setCrDatetime(Instant crDatetime) {
        this.crDatetime = crDatetime;
    }

    public Long getCategoryId() {
        return this.categoryId;
    }

    public VcManagerVCItem categoryId(Long categoryId) {
        this.setCategoryId(categoryId);
        return this;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getBusinessId() {
        return this.businessId;
    }

    public VcManagerVCItem businessId(String businessId) {
        this.setBusinessId(businessId);
        return this;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getSchemaId() {
        return this.schemaId;
    }

    public VcManagerVCItem schemaId(String schemaId) {
        this.setSchemaId(schemaId);
        return this;
    }

    public void setSchemaId(String schemaId) {
        this.schemaId = schemaId;
    }

    public String getSchema() {
        return this.schema;
    }

    public VcManagerVCItem schema(String schema) {
        this.setSchema(schema);
        return this;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getTxHash() {
        return this.txHash;
    }

    public VcManagerVCItem txHash(String txHash) {
        this.setTxHash(txHash);
        return this;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public String getMetadata() {
        return this.metadata;
    }

    public VcManagerVCItem metadata(String metadata) {
        this.setMetadata(metadata);
        return this;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getUnitTypeExpire() {
        return this.unitTypeExpire;
    }

    public VcManagerVCItem unitTypeExpire(String unitTypeExpire) {
        this.setUnitTypeExpire(unitTypeExpire);
        return this;
    }

    public void setUnitTypeExpire(String unitTypeExpire) {
        this.unitTypeExpire = unitTypeExpire;
    }

    public Integer getLengthExpire() {
        return this.lengthExpire;
    }

    public VcManagerVCItem lengthExpire(Integer lengthExpire) {
        this.setLengthExpire(lengthExpire);
        return this;
    }

    public void setLengthExpire(Integer lengthExpire) {
        this.lengthExpire = lengthExpire;
    }

    public Boolean getExpose() {
        return expose;
    }

    public void setExpose(Boolean expose) {
        this.expose = expose;
    }

// jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VcManagerVCItem)) {
            return false;
        }
        return getId() != null && getId().equals(((VcManagerVCItem) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VCItem{" +
            "id=" + getId() +
            ", serialNo='" + getSerialNo() + "'" +
            ", name='" + getName() + "'" +
            ", crUser=" + getCrUser() +
            ", crDatetime='" + getCrDatetime() + "'" +
            ", categoryId=" + getCategoryId() +
            ", businessId='" + getBusinessId() + "'" +
            ", schemaId='" + getSchemaId() + "'" +
            ", schema='" + getSchema() + "'" +
            ", txHash='" + getTxHash() + "'" +
            ", metadata='" + getMetadata() + "'" +
            ", unitTypeExpire='" + getUnitTypeExpire() + "'" +
            ", lengthExpire=" + getLengthExpire() +
            ", expose=" + getExpose() +
            "}";
    }
}
