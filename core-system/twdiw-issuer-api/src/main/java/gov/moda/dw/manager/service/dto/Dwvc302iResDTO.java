package gov.moda.dw.manager.service.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Dwvc302iResDTO {

    @JsonProperty("action")
    private String action;

    @JsonProperty("success")
    private List<String> success;

    @JsonProperty("fail")
    private List<Dwvc302iFailResDTO> fail;

    @Getter
    @Setter
    public static class Dwvc302iFailResDTO {

        @JsonProperty("code")
        private String code;

        @JsonProperty("message")
        private String message;

        @JsonProperty("cids")
        private List<Dwvc302iFailCidResDTO> cids;

    }

    @Getter
    @Setter
    public static class Dwvc302iFailCidResDTO {

        @JsonProperty("cid")
        private String cid;

        @JsonProperty("credentialStatus")
        private String credentialStatus;

    }

}
