package gov.moda.dw.manager.service.dto.custom;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtHeaderDTO {

    @JsonProperty("alg")
    private String alg;

    @JsonProperty("typ")
    private String typ;

    @JsonProperty("did")
    private String did;

}
