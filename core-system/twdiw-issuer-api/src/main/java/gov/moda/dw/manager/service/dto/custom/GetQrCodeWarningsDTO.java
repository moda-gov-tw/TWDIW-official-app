package gov.moda.dw.manager.service.dto.custom;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetQrCodeWarningsDTO {

    @JsonProperty("statusRevoke")
    private List<String> statusRevoke;

    @JsonProperty("cidNotFound")
    private List<String> cidNotFound;

}
