package gov.moda.dw.manager.service.dto.outside;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link gov.moda.dw.manager.domain.VCItem} entity.
 */
@Schema(description = "VCItem VC列表")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VCItemSimpleDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 50)
    @Schema(description = "VC編號", requiredMode = Schema.RequiredMode.REQUIRED)
    private String serialNo;

    @NotNull
    @Size(max = 50)
    @Schema(description = "VC名稱", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;


    @Schema(description = "類別Id")
    private Long categoryId;

    @NotNull
    @Size(max = 10)
    @Schema(description = "單位", requiredMode = Schema.RequiredMode.REQUIRED)
    private String businessId;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VCItemSimpleDTO)) {
            return false;
        }

        VCItemSimpleDTO vCItemDTO = (VCItemSimpleDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, vCItemDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VCItemDTO{" +
            "id=" + getId() +
            ", serialNo='" + getSerialNo() + "'" +
            ", name='" + getName() + "'" +
            ", categoryId=" + getCategoryId() +
            ", businessId='" + getBusinessId() + "'" +
            "}";
    }
}
