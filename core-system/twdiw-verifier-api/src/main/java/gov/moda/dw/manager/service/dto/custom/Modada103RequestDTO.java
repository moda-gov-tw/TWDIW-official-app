package gov.moda.dw.manager.service.dto.custom;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Modada103RequestDTO {
    
    /**
     * 金鑰代碼
     */
    private String keyId;
    
    /**
     * 金鑰備註
     */
    private String description;
    
    /**
     * 最後更新時間(起)
     */
    private Instant startDate;
    
    /**
     * 最後更新時間(迄)
     */
    private Instant endDate;
}
