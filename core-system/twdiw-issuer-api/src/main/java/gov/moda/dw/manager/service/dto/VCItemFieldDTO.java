package gov.moda.dw.manager.service.dto;

import gov.moda.dw.manager.domain.RegularExpression;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
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

    @Schema(description = "RegularExpressionId")
    private Long regularExpressionId;

    @Schema(description = "卡面資料")
    private Integer cardCoverData;

    private RegularExpression regularExpression;
    
    @Schema(description = "是否必填")
    private Boolean isRequired;

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

    public RegularExpression getRegularExpression() {
        return regularExpression;
    }

    public void setRegularExpression(RegularExpression regularExpression) {
        this.regularExpression = regularExpression;
    }

    public Integer getCardCoverData() {
        return cardCoverData;
    }

    public void setCardCoverData(Integer cardCoverData) {
        this.cardCoverData = cardCoverData;
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
            ", cardCoverData=" + getCardCoverData() +
            ", regularExpression=" + getRegularExpression() +
            ", isRequired='" + getIsRequired() + "'" +
            "}";
    }

    public Long getRegularExpressionId() {
        return regularExpressionId;
    }

    public void setRegularExpressionId(Long regularExpressionId) {
        this.regularExpressionId = regularExpressionId;
    }
}
