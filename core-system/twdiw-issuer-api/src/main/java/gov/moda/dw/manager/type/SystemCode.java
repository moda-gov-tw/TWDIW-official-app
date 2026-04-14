package gov.moda.dw.manager.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 系統類型
 */
@AllArgsConstructor
public enum SystemCode {
    ISSUERMGR("1", "發行端模組系統"),
    OID4VCI("2", "發行端OID4VCI系統服務"),
    VC("3", "發行端VC系統服務");
    
    @Getter
    private String code;

    @Getter
    private String name;
}
