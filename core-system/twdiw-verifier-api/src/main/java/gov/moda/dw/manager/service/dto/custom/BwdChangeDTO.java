package gov.moda.dw.manager.service.dto.custom;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BwdChangeDTO {

  private String currentBwd;
  private String newBwd;
  private String userId;
}
