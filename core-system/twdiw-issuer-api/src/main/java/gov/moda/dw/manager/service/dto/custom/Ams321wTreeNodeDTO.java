package gov.moda.dw.manager.service.dto.custom;

import lombok.Data;
import gov.moda.dw.manager.type.StatusCode;

@Data
public class Ams321wTreeNodeDTO {

  private MenuTreeDTO menuTreeDTO;

  private StatusCode statusCode;
}
