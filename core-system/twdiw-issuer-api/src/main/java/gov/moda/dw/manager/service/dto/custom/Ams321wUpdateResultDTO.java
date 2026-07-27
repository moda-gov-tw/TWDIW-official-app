package gov.moda.dw.manager.service.dto.custom;

import gov.moda.dw.manager.type.StatusCode;
import lombok.Data;

@Data
public class Ams321wUpdateResultDTO {

  private Ams321wRoleResDTO ams321wRoleResDTO;

  private StatusCode statusCode;
}
