package gov.moda.dw.manager.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * VCDataStatusLog VC 資料狀態紀錄
 */
@Entity
@Table(name = "vc_data_status_log")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VCDataStatusLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * VC 憑證編號
     */
    @NotNull
    @Size(max = 100)
    @Column(name = "vc_cid", length = 100, nullable = false)
    private String vcCid;

    /**
     * vc_Item_Id
     */
    @NotNull
    @Column(name = "vc_item_id", nullable = false)
    private Long vcItemId;

    /**
     * 交易序號
     */
    @NotNull
    @Size(max = 50)
    @Column(name = "transaction_id", length = 50, nullable = false)
    private String transactionId;

    /**
     * 狀態 [0: ACTIVE(有效)、1: SUSPENDED(停用)、2: REVOKED(撤銷)、301: INACTIVE(沒掃 QR Code)]
     */
    @NotNull
    @Column(name = "valid", nullable = false)
    private Integer valid;

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
     * 最後更新時間
     */
    @Column(name = "last_update_time")
    private Instant lastUpdateTime;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public VCDataStatusLog id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVcCid() {
        return this.vcCid;
    }

    public VCDataStatusLog vcCid(String vcCid) {
        this.setVcCid(vcCid);
        return this;
    }

    public void setVcCid(String vcCid) {
        this.vcCid = vcCid;
    }

    public Long getVcItemId() {
        return this.vcItemId;
    }

    public VCDataStatusLog vcItemId(Long vcItemId) {
        this.setVcItemId(vcItemId);
        return this;
    }

    public void setVcItemId(Long vcItemId) {
        this.vcItemId = vcItemId;
    }

    public String getTransactionId() {
        return this.transactionId;
    }

    public VCDataStatusLog transactionId(String transactionId) {
        this.setTransactionId(transactionId);
        return this;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getValid() {
        return this.valid;
    }

    public VCDataStatusLog valid(Integer valid) {
        this.setValid(valid);
        return this;
    }

    public void setValid(Integer valid) {
        this.valid = valid;
    }

    public Long getCrUser() {
        return this.crUser;
    }

    public VCDataStatusLog crUser(Long crUser) {
        this.setCrUser(crUser);
        return this;
    }

    public void setCrUser(Long crUser) {
        this.crUser = crUser;
    }

    public Instant getCrDatetime() {
        return this.crDatetime;
    }

    public VCDataStatusLog crDatetime(Instant crDatetime) {
        this.setCrDatetime(crDatetime);
        return this;
    }

    public void setCrDatetime(Instant crDatetime) {
        this.crDatetime = crDatetime;
    }

    public Instant getLastUpdateTime() {
        return this.lastUpdateTime;
    }

    public VCDataStatusLog lastUpdateTime(Instant lastUpdateTime) {
        this.setLastUpdateTime(lastUpdateTime);
        return this;
    }

    public void setLastUpdateTime(Instant lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and
    // setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VCDataStatusLog)) {
            return false;
        }
        return getId() != null && getId().equals(((VCDataStatusLog) o).getId());
    }

    @Override
    public int hashCode() {
        // see
        // https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VCDataStatusLog{" + "id=" + getId() + ", vcCid='" + getVcCid() + "'" + ", vcItemId=" + getVcItemId()
                + ", transactionId='" + getTransactionId() + "'" + ", valid=" + getValid() + ", crUser=" + getCrUser()
                + ", crDatetime='" + getCrDatetime() + "'" + ", lastUpdateTime='" + getLastUpdateTime() + "'" + "}";
    }
}
