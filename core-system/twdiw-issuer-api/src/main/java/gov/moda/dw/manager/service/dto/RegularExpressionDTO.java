package gov.moda.dw.manager.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link gov.moda.dw.manager.domain.RegularExpression} entity.
 */
public class RegularExpressionDTO implements Serializable {

    private Long id;

    /**
     * 正規表達式執行類型(Require所有欄位都要檢查|Specified指定欄位檢查)
     */
    @Size(max = 50)
    @Schema(description = "正規表達式執行類型(Require所有欄位都要檢查|Specified指定欄位檢查)", requiredMode = Schema.RequiredMode.REQUIRED)
    private String type;

    /**
     * 正規表達式名稱
     */
    @NotNull
    @Size(max = 40)
    @Schema(description = "正規表達式名稱", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /**
     * 正規表達式
     */
    @NotNull
    @Size(max = 200)
    @Schema(description = "正規表達式", requiredMode = Schema.RequiredMode.REQUIRED)
    private String regularExpression;

    /**
     * 描述
     */
    @NotNull
    @Size(max = 200)
    @Schema(description = "描述", requiredMode = Schema.RequiredMode.REQUIRED)
    private String description;

    /**
     * 返回給使用者用的錯誤訊息
     */
    @NotNull
    @Size(max = 200)
    @Schema(description = "返回給使用者用的錯誤訊息", requiredMode = Schema.RequiredMode.REQUIRED)
    private String errorMsg;

    /**
     * 正面表列或反面表列
     */
    @NotNull
    @Size(max = 10)
    @Schema(description = "正面表列或反面表列", requiredMode = Schema.RequiredMode.REQUIRED)
    private String ruleType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegularExpression() {
        return regularExpression;
    }

    public void setRegularExpression(String regularExpression) {
        this.regularExpression = regularExpression;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public @NotNull @Size(max = 200) String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(@NotNull @Size(max = 200) String errorMsg) {
        this.errorMsg = errorMsg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RegularExpressionDTO)) {
            return false;
        }

        RegularExpressionDTO regularExpression = (RegularExpressionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, regularExpression.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RegularExpression{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", name='" + getName() + "'" +
            ", regularExpression='" + getRegularExpression() + "'" +
            ", description='" + getDescription() + "'" +
            ", errorMsg='" + getErrorMsg() + "'" +
            ", ruleType='" + getRuleType() + "'" +
            "}";
    }
}
