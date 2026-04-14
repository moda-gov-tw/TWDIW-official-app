package gov.moda.dw.manager.service.dto.custom;

import lombok.Data;
import gov.moda.dw.manager.type.StatusCode;

@Data
class BwdRSAResultDTO {

  private String currentPassword;
  private String newPassword;
  private StatusCode statusCode;
}
