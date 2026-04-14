package gov.moda.dw.manager.service.dto.custom;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class VerifyAuthReqDTO {
    private String pid;
    private String b64Data;
}
