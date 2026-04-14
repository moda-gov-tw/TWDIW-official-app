package gov.moda.dw.manager.service.dto.custom;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Modadw101ReqDTO {

    private DIDregisterOrgDTO org;

    private String signature;
}
