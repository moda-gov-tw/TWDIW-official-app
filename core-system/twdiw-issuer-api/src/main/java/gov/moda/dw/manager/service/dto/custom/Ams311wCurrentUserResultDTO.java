package gov.moda.dw.manager.service.dto.custom;

import gov.moda.dw.manager.type.StatusCode;
import lombok.Data;

@Data
public class Ams311wCurrentUserResultDTO {

  private Ams311wCurrentUserResDTO ams311wCurrentUserResDTO;
  private StatusCode statusCode;
}
