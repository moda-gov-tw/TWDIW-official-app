package gov.moda.dw.manager.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * BwdParam 密碼規則
 */
@Entity
@Table(name = "bwd_param")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BwdParam implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
  @SequenceGenerator(name = "sequenceGenerator")
  @Column(name = "id")
  private Long id;

  /**
   * 密碼設定檔
   */
  @NotNull
  @Size(max = 40)
  @Column(name = "bwd_profile_id", length = 40, nullable = false)
  private String bwdProfileId;

  /**
   * 規則ID
   */
  @NotNull
  @Size(max = 40)
  @Column(name = "rule_id", length = 40, nullable = false)
  private String ruleId;

  /**
   * 規則名稱
   */
  @NotNull
  @Size(max = 100)
  @Column(name = "rule_name", length = 100, nullable = false)
  private String ruleName;

  /**
   * 規則描述
   */
  @Size(max = 500)
  @Column(name = "description", length = 500)
  private String description;

  /**
   * 狀態
   */
  @NotNull
  @Column(name = "state", nullable = false)
  private Boolean state;

  @Size(max = 200)
  @Column(name = "str_regular", length = 200)
  private String strRegular;

  /**
   * 參數值
   */
  @Size(max = 500)
  @Column(name = "param_value", length = 500)
  private String paramValue;

  @Size(max = 10)
  @Column(name = "action_type", length = 10)
  private String actionType;

  @Size(max = 10)
  @Column(name = "check_type", length = 10)
  private String checkType;

  /**
   * 錯誤訊息
   */
  @Size(max = 500)
  @Column(name = "error_message", length = 500)
  private String errorMessage;

  /**
   * 建立時間
   */
  @Column(name = "create_time")
  private Instant createTime;

  /**
   * 更新時間
   */
  @Column(name = "update_time")
  private Instant updateTime;

  // jhipster-needle-entity-add-field - JHipster will add fields here

  public Long getId() {
    return this.id;
  }

  public BwdParam id(Long id) {
    this.setId(id);
    return this;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getBwdProfileId() {
    return this.bwdProfileId;
  }

  public BwdParam bwdProfileId(String bwdProfileId) {
    this.setBwdProfileId(bwdProfileId);
    return this;
  }

  public void setBwdProfileId(String bwdProfileId) {
    this.bwdProfileId = bwdProfileId;
  }

  public String getRuleId() {
    return this.ruleId;
  }

  public BwdParam ruleId(String ruleId) {
    this.setRuleId(ruleId);
    return this;
  }

  public void setRuleId(String ruleId) {
    this.ruleId = ruleId;
  }

  public String getRuleName() {
    return this.ruleName;
  }

  public BwdParam ruleName(String ruleName) {
    this.setRuleName(ruleName);
    return this;
  }

  public void setRuleName(String ruleName) {
    this.ruleName = ruleName;
  }

  public String getDescription() {
    return this.description;
  }

  public BwdParam description(String description) {
    this.setDescription(description);
    return this;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Boolean getState() {
    return this.state;
  }

  public BwdParam state(Boolean state) {
    this.setState(state);
    return this;
  }

  public void setState(Boolean state) {
    this.state = state;
  }

  public String getStrRegular() {
    return this.strRegular;
  }

  public BwdParam strRegular(String strRegular) {
    this.setStrRegular(strRegular);
    return this;
  }

  public void setStrRegular(String strRegular) {
    this.strRegular = strRegular;
  }

  public String getParamValue() {
    return this.paramValue;
  }

  public BwdParam paramValue(String paramValue) {
    this.setParamValue(paramValue);
    return this;
  }

  public void setParamValue(String paramValue) {
    this.paramValue = paramValue;
  }

  public String getActionType() {
    return this.actionType;
  }

  public BwdParam actionType(String actionType) {
    this.setActionType(actionType);
    return this;
  }

  public void setActionType(String actionType) {
    this.actionType = actionType;
  }

  public String getCheckType() {
    return this.checkType;
  }

  public BwdParam checkType(String checkType) {
    this.setCheckType(checkType);
    return this;
  }

  public void setCheckType(String checkType) {
    this.checkType = checkType;
  }

  public String getErrorMessage() {
    return this.errorMessage;
  }

  public BwdParam errorMessage(String errorMessage) {
    this.setErrorMessage(errorMessage);
    return this;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public Instant getCreateTime() {
    return this.createTime;
  }

  public BwdParam createTime(Instant createTime) {
    this.setCreateTime(createTime);
    return this;
  }

  public void setCreateTime(Instant createTime) {
    this.createTime = createTime;
  }

  public Instant getUpdateTime() {
    return this.updateTime;
  }

  public BwdParam updateTime(Instant updateTime) {
    this.setUpdateTime(updateTime);
    return this;
  }

  public void setUpdateTime(Instant updateTime) {
    this.updateTime = updateTime;
  }

  // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof BwdParam)) {
      return false;
    }
    return getId() != null && getId().equals(((BwdParam) o).getId());
  }

  @Override
  public int hashCode() {
    // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
    return getClass().hashCode();
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "BwdParam{" +
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
