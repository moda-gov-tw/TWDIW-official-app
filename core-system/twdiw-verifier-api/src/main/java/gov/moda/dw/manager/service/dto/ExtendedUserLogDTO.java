package gov.moda.dw.manager.service.dto;

import gov.moda.dw.manager.domain.ExtendedUserLog;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ExtendedUserLog} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ExtendedUserLogDTO implements Serializable {

  private Long id;

  @NotNull
  @Size(max = 140)
  @Schema(description = "異動人", requiredMode = Schema.RequiredMode.REQUIRED)
  private String actor;

  @NotNull
  @Size(max = 20)
  @Schema(description = "異動類型", requiredMode = Schema.RequiredMode.REQUIRED)
  private String logType;

  @NotNull
  @Schema(description = "Log建立時間", requiredMode = Schema.RequiredMode.REQUIRED)
  private Instant logTime;

  @Size(max = 255)
  @Schema(description = "組織")
  private String orgId;

  @Size(max = 320)
  @Schema(description = "帳號")
  private String userId;

  @Size(max = 128)
  @Schema(description = "姓名")
  private String userName;

  @Size(max = 320)
  @Schema(description = "email")
  private String email;

  @Size(max = 20)
  @Schema(description = "手機")
  private String phone;

  @Size(max = 20)
  @Schema(description = "市話")
  private String tel;

  @Size(max = 20)
  @Schema(description = "員工編號")
  private String employeeId;

  @Size(max = 10)
  @Schema(description = "員工類型")
  private String employeeTypeId;

  @Schema(description = "離職日")
  private Instant leftDate;

  @Schema(description = "到職日")
  private Instant onboardDate;

  @Size(max = 10)
  @Schema(description = "帳號類型")
  private String userTypeId;

  @Size(max = 255)
  @Schema(description = "預留欄位1")
  private String dataRole1;

  @Size(max = 255)
  @Schema(description = "預留欄位2")
  private String dataRole2;

  @Size(max = 10)
  @Schema(description = "狀態")
  private String state;

  @Schema(description = "帳號建立時間")
  private Instant createTime;

  @Schema(description = "上次權限修改時間")
  private Instant authChangeTime;

  @Schema(description = "上次重置密碼時間")
  private Instant pwdResetTime;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getActor() {
    return actor;
  }

  public void setActor(String actor) {
    this.actor = actor;
  }

  public String getLogType() {
    return logType;
  }

  public void setLogType(String logType) {
    this.logType = logType;
  }

  public Instant getLogTime() {
    return logTime;
  }

  public void setLogTime(Instant logTime) {
    this.logTime = logTime;
  }

  public String getOrgId() {
    return orgId;
  }

  public void setOrgId(String orgId) {
    this.orgId = orgId;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getTel() {
    return tel;
  }

  public void setTel(String tel) {
    this.tel = tel;
  }

  public String getEmployeeId() {
    return employeeId;
  }

  public void setEmployeeId(String employeeId) {
    this.employeeId = employeeId;
  }

  public String getEmployeeTypeId() {
    return employeeTypeId;
  }

  public void setEmployeeTypeId(String employeeTypeId) {
    this.employeeTypeId = employeeTypeId;
  }

  public Instant getLeftDate() {
    return leftDate;
  }

  public void setLeftDate(Instant leftDate) {
    this.leftDate = leftDate;
  }

  public Instant getOnboardDate() {
    return onboardDate;
  }

  public void setOnboardDate(Instant onboardDate) {
    this.onboardDate = onboardDate;
  }

  public String getUserTypeId() {
    return userTypeId;
  }

  public void setUserTypeId(String userTypeId) {
    this.userTypeId = userTypeId;
  }

  public String getDataRole1() {
    return dataRole1;
  }

  public void setDataRole1(String dataRole1) {
    this.dataRole1 = dataRole1;
  }

  public String getDataRole2() {
    return dataRole2;
  }

  public void setDataRole2(String dataRole2) {
    this.dataRole2 = dataRole2;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public Instant getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Instant createTime) {
    this.createTime = createTime;
  }

  public Instant getAuthChangeTime() { return authChangeTime; }

  public void setAuthChangeTime(Instant authChangeTime) { this.authChangeTime = authChangeTime; }

  public Instant getPwdResetTime() { return pwdResetTime; }

  public void setPwdResetTime(Instant pwdResetTime) { this.pwdResetTime = pwdResetTime; }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ExtendedUserLogDTO)) {
      return false;
    }

    ExtendedUserLogDTO extendedUserLogDTO = (ExtendedUserLogDTO) o;
    if (this.id == null) {
      return false;
    }
    return Objects.equals(this.id, extendedUserLogDTO.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "ExtendedUserLogDTO{" +
            "id=" + getId() +
            ", actor='" + getActor() + "'" +
            ", logType='" + getLogType() + "'" +
            ", logTime='" + getLogTime() + "'" +
            ", orgId='" + getOrgId() + "'" +
            ", userId='" + getUserId() + "'" +
            ", userName='" + getUserName() + "'" +
            ", email='" + getEmail() + "'" +
            ", phone='" + getPhone() + "'" +
            ", tel='" + getTel() + "'" +
            ", employeeId='" + getEmployeeId() + "'" +
            ", employeeTypeId='" + getEmployeeTypeId() + "'" +
            ", leftDate='" + getLeftDate() + "'" +
            ", onboardDate='" + getOnboardDate() + "'" +
            ", userTypeId='" + getUserTypeId() + "'" +
            ", dataRole1='" + getDataRole1() + "'" +
            ", dataRole2='" + getDataRole2() + "'" +
            ", state='" + getState() + "'" +
            ", createTime='" + getCreateTime() + "'" +
            ", authChangeTime='" + getAuthChangeTime() + "'" +
            ", pwdResetTime='" + getPwdResetTime() + "'" +
            "}";
    }
}
