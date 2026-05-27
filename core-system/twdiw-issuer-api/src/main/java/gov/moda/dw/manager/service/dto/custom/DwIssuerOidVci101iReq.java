package gov.moda.dw.manager.service.dto.custom;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DwIssuerOidVci101iReq {

  @JsonProperty("authenticated")
  private boolean authenticated;

  @JsonProperty("id_token")
  private String idToken;

  @JsonProperty("cid")
  private List<String> cid;
}
