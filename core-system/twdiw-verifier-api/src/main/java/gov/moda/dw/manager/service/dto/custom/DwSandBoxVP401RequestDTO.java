package gov.moda.dw.manager.service.dto.custom;

import java.time.Instant;

import lombok.Data;

@Data
public class DwSandBoxVP401RequestDTO {
    
    /**
     * VP代碼
     */
    private String serialNo;
    
    /**
     * VP名稱
     */
    private String name;
    
    /**
     * 最後更新時間(起)
     */
    private Instant startDate;
    
    /**
     * 最後更新時間(迄)
     */
    private Instant endDate;

}
