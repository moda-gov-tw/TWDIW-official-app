package gov.moda.dw.manager.service.dto.custom;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Dwvp301iCallbackResDTO {

    @JsonProperty("code")
    private String code;

    @JsonProperty("message")
    private String message;

}
