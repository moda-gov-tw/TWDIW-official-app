package gov.moda.dw.manager.service.dto.custom;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VCItemStopIssuingReqDTO {

    @JsonProperty("id")
    private Long id;

}
