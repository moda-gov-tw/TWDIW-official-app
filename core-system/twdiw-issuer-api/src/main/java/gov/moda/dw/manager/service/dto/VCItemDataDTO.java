package gov.moda.dw.manager.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link gov.moda.dw.manager.domain.VCItemData} entity.
 */
@Schema(description = "VCItemData VC個人內容")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VCItemDataDTO implements Serializable {

    private Long id;

    @Schema(description = "內容")
    @Lob
    private String content;

    @Schema(description = "內容")
    @Lob
    private String pureContent;

    @Schema(description = "建立者", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long crUser;

    @Schema(description = "建立時間")
    private Instant crDatetime;

    @Schema(description = "VC產製時間")
    private Instant issuanceDate;

    @Schema(description = "狀態")
    private Integer valid;

    @Schema(description = "清除資料的排程ID")
    private Long clearScheduleId;

    @Schema(description = "清除資料的排程時間")
    private Instant clearScheduleDatetime;

    @Size(max = 100)
    @Schema(description = "VC 憑證編號")
    private String vcCid;

    @Size(max = 50)
    @Schema(description = "交易序號")
    private String transactionId;

    @Size(max = 255)
    @Schema(description = "單位")
    private String businessId;

    @Size(max = 50)
    @Schema(description = "VC名稱")
    private String vcItemName;

    @Schema(description = "二維碼")
    @Lob
    private String qrCode;

    @Schema(description = "DeepLink")
    @Lob
    private String deepLink;

    @Schema(description = "過期時間")
    private Instant expired;

    @Size(max = 2048)
    @Schema(description = "撤銷失敗訊息")
    private String scheduleRevokeMessage;

    @Size(max = 255)
    @Schema(description = "標記實際資料對應至VC卡片的資料標註")
    private String dataTag;

    private VCItemDTO vcItem;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getCrUser() {
        return crUser;
    }

    public void setCrUser(Long crUser) {
        this.crUser = crUser;
    }

    public Instant getCrDatetime() {
        return crDatetime;
    }

    public void setCrDatetime(Instant crDatetime) {
        this.crDatetime = crDatetime;
    }

    public String getPureContent() {
        return pureContent;
    }

    public void setPureContent(String pureContent) {
        this.pureContent = pureContent;
    }

    public Instant getIssuanceDate() {
        return issuanceDate;
    }

    public void setIssuanceDate(Instant issuanceDate) {
        this.issuanceDate = issuanceDate;
    }

    public Integer getValid() {
        return valid;
    }

    public void setValid(Integer valid) {
        this.valid = valid;
    }

    public Long getClearScheduleId() {
        return clearScheduleId;
    }

    public void setClearScheduleId(Long clearScheduleId) {
        this.clearScheduleId = clearScheduleId;
    }

    public Instant getClearScheduleDatetime() {
        return clearScheduleDatetime;
    }

    public void setClearScheduleDatetime(Instant clearScheduleDatetime) {
        this.clearScheduleDatetime = clearScheduleDatetime;
    }

    public String getVcCid() {
        return vcCid;
    }

    public void setVcCid(String vcCid) {
        this.vcCid = vcCid;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getVcItemName() {
        return vcItemName;
    }

    public void setVcItemName(String vcItemName) {
        this.vcItemName = vcItemName;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getDeepLink() {
        return deepLink;
    }

    public void setDeepLink(String deepLink) {
        this.deepLink = deepLink;
    }

    public Instant getExpired() {
        return expired;
    }

    public void setExpired(Instant expired) {
        this.expired = expired;
    }

    public String getScheduleRevokeMessage() {
        return scheduleRevokeMessage;
    }

    public void setScheduleRevokeMessage(String scheduleRevokeMessage) {
        this.scheduleRevokeMessage = scheduleRevokeMessage;
    }

    public String getDataTag() {
        return dataTag;
    }

    public void setDataTag(String dataTag) {
        this.dataTag = dataTag;
    }

    public VCItemDTO getVcItem() {
        return vcItem;
    }

    public void setVcItem(VCItemDTO vcItem) {
        this.vcItem = vcItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VCItemDataDTO that = (VCItemDataDTO) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(content, that.content) &&
            Objects.equals(pureContent, that.pureContent) &&
            Objects.equals(crUser, that.crUser) &&
            Objects.equals(crDatetime, that.crDatetime) &&
            Objects.equals(issuanceDate, that.issuanceDate) &&
            Objects.equals(valid, that.valid) &&
            Objects.equals(clearScheduleId, that.clearScheduleId) &&
            Objects.equals(clearScheduleDatetime, that.clearScheduleDatetime) &&
            Objects.equals(vcCid, that.vcCid) &&
            Objects.equals(transactionId, that.transactionId) &&
            Objects.equals(businessId, that.businessId) &&
            Objects.equals(vcItemName, that.vcItemName) &&
            Objects.equals(qrCode, that.qrCode) &&
            Objects.equals(deepLink, that.deepLink) &&
            Objects.equals(expired, that.expired) &&
            Objects.equals(scheduleRevokeMessage, that.scheduleRevokeMessage) &&
            Objects.equals(dataTag, that.dataTag) &&
            Objects.equals(vcItem, that.vcItem)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            content,
            pureContent,
            crUser,
            crDatetime,
            issuanceDate,
            valid,
            clearScheduleId,
            clearScheduleDatetime,
            vcCid,
            transactionId,
            businessId,
            vcItemName,
            qrCode,
            deepLink,
            expired,
            scheduleRevokeMessage,
            dataTag,
            vcItem
        );
    }

    @Override
    public String toString() {
        return (
            "VCItemDataDTO{" +
            "id=" +
            id +
            ", content='" +
            content +
            '\'' +
            ", pureContent='" +
            pureContent +
            '\'' +
            ", crUser=" +
            crUser +
            ", crDatetime=" +
            crDatetime +
            ", issuanceDate=" +
            issuanceDate +
            ", valid=" +
            valid +
            ", clearScheduleId=" +
            clearScheduleId +
            ", clearScheduleDatetime=" +
            clearScheduleDatetime +
            ", vcCid='" +
            vcCid +
            '\'' +
            ", transactionId='" +
            transactionId +
            '\'' +
            ", businessId='" +
            businessId +
            '\'' +
            ", vcItemName='" +
            vcItemName +
            '\'' +
            ", qrCode='" +
            qrCode +
            '\'' +
            ", deepLink='" +
            deepLink +
            '\'' +
            ", expired=" +
            expired +
            ", scheduleRevokeMessage='" +
            scheduleRevokeMessage +
            '\'' +
            ", dataTag='" +
            dataTag +
            '\'' +
            ", vcItem=" +
            vcItem +
            '}'
        );
    }
}
