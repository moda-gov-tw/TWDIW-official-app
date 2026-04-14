package gov.moda.dw.manager.service.dto;

import gov.moda.dw.manager.domain.BwdParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link BwdParam} entity.
 */
@Schema(description = "BwdParam 密碼規則")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BwdParamDTO implements Serializable {

  private Long id;

  @NotNull
  @Size(max = 40)
  @Schema(description = "密碼設定檔", requiredMode = Schema.RequiredMode.REQUIRED)
  private String bwdProfileId;

  @NotNull
  @Size(max = 40)
  @Schema(description = "規則ID", requiredMode = Schema.RequiredMode.REQUIRED)
  private String ruleId;

  @NotNull
  @Size(max = 100)
  @Schema(description = "規則名稱", requiredMode = Schema.RequiredMode.REQUIRED)
  private String ruleName;

  @Size(max = 500)
  @Schema(description = "規則描述")
  private String description;

  @NotNull
  @Schema(description = "狀態", requiredMode = Schema.RequiredMode.REQUIRED)
  private Boolean state;

  @Size(max = 200)
  private String strRegular;

  @Size(max = 500)
  @Schema(description = "參數值")
  private String paramValue;

  @Size(max = 10)
  private String actionType;

  @Size(max = 10)
  private String checkType;

  @Size(max = 500)
  @Schema(description = "錯誤訊息")
  private String errorMessage;

  @Schema(description = "建立時間")
  private Instant createTime;

  @Schema(description = "更新時間")
  private Instant updateTime;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getBwdProfileId() {
    return bwdProfileId;
  }

  public void setBwdProfileId(String bwdProfileId) {
    this.bwdProfileId = bwdProfileId;
  }

  public String getRuleId() {
    return ruleId;
  }

  public void setRuleId(String ruleId) {
    this.ruleId = ruleId;
  }

  public String getRuleName() {
    return ruleName;
  }

  public void setRuleName(String ruleName) {
    this.ruleName = ruleName;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Boolean getState() {
    return state;
  }

  public void setState(Boolean state) {
    this.state = state;
  }

  public String getStrRegular() {
    return strRegular;
  }

  public void setStrRegular(String strRegular) {
    this.strRegular = strRegular;
  }

  public String getParamValue() {
    return paramValue;
  }

  public void setParamValue(String paramValue) {
    this.paramValue = paramValue;
  }

  public String getActionType() {
    return actionType;
  }

  public void setActionType(String actionType) {
    this.actionType = actionType;
  }

  public String getCheckType() {
    return checkType;
  }

  public void setCheckType(String checkType) {
    this.checkType = checkType;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public Instant getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Instant createTime) {
    this.createTime = createTime;
  }

  public Instant getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(Instant updateTime) {
    this.updateTime = updateTime;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof BwdParamDTO)) {
      return false;
    }

    BwdParamDTO bwdParamDTO = (BwdParamDTO) o;
    if (this.id == null) {
      return false;
    }
    return Objects.equals(this.id, bwdParamDTO.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "BwdParamDTO{" +
            "id=" + getId() +
            ", bwdProfileId='" + getBwdProfileId() + "'" +
            ", ruleId='" + getRuleId() + "'" +
            ", ruleName='" + getRuleName() + "'" +
            ", description='" + getDescription() + "'" +
            ", state='" + getState() + "'" +
            ", strRegular='" + getStrRegular() + "'" +
            ", paramValue='" + getParamValue() + "'" +
            ", actionType='" + getActionType() + "'" +
            ", checkType='" + getCheckType() + "'" +
            ", errorMessage='" + getErrorMessage() + "'" +
            ", createTime='" + getCreateTime() + "'" +
            ", updateTime='" + getUpdateTime() + "'" +
            "}";
    }
}
