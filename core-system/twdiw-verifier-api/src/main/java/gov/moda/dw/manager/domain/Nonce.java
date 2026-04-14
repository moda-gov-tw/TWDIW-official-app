package gov.moda.dw.manager.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * A Nonce.
 */
@Entity
@Table(name = "nonce")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Nonce implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
  @SequenceGenerator(name = "sequenceGenerator")
  @Column(name = "id")
  private Long id;

  @Column(name = "s_id")
  private String sId;

  @Column(name = "nonce_id")
  private String nonceId;

  @Column(name = "user_id")
  private String userId;

  @Column(name = "create_time")
  private LocalDate createTime;

  @Column(name = "captcha_code")
  private String captchaCode;

  // jhipster-needle-entity-add-field - JHipster will add fields here

  public Long getId() {
    return this.id;
  }

  public Nonce id(Long id) {
    this.setId(id);
    return this;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getsId() {
    return this.sId;
  }

  public Nonce sId(String sId) {
    this.setsId(sId);
    return this;
  }

  public void setsId(String sId) {
    this.sId = sId;
  }

  public String getNonceId() {
    return this.nonceId;
  }

  public Nonce nonceId(String nonceId) {
    this.setNonceId(nonceId);
    return this;
  }

  public void setNonceId(String nonceId) {
    this.nonceId = nonceId;
  }

  public String getUserId() {
    return this.userId;
  }

  public Nonce userId(String userId) {
    this.setUserId(userId);
    return this;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public LocalDate getCreateTime() {
    return this.createTime;
  }

  public Nonce createTime(LocalDate createTime) {
    this.setCreateTime(createTime);
    return this;
  }

  public void setCreateTime(LocalDate createTime) {
    this.createTime = createTime;
  }

  public String getCaptchaCode() {
    return this.captchaCode;
  }

  public Nonce captchaCode(String captchaCode) {
    this.setCaptchaCode(captchaCode);
    return this;
  }

  public void setCaptchaCode(String captchaCode) {
    this.captchaCode = captchaCode;
  }

  // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Nonce)) {
      return false;
    }
    return getId() != null && getId().equals(((Nonce) o).getId());
  }

  @Override
  public int hashCode() {
    // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
    return getClass().hashCode();
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "Nonce{" +
            "id=" + getId() +
            ", sId='" + getsId() + "'" +
            ", nonceId='" + getNonceId() + "'" +
            ", userId='" + getUserId() + "'" +
            ", createTime='" + getCreateTime() + "'" +
            ", captchaCode='" + getCaptchaCode() + "'" +
            "}";
    }
}
