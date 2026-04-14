package gov.moda.dw.manager.domain;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * A AuthObj.
 */
@Entity
@Table(name = "auth_obj")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AuthObj implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
  @SequenceGenerator(name = "sequenceGenerator")
  @Column(name = "id")
  private String id;

  /**
   * user id
   */
  @Column(name = "user_id")
  private Long userId;

  /**
   * 功能代碼
   */
  @Column(name = "res_code")
  private String resCode;

  /**
   * 帳號
   */
  @Column(name = "login")
  private String login;

  /**
   * 角色 id
   */
  @Column(name = "role_id")
  private Long roleId;

  /**
   * 角色代碼
   */
  @Column(name = "role_code")
  private String roleCode;

  /**
   * 角色名稱
   */
  @Column(name = "role_name")
  private String roleName;

  // jhipster-needle-entity-add-field - JHipster will add fields here

  public String getId() {
    return this.id;
  }

  public AuthObj id(String id) {
    this.setId(id);
    return this;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Long getUserId() {
    return this.userId;
  }

  public AuthObj userId(Long userId) {
    this.setUserId(userId);
    return this;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getResCode() {
    return this.resCode;
  }

  public AuthObj resCode(String resCode) {
    this.setResCode(resCode);
    return this;
  }

  public void setResCode(String resCode) {
    this.resCode = resCode;
  }

  public String getLogin() {
    return this.login;
  }

  public AuthObj login(String login) {
    this.setLogin(login);
    return this;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public Long getRoleId() {
    return this.roleId;
  }

  public AuthObj roleId(Long roleId) {
    this.setRoleId(roleId);
    return this;
  }

  public void setRoleId(Long roleId) {
    this.roleId = roleId;
  }

  public String getRoleCode() {
    return this.roleCode;
  }

  public AuthObj roleCode(String roleCode) {
    this.setRoleCode(roleCode);
    return this;
  }

  public void setRoleCode(String roleCode) {
    this.roleCode = roleCode;
  }

  public String getRoleName() {
    return this.roleName;
  }

  public AuthObj roleName(String roleName) {
    this.setRoleName(roleName);
    return this;
  }

  public void setRoleName(String roleName) {
    this.roleName = roleName;
  }

  // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof AuthObj)) {
      return false;
    }
    return getId() != null && getId().equals(((AuthObj) o).getId());
  }

  @Override
  public int hashCode() {
    // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
    return getClass().hashCode();
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "AuthObj{" +
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
