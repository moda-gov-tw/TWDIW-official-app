package gov.moda.dw.manager.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * BwdHistory 密碼歷史
 */
@Entity
@Table(name = "bwd_history")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BwdHistory implements Serializable {

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
   * 建立時間
   */
  @NotNull
  @Column(name = "create_time", nullable = false)
  private Instant createTime;

  /**
   * 密碼Hash
   */
  @NotNull
  @Column(name = "bwd_hash", nullable = false)
  private String bwdHash;

  /**
   * 密碼Code
   */
  @Size(max = 500)
  @Column(name = "bwd_code", length = 500)
  private String bwdCode;

  // jhipster-needle-entity-add-field - JHipster will add fields here

  public Long getId() {
    return this.id;
  }

  public BwdHistory id(Long id) {
    this.setId(id);
    return this;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUserId() {
    return this.userId;
  }

  public BwdHistory userId(String userId) {
    this.setUserId(userId);
    return this;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public Instant getCreateTime() {
    return this.createTime;
  }

  public BwdHistory createTime(Instant createTime) {
    this.setCreateTime(createTime);
    return this;
  }

  public void setCreateTime(Instant createTime) {
    this.createTime = createTime;
  }

  public String getBwdHash() {
    return this.bwdHash;
  }

  public BwdHistory bwdHash(String bwdHash) {
    this.setBwdHash(bwdHash);
    return this;
  }

  public void setBwdHash(String bwdHash) {
    this.bwdHash = bwdHash;
  }

  public String getBwdCode() {
    return this.bwdCode;
  }

  public BwdHistory bwdCode(String bwdCode) {
    this.setBwdCode(bwdCode);
    return this;
  }

  public void setBwdCode(String bwdCode) {
    this.bwdCode = bwdCode;
  }

  // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof BwdHistory)) {
      return false;
    }
    return getId() != null && getId().equals(((BwdHistory) o).getId());
  }

  @Override
  public int hashCode() {
    // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
    return getClass().hashCode();
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "BwdHistory{" +
            "id=" + getId() +
            ", userId='" + getUserId() + "'" +
            ", createTime='" + getCreateTime() + "'" +
            ", bwdHash='" + getBwdHash() + "'" +
            ", bwdCode='" + getBwdCode() + "'" +
            "}";
    }
}
