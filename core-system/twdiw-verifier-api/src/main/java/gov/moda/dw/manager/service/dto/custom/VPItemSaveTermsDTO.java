package gov.moda.dw.manager.service.dto.custom;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VPItemSaveTermsDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("serialNo")
    private String serialNo;

    @JsonProperty("terms")
    private String terms;

}
