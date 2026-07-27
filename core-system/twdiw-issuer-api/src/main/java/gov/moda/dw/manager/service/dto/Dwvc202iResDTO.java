package gov.moda.dw.manager.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Dwvc202iResDTO {

  @JsonProperty("dataTag")
  private String dataTag;

  @JsonProperty("vcList")
  private List<Dwvc202iVcData> vcList;

  @Getter
  @Setter
  public static class Dwvc202iVcData {

    @JsonProperty("cid")
    private String cid;

    @JsonProperty("vcUid")
    private String vcUid;

    @JsonProperty("issuranceDate")
    private String issuranceDate;

    @JsonProperty("expirationDate")
    private String expirationDate;

    @JsonProperty("credentialStatus")
    private String credentialStatus;

    @JsonProperty("issuanceDate")
    public String getIssuanceDate() {
      return this.issuranceDate;
    }
  }
}
