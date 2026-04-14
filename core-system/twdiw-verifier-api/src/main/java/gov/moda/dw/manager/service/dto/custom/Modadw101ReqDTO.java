package gov.moda.dw.manager.service.dto.custom;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Modadw101ReqDTO {

    private DIDregisterOrgDTO org;

    private String signature;

}
