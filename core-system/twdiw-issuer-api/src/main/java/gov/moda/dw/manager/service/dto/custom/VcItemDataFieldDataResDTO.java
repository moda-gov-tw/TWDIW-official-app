package gov.moda.dw.manager.service.dto.custom;

import gov.moda.dw.manager.service.dto.VCItemDataFieldDTO;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VcItemDataFieldDataResDTO {

  private List<VCItemDataFieldDTO> content;

  private String serialNo;

  private String name;

  private List<CustomVcDataStatusLogDTO> statusLogList;
}
