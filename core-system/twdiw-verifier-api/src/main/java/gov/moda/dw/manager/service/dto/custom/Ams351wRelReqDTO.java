package gov.moda.dw.manager.service.dto.custom;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import gov.moda.dw.manager.service.dto.RelDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Ams351wRelReqDTO {

    @JsonProperty("relDTOList")
    private List<RelDTO> relDTOList;

    @JsonProperty("accessToken")
    private String authKey;

}
