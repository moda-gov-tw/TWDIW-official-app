package gov.moda.dw.manager.service.dto.custom.ext.api;

import gov.moda.dw.manager.type.StatusCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ams101iResultDTO {

  private StatusCode statusCode;
  private Ams101iResDTO ams101iResDTO;
}
