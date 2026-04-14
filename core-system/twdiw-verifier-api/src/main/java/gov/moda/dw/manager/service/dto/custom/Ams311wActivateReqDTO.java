package gov.moda.dw.manager.service.dto.custom;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Ams311wActivateReqDTO {

    @JsonProperty("activationKey")
    private String activationKey;

    @JsonProperty("resetKey")
    private String resetKey;

    @JsonProperty("newPassword")
    private String newBwd;

}
