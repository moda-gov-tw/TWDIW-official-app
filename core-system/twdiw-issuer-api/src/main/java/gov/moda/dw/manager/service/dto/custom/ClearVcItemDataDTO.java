package gov.moda.dw.manager.service.dto.custom;

import java.time.Instant;

public interface ClearVcItemDataDTO {

    String getSerialNo();

    String getName();

    String getCid();

    Instant getIssuanceDate();

    Long getId();

    String getContent();

    String getPureContent();

    Long getCrUser();

    Instant getCrDatetime();

    Integer getValid();

    Long getClearScheduleId();

    Instant getClearScheduleDatetime();

    String getVcCid();

    String getTransactionId();

    String getBusinessId();

    String getVcItemName();

    String getQrCode();

    Instant getExpired();

    String getScheduleRevokeMessage();

}
