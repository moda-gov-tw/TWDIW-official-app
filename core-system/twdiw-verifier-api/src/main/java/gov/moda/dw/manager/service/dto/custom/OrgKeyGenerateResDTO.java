package gov.moda.dw.manager.service.dto.custom;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrgKeyGenerateResDTO {

    @JsonProperty("publicKey")
    private String publicKey;

    @JsonProperty("privateKey")
    private String privateKey;

    @JsonProperty("totpKey")
    private String totpKey;

    @JsonProperty("hmacKey")
    private String hmacKey;

}
