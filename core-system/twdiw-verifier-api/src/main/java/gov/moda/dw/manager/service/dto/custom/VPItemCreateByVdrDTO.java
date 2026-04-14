package gov.moda.dw.manager.service.dto.custom;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

/**
 * A DTO for the {@link gov.moda.dw.manager.domain.VPItem} entity.
 */
@Schema(description = "VPItem VP列表")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VPItemCreateByVdrDTO implements Serializable {

    @NotBlank
    @Size(max = 50)
    @Schema(description = "VP編號", requiredMode = Schema.RequiredMode.REQUIRED)
    private String serialNo;

    @NotBlank
    @Size(max = 18)
    @Schema(description = "VP名稱", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotBlank
    @Size(max = 30)
    @Schema(description = "VP授權目的", requiredMode = Schema.RequiredMode.REQUIRED)
    private String purpose;

    @NotBlank
    @Schema(description = "VP授權條款", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("terms")
    private String terms;

    @NotEmpty
    private List<VPItemFieldCreateByVdrDTO> vcFieldIds;

    @JsonIgnore
    private String businessId;

    @JsonIgnore
    private Long crUser;

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

    public Long getCrUser() {
        return crUser;
    }

    public void setCrUser(Long crUser) {
        this.crUser = crUser;
    }

    public List<VPItemFieldCreateByVdrDTO> getVcFieldIds() {
        return vcFieldIds;
    }

    public void setVcFieldIds(List<VPItemFieldCreateByVdrDTO> vcFieldIds) {
        this.vcFieldIds = vcFieldIds;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VPItemCreateByVdrDTO that = (VPItemCreateByVdrDTO) o;
        return Objects.equals(serialNo, that.serialNo) && Objects.equals(name, that.name) && Objects.equals(vcFieldIds, that.vcFieldIds) && Objects.equals(businessId, that.businessId) && Objects.equals(crUser, that.crUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serialNo, name, vcFieldIds, businessId, crUser);
    }
}
