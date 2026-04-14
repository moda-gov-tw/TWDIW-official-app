package gov.moda.dw.manager.service.dto.custom;

import java.util.List;
import lombok.Data;
import gov.moda.dw.manager.service.dto.RoleLogDTO;
import gov.moda.dw.manager.type.StatusCode;
import org.springframework.http.HttpHeaders;

@Data
public class Ams322wRoleLogResDTO {

  private List<RoleLogDTO> roleLogDTOList;

  private HttpHeaders headers;

  private StatusCode statusCode;
}
