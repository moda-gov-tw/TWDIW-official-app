package gov.moda.dw.manager.service.dto.custom;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Dwvp102iResDTO {

  @JsonProperty("vpItems")
  private List<Dwvp102iVpItemResDTO> vpItems;

  @Getter
  @Setter
  public static class Dwvp102iVpItemResDTO {

    @JsonProperty("vpUid")
    private String vpUid;

    @JsonProperty("name")
    private String name;

    @JsonProperty("isStatic")
    private Boolean isStatic;

    @JsonProperty("isOffline")
    private Boolean isOffline;
  }
}
