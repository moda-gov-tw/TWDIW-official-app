package gov.moda.dw.manager.service.dto.custom.ext.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class VPApiErrorResDTO {

    @JsonProperty("code")
    private String code;

    @JsonProperty("message")
    private String message;

}
