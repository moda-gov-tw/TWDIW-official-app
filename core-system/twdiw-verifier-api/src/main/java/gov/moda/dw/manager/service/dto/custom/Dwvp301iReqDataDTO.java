package gov.moda.dw.manager.service.dto.custom;

import lombok.Data;

@Data
public class Dwvp301iReqDataDTO {
    
    private String credentialType;
    
    private Dwvp301iReqClaimsDTO claims;
}
