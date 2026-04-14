package gov.moda.dw.manager.service.dto.custom;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class VerifyDidReqDTO {
    private String b64Data;
    private String baseUrl;
    private String token;
}
