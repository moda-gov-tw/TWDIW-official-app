package gov.moda.dw.manager.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link gov.moda.dw.manager.domain.OrderForm} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrderFormDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 255)
    @Schema(description = "訂單編號", requiredMode = Schema.RequiredMode.REQUIRED)
    private String orderCaseId;

    @Size(max = 255)
    @Schema(description = "月結訂單總訂單編號")
    private String monthlyOrderCaseId;

    @Size(max = 255)
    @Schema(description = "訂單所屬平台")
    private String platformId;

    @Size(max = 255)
    @Schema(description = "訂單類型")
    private String orderType;

    @Size(max = 255)
    @Schema(description = "訂單結算所屬年月份")
    private String orderSettlementDate;

    @NotNull
    @Size(max = 255)
    @Schema(description = "正常或補正訂單", requiredMode = Schema.RequiredMode.REQUIRED)
    private String correctionType;

    @NotNull
    @Size(max = 255)
    @Schema(description = "訂單狀態", requiredMode = Schema.RequiredMode.REQUIRED)
    private String status;

    @Size(max = 255)
    @Schema(description = "客戶端-活動編號")
    private String customerActivityUUid;

    @Schema(description = "客戶端-活動日期")
    private Instant customerActivityTime;

    @Size(max = 255)
    @Schema(description = "客戶端-活動名稱")
    private String customerActivityName;

    @Size(max = 300)
    @Schema(description = "暫收款備註(最多100個中文字)")
    private String temporaryDepositsRemark;

    @NotNull
    @Schema(description = "創建時間", requiredMode = Schema.RequiredMode.REQUIRED)
    private Instant createTime;

    @Schema(description = "最後編輯時間")
    private Instant modifyTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderCaseId() {
        return orderCaseId;
    }

    public void setOrderCaseId(String orderCaseId) {
        this.orderCaseId = orderCaseId;
    }

    public String getMonthlyOrderCaseId() {
        return monthlyOrderCaseId;
    }

    public void setMonthlyOrderCaseId(String monthlyOrderCaseId) {
        this.monthlyOrderCaseId = monthlyOrderCaseId;
    }

    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getOrderSettlementDate() {
        return orderSettlementDate;
    }

    public void setOrderSettlementDate(String orderSettlementDate) {
        this.orderSettlementDate = orderSettlementDate;
    }

    public String getCorrectionType() {
        return correctionType;
    }

    public void setCorrectionType(String correctionType) {
        this.correctionType = correctionType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCustomerActivityUUid() {
        return customerActivityUUid;
    }

    public void setCustomerActivityUUid(String customerActivityUUid) {
        this.customerActivityUUid = customerActivityUUid;
    }

    public Instant getCustomerActivityTime() {
        return customerActivityTime;
    }

    public void setCustomerActivityTime(Instant customerActivityTime) {
        this.customerActivityTime = customerActivityTime;
    }

    public String getCustomerActivityName() {
        return customerActivityName;
    }

    public void setCustomerActivityName(String customerActivityName) {
        this.customerActivityName = customerActivityName;
    }

    public String getTemporaryDepositsRemark() {
        return temporaryDepositsRemark;
    }

    public void setTemporaryDepositsRemark(String temporaryDepositsRemark) {
        this.temporaryDepositsRemark = temporaryDepositsRemark;
    }

    public Instant getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Instant createTime) {
        this.createTime = createTime;
    }

    public Instant getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Instant modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderFormDTO)) {
            return false;
        }

        OrderFormDTO orderFormDTO = (OrderFormDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, orderFormDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderFormDTO{" +
            "id=" + getId() +
            ", orderCaseId='" + getOrderCaseId() + "'" +
            ", monthlyOrderCaseId='" + getMonthlyOrderCaseId() + "'" +
            ", platformId='" + getPlatformId() + "'" +
            ", orderType='" + getOrderType() + "'" +
            ", orderSettlementDate='" + getOrderSettlementDate() + "'" +
            ", correctionType='" + getCorrectionType() + "'" +
            ", status='" + getStatus() + "'" +
            ", customerActivityUUid='" + getCustomerActivityUUid() + "'" +
            ", customerActivityTime='" + getCustomerActivityTime() + "'" +
            ", customerActivityName='" + getCustomerActivityName() + "'" +
            ", temporaryDepositsRemark='" + getTemporaryDepositsRemark() + "'" +
            ", createTime='" + getCreateTime() + "'" +
            ", modifyTime='" + getModifyTime() + "'" +
            "}";
    }
}
