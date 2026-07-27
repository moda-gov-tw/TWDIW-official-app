package gov.moda.dw.manager.service.dto.custom;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.moda.dw.manager.service.dto.RelDTO;
import java.util.List;
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
