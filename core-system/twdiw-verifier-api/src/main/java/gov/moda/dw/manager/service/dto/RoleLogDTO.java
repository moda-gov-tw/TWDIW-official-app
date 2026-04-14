package gov.moda.dw.manager.service.dto;

import gov.moda.dw.manager.domain.RoleLog;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link RoleLog} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RoleLogDTO implements Serializable {

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

  @NotNull
  @Size(max = 20)
  @Schema(description = "角色代碼", requiredMode = Schema.RequiredMode.REQUIRED)
  private String roleId;

  @NotNull
  @Size(max = 50)
  @Schema(description = "角色名稱", requiredMode = Schema.RequiredMode.REQUIRED)
  private String roleName;

  @Size(max = 255)
  @Schema(description = "描述")
  private String description;

  @NotNull
  @Size(max = 10)
  @Schema(description = "狀態", requiredMode = Schema.RequiredMode.REQUIRED)
  private String state;

  @Size(max = 255)
  @Schema(description = "預留欄位1")
  private String dataRole1;

  @Size(max = 255)
  @Schema(description = "預留欄位2")
  private String dataRole2;

  @NotNull
  @Schema(description = "角色建立時間", requiredMode = Schema.RequiredMode.REQUIRED)
  private Instant createTime;

  @Schema(description = "上次權限修改時間")
  private Instant authChangeTime;

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

  public String getRoleId() {
    return roleId;
  }

  public void setRoleId(String roleId) {
    this.roleId = roleId;
  }

  public String getRoleName() {
    return roleName;
  }

  public void setRoleName(String roleName) {
    this.roleName = roleName;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
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

  public Instant getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Instant createTime) {
    this.createTime = createTime;
  }

  public Instant getAuthChangeTime() { return authChangeTime; }

  public void setAuthChangeTime(Instant authChangeTime) { this.authChangeTime = authChangeTime; }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof RoleLogDTO)) {
      return false;
    }

    RoleLogDTO roleLogDTO = (RoleLogDTO) o;
    if (this.id == null) {
      return false;
    }
    return Objects.equals(this.id, roleLogDTO.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "RoleLogDTO{" +
            "id=" + getId() +
            ", actor='" + getActor() + "'" +
            ", logType='" + getLogType() + "'" +
            ", logTime='" + getLogTime() + "'" +
            ", roleId='" + getRoleId() + "'" +
            ", roleName='" + getRoleName() + "'" +
            ", description='" + getDescription() + "'" +
            ", state='" + getState() + "'" +
            ", dataRole1='" + getDataRole1() + "'" +
            ", dataRole2='" + getDataRole2() + "'" +
            ", createTime='" + getCreateTime() + "'" +
            ", authChangeTime='" + getAuthChangeTime() + "'" +
            "}";
    }
}
