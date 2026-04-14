package gov.moda.dw.manager.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link gov.moda.dw.manager.domain.VCDataStatusLog} entity.
 */
@Schema(description = "VCDataStatusLog VC 資料狀態紀錄")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VCDataStatusLogDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 100)
    @Schema(description = "VC 憑證編號", requiredMode = Schema.RequiredMode.REQUIRED)
    private String vcCid;

    @NotNull
    @Schema(description = "vc_Item_Id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long vcItemId;

    @NotNull
    @Size(max = 50)
    @Schema(description = "交易序號", requiredMode = Schema.RequiredMode.REQUIRED)
    private String transactionId;

    @NotNull
    @Schema(description = "狀態 [0: ACTIVE(有效)、1: SUSPENDED(停用)、2: REVOKED(撤銷)、301: INACTIVE(沒掃 QR Code)]", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer valid;

    @NotNull
    @Schema(description = "建立者", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long crUser;

    @NotNull
    @Schema(description = "建立時間", requiredMode = Schema.RequiredMode.REQUIRED)
    private Instant crDatetime;

    @Schema(description = "最後更新時間")
    private Instant lastUpdateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVcCid() {
        return vcCid;
    }

    public void setVcCid(String vcCid) {
        this.vcCid = vcCid;
    }

    public Long getVcItemId() {
        return vcItemId;
    }

    public void setVcItemId(Long vcItemId) {
        this.vcItemId = vcItemId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getValid() {
        return valid;
    }

    public void setValid(Integer valid) {
        this.valid = valid;
    }

    public Long getCrUser() {
        return crUser;
    }

    public void setCrUser(Long crUser) {
        this.crUser = crUser;
    }

    public Instant getCrDatetime() {
        return crDatetime;
    }

    public void setCrDatetime(Instant crDatetime) {
        this.crDatetime = crDatetime;
    }

    public Instant getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Instant lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VCDataStatusLogDTO)) {
            return false;
        }

        VCDataStatusLogDTO vCDataStatusLogDTO = (VCDataStatusLogDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, vCDataStatusLogDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VCDataStatusLogDTO{" + "id=" + getId() + ", vcCid='" + getVcCid() + "'" + ", vcItemId=" + getVcItemId()
                + ", transactionId='" + getTransactionId() + "'" + ", valid=" + getValid() + ", crUser=" + getCrUser()
                + ", crDatetime='" + getCrDatetime() + "'" + ", lastUpdateTime='" + getLastUpdateTime() + "'" + "}";
    }
}
