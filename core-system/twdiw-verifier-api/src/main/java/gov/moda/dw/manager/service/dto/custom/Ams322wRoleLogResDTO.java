package gov.moda.dw.manager.service.dto.custom;

import gov.moda.dw.manager.service.dto.RoleLogDTO;
import gov.moda.dw.manager.type.StatusCode;
import java.util.List;
import lombok.Data;
import org.springframework.http.HttpHeaders;

@Data
public class Ams322wRoleLogResDTO {

  private List<RoleLogDTO> roleLogDTOList;

  private HttpHeaders headers;

  private StatusCode statusCode;
}
