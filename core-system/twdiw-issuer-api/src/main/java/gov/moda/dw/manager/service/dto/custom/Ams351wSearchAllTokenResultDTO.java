package gov.moda.dw.manager.service.dto.custom;

import java.io.Serializable;
import lombok.Data;
import gov.moda.dw.manager.type.StatusCode;
import org.springframework.data.domain.Page;

@Data
public class Ams351wSearchAllTokenResultDTO implements Serializable {

  private StatusCode statusCode;

  Page<Ams351wAccessTokenResDTO> ams351wAccessTokenResDTOPage;

  public Ams351wSearchAllTokenResultDTO(StatusCode statusCode) {
    this.setStatusCode(statusCode);
  }

  public Ams351wSearchAllTokenResultDTO(Page<Ams351wAccessTokenResDTO> ams351wAccessTokenResDTOPage) {
    this.ams351wAccessTokenResDTOPage = ams351wAccessTokenResDTOPage;
    this.setStatusCode(StatusCode.SUCCESS);
  }

  public void setStatusCode(StatusCode statusCode) {
    if (statusCode != null) {
      this.statusCode = statusCode;
    }
  }
}
