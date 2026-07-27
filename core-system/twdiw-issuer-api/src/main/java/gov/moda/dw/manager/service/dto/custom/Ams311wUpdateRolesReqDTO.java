package gov.moda.dw.manager.service.dto.custom;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.moda.dw.manager.service.dto.RoleDTO;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Ams311wUpdateRolesReqDTO {

  @JsonProperty("userDTO")
  private Ams311wReqDTO userDTO;

  @JsonProperty("roleDTOList")
  private List<RoleDTO> authCategories;
}
