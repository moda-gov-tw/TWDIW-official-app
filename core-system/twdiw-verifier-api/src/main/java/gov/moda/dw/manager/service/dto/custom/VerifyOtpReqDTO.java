package gov.moda.dw.manager.service.dto.custom;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyOtpReqDTO {

    @JsonProperty("email")
    private String email;

    @JsonProperty("otpToken")
    private String otpToken;

}
