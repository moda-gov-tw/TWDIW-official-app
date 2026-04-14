package gov.moda.dw.manager.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * VPVerifyResult VP驗證結果
 */
@Entity
@Table(name = "vp_verify_result")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VPVerifyResult implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * 交易序號
     */
    @NotNull
    @Size(max = 50)
    @Column(name = "transaction_id", length = 50, nullable = false)
    private String transactionId;

    /**
     * VC資料
     */
    @Column(name = "payload")
    private String payload;

    /**
     * VP id 外鍵
     */
    @Column(name = "vp_item_id")
    private Long vpItemId;

    /**
     * 建立時間
     */
    @Column(name = "cr_datetime")
    private Instant crDatetime;

    /**
     * 驗證時間
     */
    @Column(name = "verify_datetime")
    private Instant verifyDatetime;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public VPVerifyResult id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransactionId() {
        return this.transactionId;
    }

    public VPVerifyResult transactionId(String transactionId) {
        this.setTransactionId(transactionId);
        return this;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getPayload() {
        return this.payload;
    }

    public VPVerifyResult payload(String payload) {
        this.setPayload(payload);
        return this;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Long getVpItemId() {
        return this.vpItemId;
    }

    public VPVerifyResult vpItemId(Long vpItemId) {
        this.setVpItemId(vpItemId);
        return this;
    }

    public void setVpItemId(Long vpItemId) {
        this.vpItemId = vpItemId;
    }

    public Instant getCrDatetime() {
        return this.crDatetime;
    }

    public VPVerifyResult crDatetime(Instant crDatetime) {
        this.setCrDatetime(crDatetime);
        return this;
    }

    public void setCrDatetime(Instant crDatetime) {
        this.crDatetime = crDatetime;
    }

    public Instant getVerifyDatetime() {
        return this.verifyDatetime;
    }

    public VPVerifyResult verifyDatetime(Instant verifyDatetime) {
        this.setVerifyDatetime(verifyDatetime);
        return this;
    }

    public void setVerifyDatetime(Instant verifyDatetime) {
        this.verifyDatetime = verifyDatetime;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VPVerifyResult)) {
            return false;
        }
        return getId() != null && getId().equals(((VPVerifyResult) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VPVerifyResult{" +
            "id=" + getId() +
            ", transactionId='" + getTransactionId() + "'" +
            ", payload='" + getPayload() + "'" +
            ", vpItemId=" + getVpItemId() +
            ", crDatetime='" + getCrDatetime() + "'" +
            ", verifyDatetime='" + getVerifyDatetime() + "'" +
            "}";
    }
}
