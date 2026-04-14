package gov.moda.dw.manager.service.dto.custom;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Ams351wResReqDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("accessToken")
    private String authKey;

}
