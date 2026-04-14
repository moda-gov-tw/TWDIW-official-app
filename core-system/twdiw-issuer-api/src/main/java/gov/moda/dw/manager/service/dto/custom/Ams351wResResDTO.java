package gov.moda.dw.manager.service.dto.custom;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import gov.moda.dw.manager.service.dto.ResDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ams351wResResDTO implements Serializable {

  private List<ResDTO> resDTOList;
}
