package gov.moda.dw.manager.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * A ExtendedUserLog.
 */
@Entity
@Table(name = "extended_user_log")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ExtendedUserLog implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
  @SequenceGenerator(name = "sequenceGenerator")
  @Column(name = "id")
  private Long id;

  /**
   * 異動人
   */
  @NotNull
  @Size(max = 140)
  @Column(name = "actor", length = 140, nullable = false)
  private String actor;

  /**
   * 異動類型
   */
  @NotNull
  @Size(max = 20)
  @Column(name = "log_type", length = 20, nullable = false)
  private String logType;

  /**
   * Log建立時間
   */
  @NotNull
  @Column(name = "log_time", nullable = false)
  private Instant logTime;

  /**
   * 組織
   */
  @Size(max = 255)
  @Column(name = "org_id")
  private String orgId;

  /**
   * 帳號
   */
  @Size(max = 320)
  @Column(name = "user_id", length = 320)
  private String userId;

  /**
   * 姓名
   */
  @Size(max = 128)
  @Column(name = "user_name", length = 128)
  private String userName;

  /**
   * email
   */
  @Size(max = 320)
  @Column(name = "email", length = 320)
  private String email;

  /**
   * 手機
   */
  @Size(max = 20)
  @Column(name = "phone", length = 20)
  private String phone;

  /**
   * 市話
   */
  @Size(max = 20)
  @Column(name = "tel", length = 20)
  private String tel;

  /**
   * 員工編號
   */
  @Size(max = 20)
  @Column(name = "employee_id", length = 20)
  private String employeeId;

  /**
   * 員工類型
   */
  @Size(max = 10)
  @Column(name = "employee_type_id", length = 10)
  private String employeeTypeId;

  /**
   * 離職日
   */
  @Column(name = "left_date")
  private Instant leftDate;

  /**
   * 到職日
   */
  @Column(name = "onboard_date")
  private Instant onboardDate;

  /**
   * 帳號類型
   */
  @Size(max = 10)
  @Column(name = "user_type_id", length = 10)
  private String userTypeId;

  /**
   * 預留欄位1
   */
  @Size(max = 255)
  @Column(name = "data_role_1", length = 255)
  private String dataRole1;

  /**
   * 預留欄位2
   */
  @Size(max = 255)
  @Column(name = "data_role_2", length = 255)
  private String dataRole2;

  /**
   * 狀態
   */
  @Size(max = 10)
  @Column(name = "state", length = 10)
  private String state;

  /**
   * 帳號建立時間
   */
  @Column(name = "create_time")
  private Instant createTime;

  /**
   * 上次變更權限時間
   */
  @Column(name = "auth_change_time")
  private Instant authChangeTime;

  /**
   * 上次重置密碼時間
   */
  @Column(name = "pwd_reset_time")
  private Instant pwdResetTime;

  // jhipster-needle-entity-add-field - JHipster will add fields here

  public Long getId() {
    return this.id;
  }

  public ExtendedUserLog id(Long id) {
    this.setId(id);
    return this;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getActor() {
    return this.actor;
  }

  public ExtendedUserLog actor(String actor) {
    this.setActor(actor);
    return this;
  }

  public void setActor(String actor) {
    this.actor = actor;
  }

  public String getLogType() {
    return this.logType;
  }

  public ExtendedUserLog logType(String logType) {
    this.setLogType(logType);
    return this;
  }

  public void setLogType(String logType) {
    this.logType = logType;
  }

  public Instant getLogTime() {
    return this.logTime;
  }

  public ExtendedUserLog logTime(Instant logTime) {
    this.setLogTime(logTime);
    return this;
  }

  public void setLogTime(Instant logTime) {
    this.logTime = logTime;
  }

  public String getOrgId() {
    return this.orgId;
  }

  public ExtendedUserLog orgId(String orgId) {
    this.setOrgId(orgId);
    return this;
  }

  public void setOrgId(String orgId) {
    this.orgId = orgId;
  }

  public String getUserId() {
    return this.userId;
  }

  public ExtendedUserLog userId(String userId) {
    this.setUserId(userId);
    return this;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getUserName() {
    return this.userName;
  }

  public ExtendedUserLog userName(String userName) {
    this.setUserName(userName);
    return this;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getEmail() {
    return this.email;
  }

  public ExtendedUserLog email(String email) {
    this.setEmail(email);
    return this;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPhone() {
    return this.phone;
  }

  public ExtendedUserLog phone(String phone) {
    this.setPhone(phone);
    return this;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getTel() {
    return this.tel;
  }

  public ExtendedUserLog tel(String tel) {
    this.setTel(tel);
    return this;
  }

  public void setTel(String tel) {
    this.tel = tel;
  }

  public String getEmployeeId() {
    return this.employeeId;
  }

  public ExtendedUserLog employeeId(String employeeId) {
    this.setEmployeeId(employeeId);
    return this;
  }

  public void setEmployeeId(String employeeId) {
    this.employeeId = employeeId;
  }

  public String getEmployeeTypeId() {
    return this.employeeTypeId;
  }

  public ExtendedUserLog employeeTypeId(String employeeTypeId) {
    this.setEmployeeTypeId(employeeTypeId);
    return this;
  }

  public void setEmployeeTypeId(String employeeTypeId) {
    this.employeeTypeId = employeeTypeId;
  }

  public Instant getLeftDate() {
    return this.leftDate;
  }

  public ExtendedUserLog leftDate(Instant leftDate) {
    this.setLeftDate(leftDate);
    return this;
  }

  public void setLeftDate(Instant leftDate) {
    this.leftDate = leftDate;
  }

  public Instant getOnboardDate() {
    return this.onboardDate;
  }

  public ExtendedUserLog onboardDate(Instant onboardDate) {
    this.setOnboardDate(onboardDate);
    return this;
  }

  public void setOnboardDate(Instant onboardDate) {
    this.onboardDate = onboardDate;
  }

  public String getUserTypeId() {
    return this.userTypeId;
  }

  public ExtendedUserLog userTypeId(String userTypeId) {
    this.setUserTypeId(userTypeId);
    return this;
  }

  public void setUserTypeId(String userTypeId) {
    this.userTypeId = userTypeId;
  }

  public String getDataRole1() {
    return this.dataRole1;
  }

  public ExtendedUserLog dataRole1(String dataRole1) {
    this.setDataRole1(dataRole1);
    return this;
  }

  public void setDataRole1(String dataRole1) {
    this.dataRole1 = dataRole1;
  }

  public String getDataRole2() {
    return this.dataRole2;
  }

  public ExtendedUserLog dataRole2(String dataRole2) {
    this.setDataRole2(dataRole2);
    return this;
  }

  public void setDataRole2(String dataRole2) {
    this.dataRole2 = dataRole2;
  }

  public String getState() {
    return this.state;
  }

  public ExtendedUserLog state(String state) {
    this.setState(state);
    return this;
  }

  public void setState(String state) {
    this.state = state;
  }

  public Instant getCreateTime() {
    return this.createTime;
  }

  public ExtendedUserLog createTime(Instant createTime) {
    this.setCreateTime(createTime);
    return this;
  }

  public void setCreateTime(Instant createTime) {
    this.createTime = createTime;
  }

  public Instant getAuthChangeTime() {
      return this.authChangeTime;
  }

  public ExtendedUserLog authChangeTime(Instant authChangeTime) {
      this.setAuthChangeTime(authChangeTime);
      return this;
  }

  public void setAuthChangeTime(Instant authChangeTime) {
      this.authChangeTime = authChangeTime;
  }

  public Instant getPwdResetTime() {
      return this.pwdResetTime;
  }

  public ExtendedUserLog pwdResetTime(Instant pwdResetTime) {
      this.setPwdResetTime(pwdResetTime);
      return this;
  }

  public void setPwdResetTime(Instant pwdResetTime) {
      this.pwdResetTime = pwdResetTime;
  }

  // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ExtendedUserLog)) {
      return false;
    }
    return getId() != null && getId().equals(((ExtendedUserLog) o).getId());
  }

  @Override
  public int hashCode() {
    // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
    return getClass().hashCode();
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "ExtendedUserLog{" +
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
