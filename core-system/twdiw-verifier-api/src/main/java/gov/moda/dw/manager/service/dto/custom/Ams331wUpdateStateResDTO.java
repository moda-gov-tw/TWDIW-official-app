package gov.moda.dw.manager.service.dto.custom;

import gov.moda.dw.manager.service.dto.ResDTO;
import gov.moda.dw.manager.type.StatusCode;
import lombok.Data;

@Data
public class Ams331wUpdateStateResDTO {

  private ResDTO resDTO;

  private StatusCode statusCode;
}
