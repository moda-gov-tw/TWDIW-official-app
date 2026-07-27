package gov.moda.dw.manager.service.dto.custom;

import gov.moda.dw.manager.type.StatusCode;
import lombok.Data;

@Data
class BwdRSAResultDTO {

  private String currentPassword;
  private String newPassword;
  private StatusCode statusCode;
}
