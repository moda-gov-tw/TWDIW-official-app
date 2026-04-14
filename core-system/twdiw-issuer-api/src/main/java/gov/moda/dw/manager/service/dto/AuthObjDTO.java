package gov.moda.dw.manager.service.dto;

import gov.moda.dw.manager.domain.AuthObj;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link AuthObj} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AuthObjDTO implements Serializable {

  private String id;

  @Schema(description = "user id")
  private Long userId;

  @Schema(description = "功能代碼")
  private String resCode;

  @Schema(description = "帳號")
  private String login;

  @Schema(description = "角色 id")
  private Long roleId;

  @Schema(description = "角色代碼")
  private String roleCode;

  @Schema(description = "角色名稱")
  private String roleName;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getResCode() {
    return resCode;
  }

  public void setResCode(String resCode) {
    this.resCode = resCode;
  }

  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public Long getRoleId() {
    return roleId;
  }

  public void setRoleId(Long roleId) {
    this.roleId = roleId;
  }

  public String getRoleCode() {
    return roleCode;
  }

  public void setRoleCode(String roleCode) {
    this.roleCode = roleCode;
  }

  public String getRoleName() {
    return roleName;
  }

  public void setRoleName(String roleName) {
    this.roleName = roleName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof AuthObjDTO)) {
      return false;
    }

    AuthObjDTO authObjDTO = (AuthObjDTO) o;
    if (this.id == null) {
      return false;
    }
    return Objects.equals(this.id, authObjDTO.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "AuthObjDTO{" +
            "id=" + getId() +
            ", userId=" + getUserId() +
            ", resCode='" + getResCode() + "'" +
            ", login='" + getLogin() + "'" +
            ", roleId=" + getRoleId() +
            ", roleCode='" + getRoleCode() + "'" +
            ", roleName='" + getRoleName() + "'" +
            "}";
    }
}
