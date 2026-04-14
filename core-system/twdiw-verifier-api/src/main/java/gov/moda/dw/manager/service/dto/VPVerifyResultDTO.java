package gov.moda.dw.manager.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link gov.moda.dw.manager.domain.VPVerifyResult} entity.
 */
@Schema(description = "VPVerifyResult VP驗證結果")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VPVerifyResultDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 50)
    @Schema(description = "交易序號", requiredMode = Schema.RequiredMode.REQUIRED)
    private String transactionId;

    @Schema(description = "VC資料")
    @Lob
    private String payload;

    @Schema(description = "VP id 外鍵")
    private Long vpItemId;

    @Schema(description = "建立時間")
    private Instant crDatetime;

    @Schema(description = "驗證時間")
    private Instant verifyDatetime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Long getVpItemId() {
        return vpItemId;
    }

    public void setVpItemId(Long vpItemId) {
        this.vpItemId = vpItemId;
    }

    public Instant getCrDatetime() {
        return crDatetime;
    }

    public void setCrDatetime(Instant crDatetime) {
        this.crDatetime = crDatetime;
    }

    public Instant getVerifyDatetime() {
        return verifyDatetime;
    }

    public void setVerifyDatetime(Instant verifyDatetime) {
        this.verifyDatetime = verifyDatetime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VPVerifyResultDTO)) {
            return false;
        }

        VPVerifyResultDTO vPVerifyResultDTO = (VPVerifyResultDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, vPVerifyResultDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VPVerifyResultDTO{" +
            "id=" + getId() +
            ", transactionId='" + getTransactionId() + "'" +
            ", payload='" + getPayload() + "'" +
            ", vpItemId=" + getVpItemId() +
            ", crDatetime='" + getCrDatetime() + "'" +
            ", verifyDatetime='" + getVerifyDatetime() + "'" +
            "}";
    }
}
