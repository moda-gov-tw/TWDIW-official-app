package gov.moda.dw.issuer.vc.service.dto;

import gov.moda.dw.issuer.vc.domain.ExtendedUser;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ExtendedUser} entity.
 */
@Schema(description = "ExtendedUser")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ExtendedUserDTO implements Serializable {

  private Long id;

  @Size(max = 30)
  @Schema(description = "組織")
  private String orgId;

  @NotNull
  @Size(max = 320)
  @Schema(description = "帳號", requiredMode = Schema.RequiredMode.REQUIRED)
  private String userId;

  @NotNull
  @Size(max = 128)
  @Schema(description = "姓名", requiredMode = Schema.RequiredMode.REQUIRED)
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
  @Schema(description = "資料角色1")
  private String dataRole1;

  @Size(max = 255)
  @Schema(description = "資料角色2")
  private String dataRole2;

  @NotNull
  @Size(max = 10)
  @Schema(description = "狀態", requiredMode = Schema.RequiredMode.REQUIRED)
  private String state;

  @NotNull
  @Schema(description = "建立日", requiredMode = Schema.RequiredMode.REQUIRED)
  private Instant createTime;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ExtendedUserDTO)) {
      return false;
    }

    ExtendedUserDTO extendedUserDTO = (ExtendedUserDTO) o;
    if (this.id == null) {
      return false;
    }
    return Objects.equals(this.id, extendedUserDTO.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "ExtendedUserDTO{" +
            "id=" + getId() +
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
            "}";
    }
}
