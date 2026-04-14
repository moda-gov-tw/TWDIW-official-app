package gov.moda.dw.manager.service.dto;

import gov.moda.dw.manager.domain.Nonce;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link Nonce} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class NonceDTO implements Serializable {

  private Long id;

  private String sId;

  private String nonceId;

  private String userId;

  private LocalDate createTime;

  private String captchaCode;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getsId() {
    return sId;
  }

  public void setsId(String sId) {
    this.sId = sId;
  }

  public String getNonceId() {
    return nonceId;
  }

  public void setNonceId(String nonceId) {
    this.nonceId = nonceId;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public LocalDate getCreateTime() {
    return createTime;
  }

  public void setCreateTime(LocalDate createTime) {
    this.createTime = createTime;
  }


    public String getCaptchaCode() {
        return captchaCode;
    }

    public void setCaptchaCode(String captchaCode) {
        this.captchaCode = captchaCode;
    }

    @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof NonceDTO)) {
      return false;
    }

    NonceDTO nonceDTO = (NonceDTO) o;
    if (this.id == null) {
      return false;
    }
    return Objects.equals(this.id, nonceDTO.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "NonceDTO{" +
            "id=" + getId() +
            ", sId='" + getsId() + "'" +
            ", nonceId='" + getNonceId() + "'" +
            ", userId='" + getUserId() + "'" +
            ", createTime='" + getCreateTime() + "'" +
            ", captchaCode='" + getCaptchaCode() + "'" +
            "}";
    }
}
