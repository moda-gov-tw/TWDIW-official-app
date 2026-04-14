package gov.moda.dw.manager.service.dto.custom;

import java.io.Serializable;
import lombok.Data;
import gov.moda.dw.manager.type.StatusCode;

@Data
public class Ams351wUpsertTokenResultDTO implements Serializable {

  private StatusCode statusCode;

  private Ams351wResultDTO ams351WResultDTO;

  public Ams351wUpsertTokenResultDTO(StatusCode statusCode) {
    this.setStatusCode(statusCode);
  }

  public Ams351wUpsertTokenResultDTO(Ams351wResultDTO ams351WResultDTO) {
    this.ams351WResultDTO = ams351WResultDTO;
    this.setStatusCode(StatusCode.SUCCESS);
  }

  public void setStatusCode(StatusCode statusCode) {
    if (statusCode != null) {
      this.statusCode = statusCode;
    }
  }
}
