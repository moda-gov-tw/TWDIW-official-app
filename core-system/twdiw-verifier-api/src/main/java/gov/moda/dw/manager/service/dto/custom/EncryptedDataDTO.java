package gov.moda.dw.manager.service.dto.custom;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EncryptedDataDTO {

    @JsonProperty("t")
    private String tag;

    @JsonProperty("d")
    private String data;

    @JsonProperty("h")
    private String hmac;

    @JsonProperty("k")
    private String keyId;

}
