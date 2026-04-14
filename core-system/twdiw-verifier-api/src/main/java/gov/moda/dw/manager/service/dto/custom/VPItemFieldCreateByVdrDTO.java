
package gov.moda.dw.manager.service.dto.custom;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * A DTO for the {@link gov.moda.dw.manager.domain.VPItemField} entity.
 */
@Schema(description = "VPItemField VP欄位")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VPItemFieldCreateByVdrDTO implements Serializable {
    /**
     * VC BusinessId
     */
    @NotBlank
    private String vcBusinessId;

    /**
     * VC名稱
     */
    @NotBlank
    @Size(max = 50)
    private String vcName;

    /**
     * VC編號
     */
    @NotBlank
    @Size(max = 50)
    private String vcSerialNo;

    /**
     * 欄位對外名稱(中/英)
     */
    @NotBlank
    @Size(max = 50)
    private String cname;

    /**
     * 欄位名稱(英)
     */
    @NotBlank
    @Size(max = 50)
    private String ename;

    /**
     * VP id 外鍵
     */
    @JsonIgnore
    private Long vpItemId;

    /**
     * VC類別描述
     */
    @JsonIgnore
    private String vcCategoryDescription = "VDR";

    /**
     * 欄位類別
     */
    @JsonIgnore
    private String vcItemFieldType = "VDR";

    /**
     * 是否必填
     */
    private Boolean isRequired;

    public String getVcBusinessId() {
        return vcBusinessId;
    }

    public void setVcBusinessId(String vcBusinessId) {
        this.vcBusinessId = vcBusinessId;
    }

    public String getVcName() {
        return vcName;
    }

    public void setVcName(String vcName) {
        this.vcName = vcName;
    }

    public String getVcSerialNo() {
        return vcSerialNo;
    }

    public void setVcSerialNo(String vcSerialNo) {
        this.vcSerialNo = vcSerialNo;
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

    public String getVcItemFieldType() {
        return vcItemFieldType;
    }

    public void setVcItemFieldType(String vcItemFieldType) {
        this.vcItemFieldType = vcItemFieldType;
    }

    public Boolean getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VPItemFieldCreateByVdrDTO that = (VPItemFieldCreateByVdrDTO) o;
        return Objects.equals(vcBusinessId, that.vcBusinessId) && Objects.equals(vcName, that.vcName) && Objects.equals(vcSerialNo, that.vcSerialNo) && Objects.equals(cname, that.cname) && Objects.equals(ename, that.ename) && Objects.equals(vpItemId, that.vpItemId) && Objects.equals(vcCategoryDescription, that.vcCategoryDescription) && Objects.equals(vcItemFieldType, that.vcItemFieldType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vcBusinessId, vcName, vcSerialNo, cname, ename, vpItemId, vcCategoryDescription, vcItemFieldType);
    }
}
