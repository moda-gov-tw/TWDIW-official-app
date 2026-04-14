package gov.moda.dw.manager.service.dto.custom;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomVersionInfoResDTO {

    private CustomVersionInfoDetailResDTO issuermgr;
    
    private CustomVersionInfoDetailResDTO oid4vci;
    
    private CustomVersionInfoDetailResDTO vc;
    
    private String env;
    
    private String appDownloadDate;

}
