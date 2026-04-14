package gov.moda.dw.manager.service.dto.custom;

import java.time.Instant;

public interface VCCredentialRevokeVcItemDataDTO {

    String getCid();

    Instant getLastUpdateTime();

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
