package gov.moda.dw.manager.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import gov.moda.dw.manager.annotation.RequiredField;
import gov.moda.dw.manager.annotation.ToMapDTO;

/**
 * A DTO for the {@link gov.moda.dw.manager.domain.OrderType} entity.
 */
@ToMapDTO
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrderTypeDTO implements Serializable {

    @RequiredField
    private Long id;

    @Schema(description = "訂單所屬平台")
    private String orderPlatform;

    @Schema(description = "訂單類型群組")
    private String orderTypeGup;

    @Schema(description = "訂單類型")
    private String orderType;

    @Schema(description = "訂單類型中文名稱")
    private String orderTypeName;

    @Schema(description = "使用類型")
    private String userType;

    @Schema(description = "繳費平台")
    private String payPlatform;

    @Schema(description = "訂單業務別碼")
    private String productBsCode;

    @Schema(description = "結帳方式")
    private String checkoutFunc;

    @Schema(description = "開立發票或收據類型")
    private String invOrRecType;

    @Schema(description = "狀態")
    private String status;

    @Schema(description = "最後修改日期")
    private Instant modifyTime;

    @Schema(description = "建立時間")
    private Instant createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderPlatform() {
        return orderPlatform;
    }

    public void setOrderPlatform(String orderPlatform) {
        this.orderPlatform = orderPlatform;
    }

    public String getOrderTypeGup() {
        return orderTypeGup;
    }

    public void setOrderTypeGup(String orderTypeGup) {
        this.orderTypeGup = orderTypeGup;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getOrderTypeName() {
        return orderTypeName;
    }

    public void setOrderTypeName(String orderTypeName) {
        this.orderTypeName = orderTypeName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getPayPlatform() {
        return payPlatform;
    }

    public void setPayPlatform(String payPlatform) {
        this.payPlatform = payPlatform;
    }

    public String getProductBsCode() {
        return productBsCode;
    }

    public void setProductBsCode(String productBsCode) {
        this.productBsCode = productBsCode;
    }

    public String getCheckoutFunc() {
        return checkoutFunc;
    }

    public void setCheckoutFunc(String checkoutFunc) {
        this.checkoutFunc = checkoutFunc;
    }

    public String getInvOrRecType() {
        return invOrRecType;
    }

    public void setInvOrRecType(String invOrRecType) {
        this.invOrRecType = invOrRecType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Instant modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Instant getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Instant createTime) {
        this.createTime = createTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderTypeDTO)) {
            return false;
        }

        OrderTypeDTO orderTypeDTO = (OrderTypeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, orderTypeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderTypeDTO{" +
            "id=" + getId() +
            ", orderPlatform='" + getOrderPlatform() + "'" +
            ", orderTypeGup='" + getOrderTypeGup() + "'" +
            ", orderType='" + getOrderType() + "'" +
            ", orderTypeName='" + getOrderTypeName() + "'" +
            ", userType='" + getUserType() + "'" +
            ", payPlatform='" + getPayPlatform() + "'" +
            ", productBsCode='" + getProductBsCode() + "'" +
            ", checkoutFunc='" + getCheckoutFunc() + "'" +
            ", invOrRecType='" + getInvOrRecType() + "'" +
            ", status='" + getStatus() + "'" +
            ", modifyTime='" + getModifyTime() + "'" +
            ", createTime='" + getCreateTime() + "'" +
            "}";
    }
}
