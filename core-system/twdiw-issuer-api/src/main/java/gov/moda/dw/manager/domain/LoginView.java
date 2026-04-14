package gov.moda.dw.manager.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * A LoginView.
 */
@Entity
@Table(name = "login_view")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LoginView implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
  @SequenceGenerator(name = "sequenceGenerator")
  @Column(name = "id")
  private Long id;

  /**
   * 帳號
   */
  @Column(name = "user_id")
  private String userId;

  /**
   * 登入失敗數
   */
  @Column(name = "fail_count")
  private Integer failCount;

  /**
   * 最後登入時間
   */
  @Column(name = "last_login")
  private Instant lastLogin;

  /**
   * 密碼Hash
   */
  @Column(name = "bwd_hash")
  private String bwdHash;

  /**
   * 密碼異動時間
   */
  @Column(name = "bwd_date")
  private Instant bwdDate;

  /**
   * 帳號狀態
   */
  @Column(name = "login_id_state")
  private String loginIdState;

  // jhipster-needle-entity-add-field - JHipster will add fields here

  public Long getId() {
    return this.id;
  }

  public LoginView id(Long id) {
    this.setId(id);
    return this;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUserId() {
    return this.userId;
  }

  public LoginView userId(String userId) {
    this.setUserId(userId);
    return this;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public Integer getFailCount() {
    return this.failCount;
  }

  public LoginView failCount(Integer failCount) {
    this.setFailCount(failCount);
    return this;
  }

  public void setFailCount(Integer failCount) {
    this.failCount = failCount;
  }

  public Instant getLastLogin() {
    return this.lastLogin;
  }

  public LoginView lastLogin(Instant lastLogin) {
    this.setLastLogin(lastLogin);
    return this;
  }

  public void setLastLogin(Instant lastLogin) {
    this.lastLogin = lastLogin;
  }

  public String getBwdHash() {
    return this.bwdHash;
  }

  public LoginView bwdHash(String bwdHash) {
    this.setBwdHash(bwdHash);
    return this;
  }

  public void setBwdHash(String bwdHash) {
    this.bwdHash = bwdHash;
  }

  public Instant getBwdDate() {
    return this.bwdDate;
  }

  public LoginView bwdDate(Instant bwdDate) {
    this.setBwdDate(bwdDate);
    return this;
  }

  public void setBwdDate(Instant bwdDate) {
    this.bwdDate = bwdDate;
  }

  public String getLoginIdState() {
    return this.loginIdState;
  }

  public LoginView loginIdState(String loginIdState) {
    this.setLoginIdState(loginIdState);
    return this;
  }

  public void setLoginIdState(String loginIdState) {
    this.loginIdState = loginIdState;
  }

  // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof LoginView)) {
      return false;
    }
    return getId() != null && getId().equals(((LoginView) o).getId());
  }

  @Override
  public int hashCode() {
    // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
    return getClass().hashCode();
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "LoginView{" +
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
