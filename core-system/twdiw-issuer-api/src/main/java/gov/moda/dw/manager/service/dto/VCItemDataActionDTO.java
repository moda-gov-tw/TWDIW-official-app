package gov.moda.dw.manager.service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VCItemDataActionDTO {

    private Long id;

    private String encryptedVcCid;

    // 狀態 [0: ACTIVE(有效)、1: SUSPENDED(停用)、2: REVOKED(撤銷)]
    private String action;

}
