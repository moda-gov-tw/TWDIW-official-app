package gov.moda.dw.manager.service.dto.custom;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrgKeyDetailResDTO {

    private Long id;
    
    private String orgId;
    
    private String keyId;
    
    private String description;
    
    private String publicKey;
    
    private String privateKey;
    
    private String totpKey;
    
    private String hmacKey;
    
    private Boolean isActive;
    
    private Instant crDatetime;
    
    private Instant upDatetime;
}
