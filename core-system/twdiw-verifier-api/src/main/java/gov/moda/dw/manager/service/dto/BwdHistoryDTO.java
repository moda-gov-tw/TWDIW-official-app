package gov.moda.dw.manager.service.dto;

import gov.moda.dw.manager.domain.BwdHistory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link BwdHistory} entity.
 */
@Schema(description = "BwdHistory 密碼歷史")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BwdHistoryDTO implements Serializable {

  private Long id;

  @NotNull
  @Size(max = 320)
  @Schema(description = "帳號", requiredMode = Schema.RequiredMode.REQUIRED)
  private String userId;

  @NotNull
  @Schema(description = "建立時間", requiredMode = Schema.RequiredMode.REQUIRED)
  private Instant createTime;

  @NotNull
  @Schema(description = "密碼Hash", requiredMode = Schema.RequiredMode.REQUIRED)
  private String bwdHash;

  @Size(max = 500)
  @Schema(description = "密碼Code")
  private String bwdCode;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public Instant getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Instant createTime) {
    this.createTime = createTime;
  }

  public String getBwdHash() {
    return bwdHash;
  }

  public void setBwdHash(String bwdHash) {
    this.bwdHash = bwdHash;
  }

  public String getBwdCode() {
    return bwdCode;
  }

  public void setBwdCode(String bwdCode) {
    this.bwdCode = bwdCode;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof BwdHistoryDTO)) {
      return false;
    }

    BwdHistoryDTO bwdHistoryDTO = (BwdHistoryDTO) o;
    if (this.id == null) {
      return false;
    }
    return Objects.equals(this.id, bwdHistoryDTO.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "BwdHistoryDTO{" +
            "id=" + getId() +
            ", userId='" + getUserId() + "'" +
            ", createTime='" + getCreateTime() + "'" +
            ", bwdHash='" + getBwdHash() + "'" +
            ", bwdCode='" + getBwdCode() + "'" +
            "}";
    }
}
