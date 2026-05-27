package gov.moda.dw.manager.service.dto.custom;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetQrCodeWarningsDTO {

  @JsonProperty("statusRevoke")
  private List<String> statusRevoke;

  @JsonProperty("cidNotFound")
  private List<String> cidNotFound;
}
