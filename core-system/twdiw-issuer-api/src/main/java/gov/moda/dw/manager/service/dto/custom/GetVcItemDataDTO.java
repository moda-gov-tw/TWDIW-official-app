package gov.moda.dw.manager.service.dto.custom;

import java.time.Instant;

public interface GetVcItemDataDTO {

    String getDataTag();

    String getCid();

    String getCredentialType();

    Instant getIssuanceDate();

    Instant getExpirationDate();

    String getCredentialStatus();

}
