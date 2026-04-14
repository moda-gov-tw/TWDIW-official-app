package gov.moda.dw.manager.service.dto.custom;

import lombok.Data;
import gov.moda.dw.manager.type.StatusCode;

@Data
public class Ams311wCurrentUserResultDTO {

  private Ams311wCurrentUserResDTO ams311wCurrentUserResDTO;
  private StatusCode statusCode;
}
