package gov.moda.dw.manager.service.dto;

import gov.moda.dw.manager.domain.Org;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link Org} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrgTitleResDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 255)
    @Schema(description = "組織代碼", requiredMode = Schema.RequiredMode.REQUIRED)
    private String OrgId;

    @NotNull
    @Size(max = 100)
    @Schema(description = "組織中文名稱", requiredMode = Schema.RequiredMode.REQUIRED)
    private String OrgTwName;

    @NotNull
    @Size(max = 100)
    @Schema(description = "組織英文名稱", requiredMode = Schema.RequiredMode.REQUIRED)
    private String OrgEnName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotNull @Size(max = 255) String getOrgId() {
        return OrgId;
    }

    public void setOrgId(@NotNull @Size(max = 255) String orgId) {
        OrgId = orgId;
    }

    public @NotNull @Size(max = 100) String getOrgTwName() {
        return OrgTwName;
    }

    public void setOrgTwName(@NotNull @Size(max = 100) String orgTwName) {
        OrgTwName = orgTwName;
    }

    public @NotNull @Size(max = 100) String getOrgEnName() {
        return OrgEnName;
    }

    public void setOrgEnName(@NotNull @Size(max = 100) String orgEnName) {
        OrgEnName = orgEnName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrgTitleResDTO)) return false;
        OrgTitleResDTO orgDTO = (OrgTitleResDTO) o;
        if (this.id == null) return false;
        return Objects.equals(this.id, orgDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return (
            "OrgDTO{" +
            "id=" +
            getId() +
            ", orgId=" +
            getOrgId() +
            "'" +
            ", OrgTwName='" +
            getOrgTwName() +
            "'" +
            ", OrgEnName='" +
            getOrgEnName() +
            "'" +
            "}"
        );
    }
}
