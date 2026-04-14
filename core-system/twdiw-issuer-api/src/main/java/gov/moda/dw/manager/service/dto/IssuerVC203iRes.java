package gov.moda.dw.manager.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IssuerVC203iRes {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("code")
    private String code;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("message")
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("info")
    private IssuerVC203iInfoRes info;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("credentialStatus")
    private String credentialStatus;

    @Getter
    @Setter
    public static class IssuerVC203iInfoRes {

        @JsonProperty("cid")
        private String cid;

    }

}
