package gov.moda.dw.manager.service.dto.custom;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;


@Schema(description = "VPItemVerifyResultDTO")
public class VPItemVerifyResultDTO {

    @Deprecated
    Long vpItemId;

    String transactionId;

    String payload;

    public VPItemVerifyResultDTO(){}

    public VPItemVerifyResultDTO(Long vpItemId, String transactionId, String payload) {
        this.vpItemId = vpItemId;
        this.transactionId = transactionId;
        this.payload = payload;
    }

    public Long getVpItemId() {
        return vpItemId;
    }

    public void setVpItemId(Long vpItemId) {
        this.vpItemId = vpItemId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VPItemVerifyResultDTO that = (VPItemVerifyResultDTO) o;
        return Objects.equals(vpItemId, that.vpItemId) && Objects.equals(transactionId, that.transactionId) && Objects.equals(payload, that.payload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vpItemId, transactionId, payload);
    }
}
