package gov.moda.dw.manager.service.dto;

import gov.moda.dw.manager.domain.ImgVerifyCode;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ImgVerifyCode} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ImgVerifyCodeDTO implements Serializable {

  private Long id;

  @Schema(description = "圖形驗證碼")
  private String verifyCode;

  @Schema(description = "圖形驗證碼唯一碼")
  private String verifyUniId;

  @Schema(description = "創建時間")
  private Instant createTime;

  @Schema(description = "過期時間")
  private Instant expireTime;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getVerifyCode() {
    return verifyCode;
  }

  public void setVerifyCode(String verifyCode) {
    this.verifyCode = verifyCode;
  }

  public String getVerifyUniId() {
    return verifyUniId;
  }

  public void setVerifyUniId(String verifyUniId) {
    this.verifyUniId = verifyUniId;
  }

  public Instant getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Instant createTime) {
    this.createTime = createTime;
  }

  public Instant getExpireTime() {
    return expireTime;
  }

  public void setExpireTime(Instant expireTime) {
    this.expireTime = expireTime;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ImgVerifyCodeDTO)) {
      return false;
    }

    ImgVerifyCodeDTO imgVerifyCodeDTO = (ImgVerifyCodeDTO) o;
    if (this.id == null) {
      return false;
    }
    return Objects.equals(this.id, imgVerifyCodeDTO.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "ImgVerifyCodeDTO{" +
            "id=" + getId() +
            ", verifyCode='" + getVerifyCode() + "'" +
            ", verifyUniId='" + getVerifyUniId() + "'" +
            ", createTime='" + getCreateTime() + "'" +
            ", expireTime='" + getExpireTime() + "'" +
            "}";
    }
}
