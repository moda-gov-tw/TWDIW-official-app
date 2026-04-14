package gov.moda.dw.manager.service.dto.custom;

import java.util.List;
import lombok.Data;
import gov.moda.dw.manager.service.dto.RoleDTO;
import gov.moda.dw.manager.type.StatusCode;

@Data
public class Ams311wUserRolesDTO {

  private List<RoleDTO> userRolesData;

  private StatusCode statusCode;
}
