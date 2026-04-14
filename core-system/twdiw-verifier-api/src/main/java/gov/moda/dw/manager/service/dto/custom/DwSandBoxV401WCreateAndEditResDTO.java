package gov.moda.dw.manager.service.dto.custom;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class DwSandBoxV401WCreateAndEditResDTO {
    
    private Long id;

    // VP編號
    private String serialNo;
    
    // VP名稱
    private String name;
    
    // 單位
    private String businessId;
    
    // 呈現定義
    private String presentationDefinition;
    
    // 驗證目的
    private String purpose;
    
    // 使用者條款
    private String terms;
            
    // 模式
    private Map<String, String> model;
    
    // 標籤
    private String tag;
    
    // 欄位
    private List<CustomFieldReqDTO> fields;
    
    // 驗證端模組 url
    private String verifierServiceUrl;
    
    // 組織業務系統 url
    private String callBackUrl;
    
    // 模組加密
    private Boolean isEncryptEnabled;
}
