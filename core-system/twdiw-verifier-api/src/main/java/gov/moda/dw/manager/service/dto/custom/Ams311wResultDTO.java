package gov.moda.dw.manager.service.dto.custom;

import java.io.Serializable;
import lombok.Data;
import gov.moda.dw.manager.type.StatusCode;

@Data
public class Ams311wResultDTO implements Serializable {

  private StatusCode statusCode;

  private Ams311wAccountResDTO ams311wAccountResDTO;

  public Ams311wResultDTO() {}

  public Ams311wResultDTO(StatusCode statusCode) {
    this.statusCode = statusCode;
  }

  public Ams311wResultDTO(Ams311wAccountResDTO ams311wAccountResDTO) {
    this.ams311wAccountResDTO = ams311wAccountResDTO;
    setStatusCode(StatusCode.SUCCESS);
  }
}
