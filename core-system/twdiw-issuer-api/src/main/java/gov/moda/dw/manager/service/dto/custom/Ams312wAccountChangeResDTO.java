package gov.moda.dw.manager.service.dto.custom;

import java.util.List;
import lombok.Data;
import gov.moda.dw.manager.service.dto.ExtendedUserLogDTO;
import gov.moda.dw.manager.type.StatusCode;
import org.springframework.http.HttpHeaders;

@Data
public class Ams312wAccountChangeResDTO {

  private List<ExtendedUserLogDTO> extendedUserLogDTOList;

  private HttpHeaders headers;

  private StatusCode statusCode;
}
