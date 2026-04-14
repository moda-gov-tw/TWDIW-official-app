package gov.moda.dw.manager.service.dto.custom;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * A DTO for the {@link gov.moda.dw.manager.domain.VPItem} entity.
 */
@Schema(description = "VPItem VP列表")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VPItemCreateDTO implements Serializable {

    @NotNull
    @Size(max = 50)
    @Schema(description = "VP編號", requiredMode = Schema.RequiredMode.REQUIRED)
    private String serialNo;

    @NotNull
    @Size(max = 50)
    @Schema(description = "VP名稱", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;


    private List<Long> vcFieldIds;


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

    public Long getCrUser() {
        return crUser;
    }

    public void setCrUser(Long crUser) {
        this.crUser = crUser;
    }

    public List<Long> getVcFieldIds() {
        return vcFieldIds;
    }

    public void setVcFieldIds(List<Long> vcFieldIds) {
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
        VPItemCreateDTO that = (VPItemCreateDTO) o;
        return Objects.equals(serialNo, that.serialNo) && Objects.equals(name, that.name) && Objects.equals(vcFieldIds, that.vcFieldIds) && Objects.equals(businessId, that.businessId) && Objects.equals(crUser, that.crUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serialNo, name, vcFieldIds, businessId, crUser);
    }
}
