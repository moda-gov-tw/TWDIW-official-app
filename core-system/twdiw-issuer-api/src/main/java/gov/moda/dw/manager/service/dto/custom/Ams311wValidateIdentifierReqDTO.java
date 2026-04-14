package gov.moda.dw.manager.service.dto.custom;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ams311wValidateIdentifierReqDTO implements Serializable {

  private String activationKey;
  private String resetKey;
}
