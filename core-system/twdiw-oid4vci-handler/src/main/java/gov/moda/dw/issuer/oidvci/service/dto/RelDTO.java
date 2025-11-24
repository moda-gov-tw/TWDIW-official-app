package gov.moda.dw.issuer.oidvci.service.dto;

import gov.moda.dw.issuer.oidvci.domain.Rel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link Rel} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RelDTO implements Serializable {

  private Long id;

  @NotNull
  @Size(max = 20)
  @Schema(description = "左值table(Table 代碼) UK", requiredMode = Schema.RequiredMode.REQUIRED)
  private String leftTbl;

  @NotNull
  @Schema(description = "左值id(Table 的id) UK", requiredMode = Schema.RequiredMode.REQUIRED)
  private Long leftId;

  @NotNull
  @Size(max = 20)
  @Schema(description = "右值table(Table 代碼) UK", requiredMode = Schema.RequiredMode.REQUIRED)
  private String rightTbl;

  @NotNull
  @Schema(description = "右值id(Table 的id) UK", requiredMode = Schema.RequiredMode.REQUIRED)
  private Long rightId;

  @Size(max = 320)
  @Schema(description = "左值代碼")
  private String leftCode;

  @Size(max = 50)
  @Schema(description = "右值代碼")
  private String rightCode;

  @NotNull
  @Size(max = 10)
  @Schema(description = "狀態", requiredMode = Schema.RequiredMode.REQUIRED)
  private String state;

  @Size(max = 255)
  @Schema(description = "預留欄位1")
  private String dataRole1;

  @Size(max = 255)
  @Schema(description = "預留欄位2")
  private String dataRole2;

  @Size(max = 64)
  @Schema(description = "受控項")
  private String dataAuth;

  @NotNull
  @Schema(description = "建立日", requiredMode = Schema.RequiredMode.REQUIRED)
  private Instant createTime;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getLeftTbl() {
    return leftTbl;
  }

  public void setLeftTbl(String leftTbl) {
    this.leftTbl = leftTbl;
  }

  public Long getLeftId() {
    return leftId;
  }

  public void setLeftId(Long leftId) {
    this.leftId = leftId;
  }

  public String getRightTbl() {
    return rightTbl;
  }

  public void setRightTbl(String rightTbl) {
    this.rightTbl = rightTbl;
  }

  public Long getRightId() {
    return rightId;
  }

  public void setRightId(Long rightId) {
    this.rightId = rightId;
  }

  public String getLeftCode() {
    return leftCode;
  }

  public void setLeftCode(String leftCode) {
    this.leftCode = leftCode;
  }

  public String getRightCode() {
    return rightCode;
  }

  public void setRightCode(String rightCode) {
    this.rightCode = rightCode;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getDataRole1() {
    return dataRole1;
  }

  public void setDataRole1(String dataRole1) {
    this.dataRole1 = dataRole1;
  }

  public String getDataRole2() {
    return dataRole2;
  }

  public void setDataRole2(String dataRole2) {
    this.dataRole2 = dataRole2;
  }

  public String getDataAuth() {
    return dataAuth;
  }

  public void setDataAuth(String dataAuth) {
    this.dataAuth = dataAuth;
  }

  public Instant getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Instant createTime) {
    this.createTime = createTime;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof RelDTO)) {
      return false;
    }

    RelDTO relDTO = (RelDTO) o;
    if (this.id == null) {
      return false;
    }
    return Objects.equals(this.id, relDTO.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "RelDTO{" +
            "id=" + getId() +
            ", leftTbl='" + getLeftTbl() + "'" +
            ", leftId=" + getLeftId() +
            ", rightTbl='" + getRightTbl() + "'" +
            ", rightId=" + getRightId() +
            ", leftCode='" + getLeftCode() + "'" +
            ", rightCode='" + getRightCode() + "'" +
            ", state='" + getState() + "'" +
            ", dataRole1='" + getDataRole1() + "'" +
            ", dataRole2='" + getDataRole2() + "'" +
            ", dataAuth='" + getDataAuth() + "'" +
            ", createTime='" + getCreateTime() + "'" +
            "}";
    }
}
