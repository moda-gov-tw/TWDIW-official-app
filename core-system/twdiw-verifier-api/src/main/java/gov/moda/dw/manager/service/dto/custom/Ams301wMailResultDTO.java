package gov.moda.dw.manager.service.dto.custom;

import java.io.Serializable;
import lombok.Data;
import gov.moda.dw.manager.service.dto.MailTemplateDTO;
import gov.moda.dw.manager.type.StatusCode;

@Data
public class Ams301wMailResultDTO implements Serializable {

  private StatusCode statusCode;

  private MailTemplateDTO mailTemplateDTO;

  public Ams301wMailResultDTO(StatusCode statusCode) {
    this.statusCode = statusCode;
  }

  public Ams301wMailResultDTO(MailTemplateDTO mailTemplateDTO) {
    this.mailTemplateDTO = mailTemplateDTO;
    setStatusCode(StatusCode.SUCCESS);
  }
}
