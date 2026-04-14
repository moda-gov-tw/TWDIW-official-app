package gov.moda.dw.manager.service.dto.custom;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomVersionInfoResDTO {

    private CustomVersionInfoDetailResDTO verifiermgr;
    
    private CustomVersionInfoDetailResDTO oid4vp;
    
    private CustomVersionInfoDetailResDTO vp;
    
    private String env;
    
    private String appDownloadDate;

}
