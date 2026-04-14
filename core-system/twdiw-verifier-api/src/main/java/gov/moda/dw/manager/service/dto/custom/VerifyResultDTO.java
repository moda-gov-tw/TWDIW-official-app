package gov.moda.dw.manager.service.dto.custom;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyResultDTO {

    @JsonProperty("holder_did")
    public String holderDid;

    @JsonProperty("verifyResult")
    public boolean verifyResult;

    @JsonProperty("resultDescription")
    public String resultDescription;

    @JsonProperty("transactionId")
    public String transactionId;

    @JsonProperty("data")
    public List<VerifyResultDataDTO> data;

    @Getter
    @Setter
    public static class VerifyResultDataDTO {

        @JsonProperty("credentialType")
        public String credentialType;

        @JsonProperty("claims")
        public List<VerifyResultDataClaimDTO> claims;

    }

    @Getter
    @Setter
    public static class VerifyResultDataClaimDTO {

        @JsonProperty("ename")
        public String ename;

        @JsonProperty("cname")
        public String cname;

        @JsonProperty("value")
        public String value;

    }

}
