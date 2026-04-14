package gov.moda.dw.manager.service.dto.custom.ext.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifierOid4vp101iRespDTO {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("code")
    private String code;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("message")
    private String message;

    @JsonProperty("transaction_id")
    private String transactionId;

    @JsonProperty("qrcode_image")
    private String qrcodeImage;

    @JsonProperty("auth_uri")
    private String authUri;

}
