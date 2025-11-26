package gov.moda.dw.issuer.oidvci.service.dto;

import gov.moda.dw.issuer.oidvci.domain.Res;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link Res} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ResDTO implements Serializable {

  private Long id;

  @NotNull
  @Size(max = 20)
  @Schema(description = "功能類型", requiredMode = Schema.RequiredMode.REQUIRED)
  private String typeId;

  @NotNull
  @Size(max = 20)
  @Schema(description = "功能代碼", requiredMode = Schema.RequiredMode.REQUIRED)
  private String resId;

  @NotNull
  @Size(max = 20)
  @Schema(description = "功能群組", requiredMode = Schema.RequiredMode.REQUIRED)
  private String resGrp;

  @NotNull
  @Size(max = 50)
  @Schema(description = "功能名稱", requiredMode = Schema.RequiredMode.REQUIRED)
  private String resName;

  @Size(max = 255)
  @Schema(description = "功能描述")
  private String description;

  @NotNull
  @Size(max = 10)
  @Schema(description = "是否啟用", requiredMode = Schema.RequiredMode.REQUIRED)
  private String state;

  @NotNull
  @Size(max = 2048)
  @Schema(description = "URI", requiredMode = Schema.RequiredMode.REQUIRED)
  private String apiUri;

  @Size(max = 2048)
  @Schema(description = "URL")
  private String webUrl;

  @Size(max = 255)
  @Schema(description = "預留欄位1")
  private String dataRole1;

  @Size(max = 255)
  @Schema(description = "預留欄位2")
  private String dataRole2;

  @NotNull
  @Schema(description = "建立日", requiredMode = Schema.RequiredMode.REQUIRED)
  private Instant createTime;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTypeId() {
    return typeId;
  }

  public void setTypeId(String typeId) {
    this.typeId = typeId;
  }

  public String getResId() {
    return resId;
  }

  public void setResId(String resId) {
    this.resId = resId;
  }

  public String getResGrp() {
    return resGrp;
  }

  public void setResGrp(String resGrp) {
    this.resGrp = resGrp;
  }

  public String getResName() {
    return resName;
  }

  public void setResName(String resName) {
    this.resName = resName;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getApiUri() {
    return apiUri;
  }

  public void setApiUri(String apiUri) {
    this.apiUri = apiUri;
  }

  public String getWebUrl() {
    return webUrl;
  }

  public void setWebUrl(String webUrl) {
    this.webUrl = webUrl;
  }

  public String getDataRole1() {
    return dataRole1;
  }

//  public void setDataRole1(String dataRole1) {
//    this.dataRole1 = dataRole1;
//  }

  public String getDataRole2() {
    return dataRole2;
  }

//  public void setDataRole2(String dataRole2) {
//    this.dataRole2 = dataRole2;
//  }

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
    if (!(o instanceof ResDTO)) {
      return false;
    }

    ResDTO resDTO = (ResDTO) o;
    if (this.id == null) {
      return false;
    }
    return Objects.equals(this.id, resDTO.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "ResDTO{" +
            "id=" + getId() +
            ", typeId='" + getTypeId() + "'" +
            ", resId='" + getResId() + "'" +
            ", resGrp='" + getResGrp() + "'" +
            ", resName='" + getResName() + "'" +
            ", description='" + getDescription() + "'" +
            ", state='" + getState() + "'" +
            ", apiUri='" + getApiUri() + "'" +
            ", webUrl='" + getWebUrl() + "'" +
            ", dataRole1='" + getDataRole1() + "'" +
            ", dataRole2='" + getDataRole2() + "'" +
            ", createTime='" + getCreateTime() + "'" +
            "}";
    }
}
