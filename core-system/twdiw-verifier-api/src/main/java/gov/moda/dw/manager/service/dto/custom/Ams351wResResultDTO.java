package gov.moda.dw.manager.service.dto.custom;

import java.io.Serializable;
import java.util.List;
import lombok.Data;
import lombok.Getter;
import gov.moda.dw.manager.service.dto.ResDTO;
import gov.moda.dw.manager.type.StatusCode;

@Getter
@Data
public class Ams351wResResultDTO implements Serializable {

  private StatusCode statusCode;

  private List<ResDTO> resDTOList;

  public Ams351wResResultDTO(StatusCode statusCode) {
    this.setStatusCode(statusCode);
  }

  public Ams351wResResultDTO(List<ResDTO> resDTOList) {
    this.resDTOList = resDTOList;
    this.setStatusCode(StatusCode.SUCCESS);
  }

  public void setStatusCode(StatusCode statusCode) {
    if (statusCode != null) {
      this.statusCode = statusCode;
    }
  }
}
