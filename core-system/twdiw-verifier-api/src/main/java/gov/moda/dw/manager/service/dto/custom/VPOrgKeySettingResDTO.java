package gov.moda.dw.manager.service.dto.custom;

import java.time.Instant;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VPOrgKeySettingResDTO {
    
    private Boolean verifyDID;
    
    private List<OrgKeySettingResDTO> data;
    
}
