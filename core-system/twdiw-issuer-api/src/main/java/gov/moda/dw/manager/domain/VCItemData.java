package gov.moda.dw.manager.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * VCItemData VC個人內容
 */
@Entity
@Table(name = "vc_item_data")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VCItemData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * 內容
     */
    @Column(name = "content")
    private String content;

    /**
     * 內容
     */
    @Column(name = "pure_content")
    private String pureContent;

    /**
     * 建立者
     */
    @NotNull
    @Column(name = "cr_user", nullable = false)
    private Long crUser;

    /**
     * 建立時間
     */
    @Column(name = "cr_datetime")
    private Instant crDatetime;

    /**
     * 狀態
     */
    @Column(name = "valid")
    private Integer valid;

    /**
     * 清除資料的排程ID
     */
    @Column(name = "clear_schedule_id")
    private Long clearScheduleId;

    /**
     * 清除資料的排程時間
     */
    @Column(name = "clear_schedule_datetime")
    private Instant clearScheduleDatetime;

    /**
     * VC 憑證編號
     */
    @Size(max = 100)
    @Column(name = "vc_cid", length = 100)
    private String vcCid;

    /**
     * 交易序號
     */
    @Size(max = 50)
    @Column(name = "transaction_id", length = 50)
    private String transactionId;

    /**
     * 單位
     */
    @Size(max = 255)
    @Column(name = "business_id", length = 255)
    private String businessId;

    /**
     * VC名稱
     */
    @Size(max = 50)
    @Column(name = "vc_item_name", length = 50)
    private String vcItemName;

    /**
     * 二維碼
     */
    @Column(name = "qr_code")
    private String qrCode;

    /**
     * 過期時間
     */
    @Column(name = "expired")
    private Instant expired;

    /**
     * 撤銷失敗訊息
     */
    @Size(max = 2048)
    @Column(name = "schedule_revoke_message", length = 2048)
    private String scheduleRevokeMessage;

    /**
     * 標記實際資料對應至VC卡片的資料標註
     */
    @Size(max = 255)
    @Column(name = "data_tag", length = 255)
    private String dataTag;

    @ManyToOne(fetch = FetchType.LAZY)
    private VCItem vcItem;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public VCItemData id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return this.content;
    }

    public VCItemData content(String content) {
        this.setContent(content);
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getCrUser() {
        return this.crUser;
    }

    public VCItemData crUser(Long crUser) {
        this.setCrUser(crUser);
        return this;
    }

    public void setCrUser(Long crUser) {
        this.crUser = crUser;
    }

    public Instant getCrDatetime() {
        return this.crDatetime;
    }

    public VCItemData crDatetime(Instant crDatetime) {
        this.setCrDatetime(crDatetime);
        return this;
    }

    public void setCrDatetime(Instant crDatetime) {
        this.crDatetime = crDatetime;
    }

    public Integer getValid() {
        return this.valid;
    }

    public VCItemData valid(Integer valid) {
        this.setValid(valid);
        return this;
    }

    public void setValid(Integer valid) {
        this.valid = valid;
    }

    public Long getClearScheduleId() {
        return this.clearScheduleId;
    }

    public VCItemData clearScheduleId(Long clearScheduleId) {
        this.setClearScheduleId(clearScheduleId);
        return this;
    }

    public void setClearScheduleId(Long clearScheduleId) {
        this.clearScheduleId = clearScheduleId;
    }

    public Instant getClearScheduleDatetime() {
        return this.clearScheduleDatetime;
    }

    public VCItemData clearScheduleDatetime(Instant clearScheduleDatetime) {
        this.setClearScheduleDatetime(clearScheduleDatetime);
        return this;
    }

    public void setClearScheduleDatetime(Instant clearScheduleDatetime) {
        this.clearScheduleDatetime = clearScheduleDatetime;
    }

    public String getVcCid() {
        return this.vcCid;
    }

    public VCItemData vcCid(String vcCid) {
        this.setVcCid(vcCid);
        return this;
    }

    public void setVcCid(String vcCid) {
        this.vcCid = vcCid;
    }

    public String getTransactionId() {
        return this.transactionId;
    }

    public VCItemData transactionId(String transactionId) {
        this.setTransactionId(transactionId);
        return this;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getBusinessId() {
        return this.businessId;
    }

    public VCItemData businessId(String businessId) {
        this.setBusinessId(businessId);
        return this;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getVcItemName() {
        return this.vcItemName;
    }

    public VCItemData vcItemName(String vcItemName) {
        this.setVcItemName(vcItemName);
        return this;
    }

    public void setVcItemName(String vcItemName) {
        this.vcItemName = vcItemName;
    }

    public String getQrCode() {
        return this.qrCode;
    }

    public VCItemData qrCode(String qrCode) {
        this.setQrCode(qrCode);
        return this;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public Instant getExpired() {
        return this.expired;
    }

    public VCItemData expired(Instant expired) {
        this.setExpired(expired);
        return this;
    }

    public void setExpired(Instant expired) {
        this.expired = expired;
    }

    public String getScheduleRevokeMessage() {
        return this.scheduleRevokeMessage;
    }

    public VCItemData scheduleRevokeMessage(String scheduleRevokeMessage) {
        this.setScheduleRevokeMessage(scheduleRevokeMessage);
        return this;
    }

    public void setScheduleRevokeMessage(String scheduleRevokeMessage) {
        this.scheduleRevokeMessage = scheduleRevokeMessage;
    }

    public String getDataTag() {
        return this.dataTag;
    }

    public VCItemData dataTag(String dataTag) {
        this.setDataTag(dataTag);
        return this;
    }

    public void setDataTag(String dataTag) {
        this.dataTag = dataTag;
    }

    public VCItem getVcItem() {
        return this.vcItem;
    }

    public void setVcItem(VCItem vCItem) {
        this.vcItem = vCItem;
    }

    public VCItemData vcItem(VCItem vCItem) {
        this.setVcItem(vCItem);
        return this;
    }

    public String getPureContent() {
        return pureContent;
    }

    public void setPureContent(String pureContent) {
        this.pureContent = pureContent;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VCItemData)) {
            return false;
        }
        return getId() != null && getId().equals(((VCItemData) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VCItemData{" +
            "id=" + getId() +
            ", content='" + getContent() + "'" +
            ", crUser=" + getCrUser() +
            ", crDatetime='" + getCrDatetime() + "'" +
            ", valid=" + getValid() +
            ", clearScheduleId=" + getClearScheduleId() +
            ", clearScheduleDatetime='" + getClearScheduleDatetime() + "'" +
            ", vcCid='" + getVcCid() + "'" +
            ", transactionId='" + getTransactionId() + "'" +
            ", businessId='" + getBusinessId() + "'" +
            ", vcItemName='" + getVcItemName() + "'" +
            ", qrCode='" + getQrCode() + "'" +
            ", expired='" + getExpired() + "'" +
            ", scheduleRevokeMessage='" + getScheduleRevokeMessage() + "'" +
            "}";
    }
}
