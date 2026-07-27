package gov.moda.dw.manager.service.dto.custom;

import gov.moda.dw.manager.type.StatusCode;
import lombok.Data;

@Data
public class Ams311wDetailResultDTO {

  private Ams311wAccountResDTO ams311wAccountResDTO;

  private StatusCode statusCode;
}
