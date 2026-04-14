package gov.moda.dw.manager.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link gov.moda.dw.manager.domain.Product} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductDTO implements Serializable {

    private Long id;

    @Size(max = 255)
    @Schema(description = "商品代碼")
    private String productCode;

    @Size(max = 255)
    @Schema(description = "商品名稱")
    private String productName;

    @Size(max = 255)
    @Schema(description = "商品群組")
    private String productGroup;

    @Size(max = 10)
    @Schema(description = "商品業務別碼")
    private String productBsCode;

    @Schema(description = "商品單價(含審查費)")
    private Long unitPrice;

    @Schema(description = "審查處理費")
    private Long verifyFee;

    @Size(max = 255)
    @Schema(description = "使用對象")
    private String useTarget;

    @Size(max = 255)
    @Schema(description = "狀態")
    private String status;

    @Schema(description = "預計調整商品單價")
    private Long unitPriceFuture;

    @Schema(description = "預計調整審查處理費")
    private Long verifyFeeFuture;

    @Schema(description = "預計啟用時間")
    private Instant futureStartTime;

    @Size(max = 255)
    @Schema(description = "備註")
    private String remark;

    @Size(max = 255)
    @Schema(description = "建立者")
    private String createID;

    @Schema(description = "最後編輯時間")
    private Instant modifyTime;

    @Schema(description = "創建時間")
    private Instant createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductGroup() {
        return productGroup;
    }

    public void setProductGroup(String productGroup) {
        this.productGroup = productGroup;
    }

    public String getProductBsCode() {
        return productBsCode;
    }

    public void setProductBsCode(String productBsCode) {
        this.productBsCode = productBsCode;
    }

    public Long getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Long unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Long getVerifyFee() {
        return verifyFee;
    }

    public void setVerifyFee(Long verifyFee) {
        this.verifyFee = verifyFee;
    }

    public String getUseTarget() {
        return useTarget;
    }

    public void setUseTarget(String useTarget) {
        this.useTarget = useTarget;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getUnitPriceFuture() {
        return unitPriceFuture;
    }

    public void setUnitPriceFuture(Long unitPriceFuture) {
        this.unitPriceFuture = unitPriceFuture;
    }

    public Long getVerifyFeeFuture() {
        return verifyFeeFuture;
    }

    public void setVerifyFeeFuture(Long verifyFeeFuture) {
        this.verifyFeeFuture = verifyFeeFuture;
    }

    public Instant getFutureStartTime() {
        return futureStartTime;
    }

    public void setFutureStartTime(Instant futureStartTime) {
        this.futureStartTime = futureStartTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreateID() {
        return createID;
    }

    public void setCreateID(String createID) {
        this.createID = createID;
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
        if (!(o instanceof ProductDTO)) {
            return false;
        }

        ProductDTO productDTO = (ProductDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, productDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductDTO{" +
            "id=" + getId() +
            ", productCode='" + getProductCode() + "'" +
            ", productName='" + getProductName() + "'" +
            ", productGroup='" + getProductGroup() + "'" +
            ", productBsCode='" + getProductBsCode() + "'" +
            ", unitPrice=" + getUnitPrice() +
            ", verifyFee=" + getVerifyFee() +
            ", useTarget='" + getUseTarget() + "'" +
            ", status='" + getStatus() + "'" +
            ", unitPriceFuture=" + getUnitPriceFuture() +
            ", verifyFeeFuture=" + getVerifyFeeFuture() +
            ", futureStartTime='" + getFutureStartTime() + "'" +
            ", remark='" + getRemark() + "'" +
            ", createID='" + getCreateID() + "'" +
            ", modifyTime='" + getModifyTime() + "'" +
            ", createTime='" + getCreateTime() + "'" +
            "}";
    }
}
