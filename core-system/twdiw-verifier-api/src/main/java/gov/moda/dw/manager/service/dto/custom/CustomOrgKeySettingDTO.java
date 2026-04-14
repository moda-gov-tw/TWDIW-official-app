package gov.moda.dw.manager.service.dto.custom;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomOrgKeySettingDTO {

    @NotBlank
    @Size(max = 50)
    @JsonProperty("keyId")
    private String keyId;

    @Size(max = 18)
    @JsonProperty("description")
    private String description;

    @NotBlank
    @JsonProperty("publicKey")
    private String pubKey;

    @JsonProperty("privateKey")
    private String prvKey;

    @NotBlank
    @JsonProperty("totpKey")
    private String totpKey;

    @NotBlank
    @JsonProperty("hmacKey")
    private String hmacKey;

    @JsonProperty("isActive")
    private Boolean isActive;

}
