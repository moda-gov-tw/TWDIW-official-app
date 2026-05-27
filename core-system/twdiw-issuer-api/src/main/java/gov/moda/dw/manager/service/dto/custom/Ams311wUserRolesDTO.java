package gov.moda.dw.manager.service.dto.custom;

import gov.moda.dw.manager.service.dto.RoleDTO;
import gov.moda.dw.manager.type.StatusCode;
import java.util.List;
import lombok.Data;

@Data
public class Ams311wUserRolesDTO {

  private List<RoleDTO> userRolesData;

  private StatusCode statusCode;
}
