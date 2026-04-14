package gov.moda.dw.manager.service.dto.custom;

import java.time.Instant;

public interface CustomVPItemDTO {
	// ID
	Long getId();
	
	// VP編號
	String getSerialNo();
	
	// VP名稱
	String getName();
	
	// 建立者
	Long getCrUser();
	
	// 單位
	String getBusinessId();
	
	// 呈現定義
	String getPresentationDefinition();
	
	// 驗證目的
	String getPurpose();
	
	// 使用者條款
	String getTerms();
	
	// 修改者
	String getUrUser();
	
	// 修改時間
	Instant getUpDatetime();
	
	// 建立群組資訊
    String getGroupInfo();
    
    // 模式
    String getModel();
    
    // 組織業務系統URL
    String getVerifierServiceUrl();
    
    // callBack URL
    String getCallBackUrl();
    
    // 標籤
    String getTag();
    
    // 模組加密
    Boolean getIsEncryptEnabled();
    
    // 描述與欄位
    String getFieldInfo();
}