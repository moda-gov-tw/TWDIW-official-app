package gov.moda.dw.manager.service.dto.custom;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VCCredentialQueryDTO {

    @JsonProperty("orgId")
    private String orgId;

    @JsonProperty("vcSerialNo")
    private String vcSerialNo;

    @JsonProperty("issuanceDateStart")
    private Instant issuanceDateStart;

    @JsonProperty("issuanceDateEnd")
    private Instant issuanceDateEnd;

    @JsonProperty("crUserId")
    private Long crUserId;

    @JsonProperty("credentialStatus")
    private String credentialStatus;

    @JsonProperty("sortType")
    private String sortType;

    @JsonProperty("dataTag")
    private String dataTag;

    @JsonProperty("transactionId")
    private String transactionId;

}
