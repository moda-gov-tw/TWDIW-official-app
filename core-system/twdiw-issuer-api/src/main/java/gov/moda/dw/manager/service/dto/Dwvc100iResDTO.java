package gov.moda.dw.manager.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import gov.moda.dw.manager.service.dto.custom.GetQrCodeWarningsDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO for the {@link gov.moda.dw.manager.domain.VCItemData} entity.
 */
@Getter
@Setter
public class Dwvc100iResDTO {

    @JsonProperty("transactionId")
    private String transactionId;

    @JsonProperty("qrCode")
    private String qrCode;

    @JsonProperty("deepLink")
    private String deepLink;

    @JsonProperty("warnings")
    public GetQrCodeWarningsDTO warnings;

}
