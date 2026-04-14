package gov.moda.dw.manager.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link gov.moda.dw.manager.domain.OrderProdItemDetail} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrderProdItemDetailDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 255)
    @Schema(description = "訂單編號", requiredMode = Schema.RequiredMode.REQUIRED)
    private String orderCaseId;

    @NotNull
    @Size(max = 255)
    @Schema(description = "購買人身分證號", requiredMode = Schema.RequiredMode.REQUIRED)
    private String buyerIdNo;

    @NotNull
    @Size(max = 255)
    @Schema(description = "驗證結果", requiredMode = Schema.RequiredMode.REQUIRED)
    private String verifyResult;

    @NotNull
    @Size(max = 255)
    @Schema(description = "支付類別", requiredMode = Schema.RequiredMode.REQUIRED)
    private String paymentCategory;

    @NotNull
    @Size(max = 255)
    @Schema(description = "購買/申請時間", requiredMode = Schema.RequiredMode.REQUIRED)
    private String purchaseTime;

    @NotNull
    @Size(max = 255)
    @Schema(description = "購買狀態", requiredMode = Schema.RequiredMode.REQUIRED)
    private String purchaseStatus;

    @Size(max = 500)
    @Schema(description = "備註")
    private String remark;

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

    public String getBuyerIdNo() {
        return buyerIdNo;
    }

    public void setBuyerIdNo(String buyerIdNo) {
        this.buyerIdNo = buyerIdNo;
    }

    public String getVerifyResult() {
        return verifyResult;
    }

    public void setVerifyResult(String verifyResult) {
        this.verifyResult = verifyResult;
    }

    public String getPaymentCategory() {
        return paymentCategory;
    }

    public void setPaymentCategory(String paymentCategory) {
        this.paymentCategory = paymentCategory;
    }

    public String getPurchaseTime() {
        return purchaseTime;
    }

    public void setPurchaseTime(String purchaseTime) {
        this.purchaseTime = purchaseTime;
    }

    public String getPurchaseStatus() {
        return purchaseStatus;
    }

    public void setPurchaseStatus(String purchaseStatus) {
        this.purchaseStatus = purchaseStatus;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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
        if (!(o instanceof OrderProdItemDetailDTO)) {
            return false;
        }

        OrderProdItemDetailDTO orderProdItemDetailDTO = (OrderProdItemDetailDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, orderProdItemDetailDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderProdItemDetailDTO{" +
            "id=" + getId() +
            ", orderCaseId='" + getOrderCaseId() + "'" +
            ", buyerIdNo='" + getBuyerIdNo() + "'" +
            ", verifyResult='" + getVerifyResult() + "'" +
            ", paymentCategory='" + getPaymentCategory() + "'" +
            ", purchaseTime='" + getPurchaseTime() + "'" +
            ", purchaseStatus='" + getPurchaseStatus() + "'" +
            ", remark='" + getRemark() + "'" +
            ", createTime='" + getCreateTime() + "'" +
            ", modifyTime='" + getModifyTime() + "'" +
            "}";
    }
}
