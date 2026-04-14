package gov.moda.dw.manager.service.dto;

import gov.moda.dw.manager.domain.LoginView;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link LoginView} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LoginViewDTO implements Serializable {

  private Long id;

  @Schema(description = "帳號")
  private String userId;

  @Schema(description = "登入失敗數")
  private Integer failCount;

  @Schema(description = "最後登入時間")
  private Instant lastLogin;

  @Schema(description = "密碼Hash")
  private String bwdHash;

  @Schema(description = "密碼異動時間")
  private Instant bwdDate;

  @Schema(description = "帳號狀態")
  private String loginIdState;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public Integer getFailCount() {
    return failCount;
  }

  public void setFailCount(Integer failCount) {
    this.failCount = failCount;
  }

  public Instant getLastLogin() {
    return lastLogin;
  }

  public void setLastLogin(Instant lastLogin) {
    this.lastLogin = lastLogin;
  }

  public String getBwdHash() {
    return bwdHash;
  }

  public void setBwdHash(String bwdHash) {
    this.bwdHash = bwdHash;
  }

  public Instant getBwdDate() {
    return bwdDate;
  }

  public void setBwdDate(Instant bwdDate) {
    this.bwdDate = bwdDate;
  }

  public String getLoginIdState() {
    return loginIdState;
  }

  public void setLoginIdState(String loginIdState) {
    this.loginIdState = loginIdState;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof LoginViewDTO)) {
      return false;
    }

    LoginViewDTO loginViewDTO = (LoginViewDTO) o;
    if (this.id == null) {
      return false;
    }
    return Objects.equals(this.id, loginViewDTO.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "LoginViewDTO{" +
            "id=" + getId() +
            ", userId='" + getUserId() + "'" +
            ", failCount=" + getFailCount() +
            ", lastLogin='" + getLastLogin() + "'" +
            ", bwdHash='" + getBwdHash() + "'" +
            ", bwdDate='" + getBwdDate() + "'" +
            ", loginIdState='" + getLoginIdState() + "'" +
            "}";
    }
}
