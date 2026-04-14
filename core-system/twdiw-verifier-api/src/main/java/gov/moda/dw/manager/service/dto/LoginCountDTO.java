package gov.moda.dw.manager.service.dto;

import gov.moda.dw.manager.domain.LoginCount;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link LoginCount} entity.
 */
@Schema(description = "LoginCount 登入失敗次數")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LoginCountDTO implements Serializable {

  private Long id;

  @NotNull
  @Size(max = 320)
  @Schema(description = "帳號", requiredMode = Schema.RequiredMode.REQUIRED)
  private String userId;

  @NotNull
  @Schema(description = "登入失敗次數(-1:強制重置密碼、n:失敗次數)", requiredMode = Schema.RequiredMode.REQUIRED)
  private Integer failCount;

  @NotNull
  @Schema(description = "修改時間", requiredMode = Schema.RequiredMode.REQUIRED)
  private Instant updateTime;

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
    if (!(o instanceof LoginCountDTO)) {
      return false;
    }

    LoginCountDTO loginCountDTO = (LoginCountDTO) o;
    if (this.id == null) {
      return false;
    }
    return Objects.equals(this.id, loginCountDTO.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "LoginCountDTO{" +
            "id=" + getId() +
            ", userId='" + getUserId() + "'" +
            ", failCount=" + getFailCount() +
            ", updateTime='" + getUpdateTime() + "'" +
            "}";
    }
}
