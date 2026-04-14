package gov.moda.dw.manager.service.dto.custom;

import com.fasterxml.jackson.annotation.JsonProperty;

import gov.moda.dw.manager.service.dto.VCItemDataDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomVCItemDataDTO extends VCItemDataDTO {

    private static final long serialVersionUID = -5146152890533043168L;

    @JsonProperty("deepLink")
    private String deepLink;

    @JsonProperty("warnings")
    private GetQrCodeWarningsDTO warnings;

}
