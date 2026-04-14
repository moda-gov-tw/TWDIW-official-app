package gov.moda.dw.manager.service.dto.outside;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link gov.moda.dw.manager.domain.VCItemField} entity.
 */
@Schema(description = "VCItemField VC欄位")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VCItemFieldDTO implements Serializable {

    private Long id;

    @NotNull
    @Schema(description = "VC id 外鍵", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long vcItemId;

    @NotNull
    @Size(max = 10)
    @Schema(description = "欄位類別(BASIC NORMAL NORMAL)", requiredMode = Schema.RequiredMode.REQUIRED)
    private String type;

    @NotNull
    @Size(max = 50)
    @Schema(description = "欄位對外名稱(中/英)", requiredMode = Schema.RequiredMode.REQUIRED)
    private String cname;

    @NotNull
    @Size(max = 50)
    @Schema(description = "欄位名稱(英)", requiredMode = Schema.RequiredMode.REQUIRED)
    private String ename;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVcItemId() {
        return vcItemId;
    }

    public void setVcItemId(Long vcItemId) {
        this.vcItemId = vcItemId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VCItemFieldDTO)) {
            return false;
        }

        VCItemFieldDTO vCItemFieldDTO = (VCItemFieldDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, vCItemFieldDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VCItemFieldDTO{" +
            "id=" + getId() +
            ", vcItemId=" + getVcItemId() +
            ", type='" + getType() + "'" +
            ", cname='" + getCname() + "'" +
            ", ename='" + getEname() + "'" +
            "}";
    }
}
