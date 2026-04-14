package gov.moda.dw.manager.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Dwvc203iReqDTO {

    @JsonProperty("dataTag")
    private String dataTag;

    @JsonProperty("vcUid")
    private String vcUid;

    @JsonProperty("credentialStatus")
    private String credentialStatus;

    @JsonProperty("page")
    private Integer page = 0;

    @JsonProperty("size")
    private Integer size = 10;

}
