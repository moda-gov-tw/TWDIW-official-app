package gov.moda.dw.manager.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link gov.moda.dw.manager.domain.PaymentInfo} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PaymentInfoDTO implements Serializable {

    private Long id;

    @Size(max = 255)
    @Schema(description = "訂單編號")
    private String orderCaseId;

    @Size(max = 255)
    @Schema(description = "繳費平台")
    private String orderType;

    @Schema(description = "交易金額")
    private Long amount;

    @Size(max = 10)
    @Schema(description = "業務別碼")
    private String productBsCode;

    @Size(max = 255)
    @Schema(description = "台企銀-虛擬帳號")
    private String virtualAccount;

    @Size(max = 50)
    @Schema(description = "台企銀-繳費起時")
    private String payStartDate;

    @Size(max = 50)
    @Schema(description = "台企銀-繳費區(訖)")
    private String payEndDate;

    @Schema(description = "交易時間")
    private Instant tranDate;

    @Size(max = 100)
    @Schema(description = "繳費狀態")
    private String paymentStatus;

    @Schema(description = "建立時間")
    private Instant created;

    @Size(max = 255)
    @Schema(description = "備註")
    private String remark;

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

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getProductBsCode() {
        return productBsCode;
    }

    public void setProductBsCode(String productBsCode) {
        this.productBsCode = productBsCode;
    }

    public String getVirtualAccount() {
        return virtualAccount;
    }

    public void setVirtualAccount(String virtualAccount) {
        this.virtualAccount = virtualAccount;
    }

    public String getPayStartDate() {
        return payStartDate;
    }

    public void setPayStartDate(String payStartDate) {
        this.payStartDate = payStartDate;
    }

    public String getPayEndDate() {
        return payEndDate;
    }

    public void setPayEndDate(String payEndDate) {
        this.payEndDate = payEndDate;
    }

    public Instant getTranDate() {
        return tranDate;
    }

    public void setTranDate(Instant tranDate) {
        this.tranDate = tranDate;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PaymentInfoDTO)) {
            return false;
        }

        PaymentInfoDTO paymentInfoDTO = (PaymentInfoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, paymentInfoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PaymentInfoDTO{" +
            "id=" + getId() +
            ", orderCaseId='" + getOrderCaseId() + "'" +
            ", orderType='" + getOrderType() + "'" +
            ", amount=" + getAmount() +
            ", productBsCode='" + getProductBsCode() + "'" +
            ", virtualAccount='" + getVirtualAccount() + "'" +
            ", payStartDate='" + getPayStartDate() + "'" +
            ", payEndDate='" + getPayEndDate() + "'" +
            ", tranDate='" + getTranDate() + "'" +
            ", paymentStatus='" + getPaymentStatus() + "'" +
            ", created='" + getCreated() + "'" +
            ", remark='" + getRemark() + "'" +
            "}";
    }
}
