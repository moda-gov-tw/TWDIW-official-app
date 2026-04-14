package gov.moda.dw.manager.service.dto.custom;

import java.util.List;
import lombok.Data;
import gov.moda.dw.manager.service.dto.ResLogDTO;
import gov.moda.dw.manager.type.StatusCode;
import org.springframework.http.HttpHeaders;

@Data
public class Ams332wResLogResDTO {

  private List<ResLogDTO> resLogDTO;

  private HttpHeaders headers;

  private StatusCode statusCode;
}
