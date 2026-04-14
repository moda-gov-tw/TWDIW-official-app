package gov.moda.dw.manager.service.dto;

import gov.moda.dw.manager.domain.RegularExpression;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link gov.moda.dw.manager.domain.Field} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FieldDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 50)
    @Schema(description = "欄位對外名稱(中/英)", requiredMode = Schema.RequiredMode.REQUIRED)
    private String cname;

    @NotNull
    @Size(max = 50)
    @Schema(description = "欄位名稱(英)", requiredMode = Schema.RequiredMode.REQUIRED)
    private String ename;

    @NotNull
    @Size(max = 10)
    @Schema(description = "欄位類型", requiredMode = Schema.RequiredMode.REQUIRED)
    private String type;

    @NotNull
    @Schema(description = "是否顯示", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean visible;

    @Size(max = 255)
    @Schema(description = "單位", requiredMode = Schema.RequiredMode.REQUIRED)
    private String businessId;

    @Schema(description = "regular expression id (主鍵)")
    private Long regularExpressionId;

    private RegularExpression regularExpression;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public RegularExpression getRegularExpression() {
        return regularExpression;
    }

    public void setRegularExpression(RegularExpression regularExpression) {
        this.regularExpression = regularExpression;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FieldDTO)) {
            return false;
        }

        FieldDTO fieldDTO = (FieldDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, fieldDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FieldDTO{" +
            "id=" + getId() +
            ", cname='" + getCname() + "'" +
            ", ename='" + getEname() + "'" +
            ", type='" + getType() + "'" +
            ", visible='" + getVisible() + "'" +
            ", businessId='" + getBusinessId() + "'" +
            ", regularExpression=" + getRegularExpression() +
            "}";
    }

    public Long getRegularExpressionId() {
        return regularExpressionId;
    }

    public void setRegularExpressionId(Long regularExpressionId) {
        this.regularExpressionId = regularExpressionId;
    }
}
