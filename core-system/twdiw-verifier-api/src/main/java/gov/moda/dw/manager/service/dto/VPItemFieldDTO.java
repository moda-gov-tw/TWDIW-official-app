package gov.moda.dw.manager.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link gov.moda.dw.manager.domain.VPItemField} entity.
 */
@Schema(description = "VPItemField VP欄位")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VPItemFieldDTO implements Serializable {

    private Long id;

    @NotNull
    @Schema(description = "VP id 外鍵", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long vpItemId;

    @Size(max = 50)
    @Schema(description = "VC類別描述")
    private String vcCategoryDescription;

    @NotNull
    @Size(max = 50)
    @Schema(description = "VC名稱", requiredMode = Schema.RequiredMode.REQUIRED)
    private String vcName;

    @Size(max = 20)
    @Schema(description = "欄位類別(BASIC NORMAL NORMAL)")
    private String vcItemFieldType;

    @NotNull
    @Size(max = 50)
    @Schema(description = "欄位對外名稱(中/英)", requiredMode = Schema.RequiredMode.REQUIRED)
    private String cname;

    @NotNull
    @Size(max = 50)
    @Schema(description = "欄位名稱(英)", requiredMode = Schema.RequiredMode.REQUIRED)
    private String ename;

    @NotNull
    @Size(max = 50)
    @Schema(description = "VC編號", requiredMode = Schema.RequiredMode.REQUIRED)
    private String vcSerialNo;

    @Size(max = 255)
    @Schema(description = "VC單位")
    private String vcBusinessId;

    @Size(max = 255)
    @Schema(description = "VP單位")
    private String vpBusinessId;

    @Schema(description = "是否必填")
    private Boolean isRequired;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVpItemId() {
        return vpItemId;
    }

    public void setVpItemId(Long vpItemId) {
        this.vpItemId = vpItemId;
    }

    public String getVcCategoryDescription() {
        return vcCategoryDescription;
    }

    public void setVcCategoryDescription(String vcCategoryDescription) {
        this.vcCategoryDescription = vcCategoryDescription;
    }

    public String getVcName() {
        return vcName;
    }

    public void setVcName(String vcName) {
        this.vcName = vcName;
    }

    public String getVcItemFieldType() {
        return vcItemFieldType;
    }

    public void setVcItemFieldType(String vcItemFieldType) {
        this.vcItemFieldType = vcItemFieldType;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getEname() {
        return ename;
    }

    public void setEname(String ename) {
        this.ename = ename;
    }

    public String getVcSerialNo() {
        return vcSerialNo;
    }

    public void setVcSerialNo(String vcSerialNo) {
        this.vcSerialNo = vcSerialNo;
    }

    public String getVcBusinessId() {
        return vcBusinessId;
    }

    public void setVcBusinessId(String vcBusinessId) {
        this.vcBusinessId = vcBusinessId;
    }

    public String getVpBusinessId() {
        return vpBusinessId;
    }

    public void setVpBusinessId(String vpBusinessId) {
        this.vpBusinessId = vpBusinessId;
    }

    public Boolean getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VPItemFieldDTO)) {
            return false;
        }

        VPItemFieldDTO vPItemFieldDTO = (VPItemFieldDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, vPItemFieldDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VPItemFieldDTO{" +
            "id=" + getId() +
            ", vpItemId=" + getVpItemId() +
            ", vcCategoryDescription='" + getVcCategoryDescription() + "'" +
            ", vcName='" + getVcName() + "'" +
            ", vcItemFieldType='" + getVcItemFieldType() + "'" +
            ", cname='" + getCname() + "'" +
            ", ename='" + getEname() + "'" +
            ", vcSerialNo='" + getVcSerialNo() + "'" +
            ", vcBusinessId='" + getVcBusinessId() + "'" +
            ", vpBusinessId='" + getVpBusinessId() + "'" +
            ", isRequired='" + getIsRequired() + "'" +
            "}";
    }
}
