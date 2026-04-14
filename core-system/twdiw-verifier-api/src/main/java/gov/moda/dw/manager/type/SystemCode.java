package gov.moda.dw.manager.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 系統類型
 */
@AllArgsConstructor
public enum SystemCode {
    VERIFIERMGR("1", "驗證端模組系統"),
    OID4P("2", "驗證端OID4VP系統服務"),
    VP("3", "驗證端VP系統服務");
    
    @Getter
    private String code;

    @Getter
    private String name;
}
