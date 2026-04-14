package gov.moda.dw.manager.service.dto.custom;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Dwvp401iResDTO {

    @JsonProperty("transactionId")
    private String transactionId;

    @JsonProperty("deepLink")
    private String deepLink;

}
