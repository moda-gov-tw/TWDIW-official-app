package gov.moda.dw.manager.service.dto;

import gov.moda.dw.manager.domain.ResLayer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ResLayer} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ResLayerDTO implements Serializable {

  private Long id;

  @Schema(description = "父id")
  private Long parentId;

  @NotNull
  @Schema(description = "子id", requiredMode = Schema.RequiredMode.REQUIRED)
  private Long childId;

  @Size(max = 50)
  @Schema(description = "父代碼")
  private String parentCode;

  @Size(max = 50)
  @Schema(description = "子代碼")
  private String childCode;

  @NotNull
  @Schema(description = "建立日期時間", requiredMode = Schema.RequiredMode.REQUIRED)
  private Instant createTime;

  @Size(max = 10)
  @Schema(description = "排序")
  private String orderval;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getParentId() {
    return parentId;
  }

  public void setParentId(Long parentId) {
    this.parentId = parentId;
  }

  public Long getChildId() {
    return childId;
  }

  public void setChildId(Long childId) {
    this.childId = childId;
  }

  public String getParentCode() {
    return parentCode;
  }

  public void setParentCode(String parentCode) {
    this.parentCode = parentCode;
  }

  public String getChildCode() {
    return childCode;
  }

  public void setChildCode(String childCode) {
    this.childCode = childCode;
  }

  public Instant getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Instant createTime) {
    this.createTime = createTime;
  }

  public String getOrderval() {
    return orderval;
  }

  public void setOrderval(String orderval) {
    this.orderval = orderval;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ResLayerDTO)) {
      return false;
    }

    ResLayerDTO resLayerDTO = (ResLayerDTO) o;
    if (this.id == null) {
      return false;
    }
    return Objects.equals(this.id, resLayerDTO.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "ResLayerDTO{" +
            "id=" + getId() +
            ", parentId=" + getParentId() +
            ", childId=" + getChildId() +
            ", parentCode='" + getParentCode() + "'" +
            ", childCode='" + getChildCode() + "'" +
            ", createTime='" + getCreateTime() + "'" +
            ", orderval='" + getOrderval() + "'" +
            "}";
    }
}
