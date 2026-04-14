package gov.moda.dw.manager.service.dto.custom;

import lombok.Data;
import gov.moda.dw.manager.service.dto.ResDTO;
import gov.moda.dw.manager.type.StatusCode;

@Data
public class Ams331wUpdateStateResDTO {

  private ResDTO resDTO;

  private StatusCode statusCode;
}
