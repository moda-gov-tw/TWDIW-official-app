package gov.moda.dw.manager.service.dto;

import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;

public interface VCCredentialDTO {
    @Value("#{target.cid}")
    String getCid();

    @Value("#{target.credentialType}")
    String getCredentialType();

    @Value("#{target.credentialSubjectId}")
    String getCredentialSubjectId();

    @Value("#{target.issuanceDate}")
    Instant getIssuanceDate();

    @Value("#{target.expirationDate}")
    Instant getExpirationDate();

    @Value("#{target.content}")
    String getContent();

    @Value("#{target.ticketNumber}")
    Long getTicketNumber();

    @Value("#{target.lastUpdateTime}")
    Instant getLastUpdateTime();

    @Value("#{target.credentialStatus}")
    String getCredentialStatus();

    @Value("#{target.nonce}")
    String getNonce();

    @Value("#{target.name}")
    String getName();

    @Value("#{target.org_id}")
    String getOrgId();

    @Value("#{target.org_tw_name}")
    String getOrgTwName();

    @Value("#{target.serial_no}")
    String getSerialNo();

    @Value("#{target.clear_schedule_datetime}")
    Instant getClearScheduleDatetime();

    @Value("#{target.clear_schedule_id}")
    Long getClearScheduleId();

    @Value("#{target.valid}")
    Long getValid();

    @Value("#{target.expired}")
    Instant getExpiredDate();

    @Value("#{target.data_tag}")
    String getDataTag();

    @Value("#{target.transaction_id}")
    String getTransactionId();

}
