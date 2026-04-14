package gov.moda.dw.manager.service.dto.custom;

import java.time.Instant;

public interface GetVcCredentialDTO {

    String getCid();

    String getCredentialType();

    Instant getIssuanceDate();

    Instant getExpirationDate();

    String getCredentialStatus();

}
