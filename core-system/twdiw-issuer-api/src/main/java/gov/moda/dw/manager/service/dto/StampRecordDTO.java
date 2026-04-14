package gov.moda.dw.manager.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link gov.moda.dw.manager.domain.StampRecord} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StampRecordDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 255)
    @Schema(description = "印章名稱", requiredMode = Schema.RequiredMode.REQUIRED)
    private String stampName;

    @Size(max = 255)
    @Schema(description = "擁有者")
    private String ownerId;

    @NotNull
    @Size(max = 255)
    @Schema(description = "圖片位置", requiredMode = Schema.RequiredMode.REQUIRED)
    private String imagePath;

    @Size(max = 255)
    @Schema(description = "圖片類型")
    private String imageType;

    @NotNull
    @Size(max = 255)
    @Schema(description = "狀態", requiredMode = Schema.RequiredMode.REQUIRED)
    private String status;

    @Size(max = 255)
    @Schema(description = "備註")
    private String remark;

    @Size(max = 255)
    @Schema(description = "修改者")
    private String modifierId;

    @Size(max = 255)
    @Schema(description = "建立者")
    private String createId;

    @Schema(description = "最後編輯時間")
    private Instant modifyTime;

    @Schema(description = "創建時間")
    private Instant createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStampName() {
        return stampName;
    }

    public void setStampName(String stampName) {
        this.stampName = stampName;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getModifierId() {
        return modifierId;
    }

    public void setModifierId(String modifierId) {
        this.modifierId = modifierId;
    }

    public String getCreateId() {
        return createId;
    }

    public void setCreateId(String createId) {
        this.createId = createId;
    }

    public Instant getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Instant modifyTime) {
        this.modifyTime = modifyTime;
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
        if (!(o instanceof StampRecordDTO)) {
            return false;
        }

        StampRecordDTO stampRecordDTO = (StampRecordDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, stampRecordDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StampRecordDTO{" +
            "id=" + getId() +
            ", stampName='" + getStampName() + "'" +
            ", ownerId='" + getOwnerId() + "'" +
            ", imagePath='" + getImagePath() + "'" +
            ", imageType='" + getImageType() + "'" +
            ", status='" + getStatus() + "'" +
            ", remark='" + getRemark() + "'" +
            ", modifierId='" + getModifierId() + "'" +
            ", createId='" + getCreateId() + "'" +
            ", modifyTime='" + getModifyTime() + "'" +
            ", createTime='" + getCreateTime() + "'" +
            "}";
    }
}
