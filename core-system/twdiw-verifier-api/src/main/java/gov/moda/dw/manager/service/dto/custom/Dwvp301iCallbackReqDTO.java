package gov.moda.dw.manager.service.dto.custom;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Dwvp301iCallbackReqDTO {

    @JsonProperty("code")
    private Integer code;

    @JsonProperty("verifyResult")
    private Boolean verifyResult;

    @JsonProperty("resultDescription")
    private String resultDescription;

    @JsonProperty("transactionId")
    private String transactionId;

    @JsonProperty("vpUid")
    private String vpUid;

    @JsonProperty("customData")
    private Object customData;

    @JsonProperty("data")
    private List<Dwvp301iCallbackDataDTO> data;

    @Getter
    @Setter
    public static class Dwvp301iCallbackDataDTO {

        @JsonProperty("credentialType")
        private String credentialType;

        @JsonProperty("claims")
        private Object claims;

    }

}
