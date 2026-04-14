package gov.moda.dw.manager.service.dto.custom.ext.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifierOid4vp101iV2RespDTO {

    @JsonProperty("transactionId")
    private String transactionId;

    @JsonProperty("qrcodeImage")
    private String qrcodeImage;

    @JsonProperty("authUri")
    private String authUri;

    // private String code;

    // private String message;

    // private JsonObject info;

}
