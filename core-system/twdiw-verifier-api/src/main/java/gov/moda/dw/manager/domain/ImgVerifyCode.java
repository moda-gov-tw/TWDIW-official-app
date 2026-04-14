package gov.moda.dw.manager.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * A ImgVerifyCode.
 */
@Entity
@Table(name = "img_verify_code")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ImgVerifyCode implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
  @SequenceGenerator(name = "sequenceGenerator")
  @Column(name = "id")
  private Long id;

  /**
   * 圖形驗證碼
   */
  @Column(name = "verify_code")
  private String verifyCode;

  /**
   * 圖形驗證碼唯一碼
   */
  @Column(name = "verify_uni_id")
  private String verifyUniId;

  /**
   * 創建時間
   */
  @Column(name = "create_time")
  private Instant createTime;

  /**
   * 過期時間
   */
  @Column(name = "expire_time")
  private Instant expireTime;

  // jhipster-needle-entity-add-field - JHipster will add fields here

  public Long getId() {
    return this.id;
  }

  public ImgVerifyCode id(Long id) {
    this.setId(id);
    return this;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getVerifyCode() {
    return this.verifyCode;
  }

  public ImgVerifyCode verifyCode(String verifyCode) {
    this.setVerifyCode(verifyCode);
    return this;
  }

  public void setVerifyCode(String verifyCode) {
    this.verifyCode = verifyCode;
  }

  public String getVerifyUniId() {
    return this.verifyUniId;
  }

  public ImgVerifyCode verifyUniId(String verifyUniId) {
    this.setVerifyUniId(verifyUniId);
    return this;
  }

  public void setVerifyUniId(String verifyUniId) {
    this.verifyUniId = verifyUniId;
  }

  public Instant getCreateTime() {
    return this.createTime;
  }

  public ImgVerifyCode createTime(Instant createTime) {
    this.setCreateTime(createTime);
    return this;
  }

  public void setCreateTime(Instant createTime) {
    this.createTime = createTime;
  }

  public Instant getExpireTime() {
    return this.expireTime;
  }

  public ImgVerifyCode expireTime(Instant expireTime) {
    this.setExpireTime(expireTime);
    return this;
  }

  public void setExpireTime(Instant expireTime) {
    this.expireTime = expireTime;
  }

  // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ImgVerifyCode)) {
      return false;
    }
    return getId() != null && getId().equals(((ImgVerifyCode) o).getId());
  }

  @Override
  public int hashCode() {
    // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
    return getClass().hashCode();
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "ImgVerifyCode{" +
            "id=" + getId() +
            ", verifyCode='" + getVerifyCode() + "'" +
            ", verifyUniId='" + getVerifyUniId() + "'" +
            ", createTime='" + getCreateTime() + "'" +
            ", expireTime='" + getExpireTime() + "'" +
            "}";
    }
}
