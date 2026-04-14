package gov.moda.dw.manager.service.dto.custom;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import gov.moda.dw.manager.service.dto.RoleDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ams311wAllRoleResDTO implements Serializable {

  private List<RoleDTO> roleDTOList;
}
