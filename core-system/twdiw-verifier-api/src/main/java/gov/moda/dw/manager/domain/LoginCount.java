package gov.moda.dw.manager.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * LoginCount 登入失敗次數
 */
@Entity
@Table(name = "login_count")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LoginCount implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
  @SequenceGenerator(name = "sequenceGenerator")
  @Column(name = "id")
  private Long id;

  /**
   * 帳號
   */
  @NotNull
  @Size(max = 320)
  @Column(name = "user_id", length = 320, nullable = false)
  private String userId;

  /**
   * 登入失敗次數(-1:強制重置密碼、n:失敗次數)
   */
  @NotNull
  @Column(name = "fail_count", nullable = false)
  private Integer failCount;

  /**
   * 修改時間
   */
  @NotNull
  @Column(name = "update_time", nullable = false)
  private Instant updateTime;

  // jhipster-needle-entity-add-field - JHipster will add fields here

  public Long getId() {
    return this.id;
  }

  public LoginCount id(Long id) {
    this.setId(id);
    return this;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUserId() {
    return this.userId;
  }

  public LoginCount userId(String userId) {
    this.setUserId(userId);
    return this;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public Integer getFailCount() {
    return this.failCount;
  }

  public LoginCount failCount(Integer failCount) {
    this.setFailCount(failCount);
    return this;
  }

  public void setFailCount(Integer failCount) {
    this.failCount = failCount;
  }

  public Instant getUpdateTime() {
    return this.updateTime;
  }

  public LoginCount updateTime(Instant updateTime) {
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
    if (!(o instanceof LoginCount)) {
      return false;
    }
    return getId() != null && getId().equals(((LoginCount) o).getId());
  }

  @Override
  public int hashCode() {
    // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
    return getClass().hashCode();
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "LoginCount{" +
            "id=" + getId() +
            ", userId='" + getUserId() + "'" +
            ", failCount=" + getFailCount() +
            ", updateTime='" + getUpdateTime() + "'" +
            "}";
    }
}
