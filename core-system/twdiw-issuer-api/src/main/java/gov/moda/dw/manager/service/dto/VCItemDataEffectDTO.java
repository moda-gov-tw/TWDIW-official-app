package gov.moda.dw.manager.service.dto;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VCItemDataEffectDTO {

    private String vcSerialNo;

    private String vcName;

    private String vcCidMask;

    private String vcCid;

    private Instant issuanceDate;

    private String status;

    private String statusName;

    private String orgId;

    private String orgTwName;

    private String credentialType;

    private Instant clearScheduleDatetime;

    private Long clearScheduleId;

    private Instant expiredDate;

    private Boolean isExpired;

    private String dataTag;

    private String transactionId;

}
