package gov.moda.dw.manager.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link gov.moda.dw.manager.domain.OrderProdItem} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrderProdItemDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 255)
    @Schema(description = "訂單編號", requiredMode = Schema.RequiredMode.REQUIRED)
    private String orderCaseId;

    @Size(max = 255)
    @Schema(description = "月結訂單總訂單編號")
    private String monthlyOrderCaseId;

    @NotNull
    @Size(max = 255)
    @Schema(description = "商品名稱代碼", requiredMode = Schema.RequiredMode.REQUIRED)
    private String itemNameCode;

    @NotNull
    @Size(max = 255)
    @Schema(description = "商品單價", requiredMode = Schema.RequiredMode.REQUIRED)
    private String itemPrice;

    @NotNull
    @Size(max = 255)
    @Schema(description = "商品數量", requiredMode = Schema.RequiredMode.REQUIRED)
    private String itemDetailCount;

    @NotNull
    @Size(max = 255)
    @Schema(description = "商品總金額", requiredMode = Schema.RequiredMode.REQUIRED)
    private String itemTotlePrice;

    @NotNull
    @Size(max = 255)
    @Schema(description = "訂單商品狀態", requiredMode = Schema.RequiredMode.REQUIRED)
    private String prodStatus;

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

    public String getItemNameCode() {
        return itemNameCode;
    }

    public void setItemNameCode(String itemNameCode) {
        this.itemNameCode = itemNameCode;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemDetailCount() {
        return itemDetailCount;
    }

    public void setItemDetailCount(String itemDetailCount) {
        this.itemDetailCount = itemDetailCount;
    }

    public String getItemTotlePrice() {
        return itemTotlePrice;
    }

    public void setItemTotlePrice(String itemTotlePrice) {
        this.itemTotlePrice = itemTotlePrice;
    }

    public String getProdStatus() {
        return prodStatus;
    }

    public void setProdStatus(String prodStatus) {
        this.prodStatus = prodStatus;
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
        if (!(o instanceof OrderProdItemDTO)) {
            return false;
        }

        OrderProdItemDTO orderProdItemDTO = (OrderProdItemDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, orderProdItemDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderProdItemDTO{" +
            "id=" + getId() +
            ", orderCaseId='" + getOrderCaseId() + "'" +
            ", monthlyOrderCaseId='" + getMonthlyOrderCaseId() + "'" +
            ", itemNameCode='" + getItemNameCode() + "'" +
            ", itemPrice='" + getItemPrice() + "'" +
            ", itemDetailCount='" + getItemDetailCount() + "'" +
            ", itemTotlePrice='" + getItemTotlePrice() + "'" +
            ", prodStatus='" + getProdStatus() + "'" +
            ", createTime='" + getCreateTime() + "'" +
            ", modifyTime='" + getModifyTime() + "'" +
            "}";
    }
}
