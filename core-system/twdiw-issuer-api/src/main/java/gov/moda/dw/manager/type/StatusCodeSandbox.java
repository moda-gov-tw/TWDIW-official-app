package gov.moda.dw.manager.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum StatusCodeSandbox {
    Success("S0001", "成功", "Success"),
    No_Create_Permission("S0002", "無建立權限", "No_Create_Permission"),
    No_View_Permission("S0003", "無查看權限", "No_View_Permission"),
    No_Edit_Permission("S0004", "無異動權限", "No_Edit_Permission"),
    No_Delete_Permission("S0005", "無刪除權限", "No_Delete_Permission"),
    Not_Exist("S0006", "交易序號不存在", "Not_Exist"),
    //
    Organization_Info_Not_Found("S2001", "查無組織資料", "Organization_Info_Not_Found"),
    OrgId_Account_OrgId_Mismatch("S2002", "OrgId 與帳號組織不一致", "OrgId_Account_OrgId_Mismatch"),
    //vc欄位,正則
    //vc模版
    Invalid_Credential_Type("S4001", "Credential Type 不正確", "Invalid_Credential_Type"),
    VC_Template_Not_Found("S4002", "VC模版不存在", "VC_Template_Not_Found"),
    Data_Field_Count_Mismatch_With_VC_Template(
        "S4003",
        "資料欄位與VC模版欄位定義的數量不一致",
        "Data_Field_Count_Mismatch_With_VC_Template"
    ),
    Data_Field_Order_Mismatch_With_VC_Template(
        "S4004",
        "資料欄位與VC模版欄位定義的順序不一致",
        "Data_Field_Order_Mismatch_With_VC_Template"
    ),
    VC_Template_Field_Not_Found_In_Data("S4005", "VC模版欄位定義的欄位在資料中不存在", "VC_Template_Field_Not_Found_In_Data"),
    Duplicate_VC_Template_TransactionId("S4006", "VC模版的TransactionId重覆", "Duplicate_VC_Template_TransactionId"),
    Connection_901_Information_Not_Set("S4007", "未設定901連線資訊", "Connection_901_Information_Not_Set"),
    Template_Inconsistency("S4008", "模版不一致", "Template_Inconsistency"),
    The_VC_Template_Has_Been_Used("S4009", "VC模版已使用", "The_VC_Template_Has_Been_Used"),
    Non_Original_Creator("S4010", "非原始建立者", "Non_Original_Creator"),
    Invalid_Type_Value("S4011", "Type 值錯誤", "Invalid_Type_Value"),
    Type_Or_IssuerServiceUrl_Missing("S4012", "Type 與 IssuerServiceUrl 必須同時存在或同時為空", "Type_Or_IssuerServiceUrl_Missing"),
    The_VC_Template_Is_Not_Used_And_Can_Be_Deleted_Directly("S4013", "VC模版未使用，可直接刪除",
            "The_VC_Template_Is_Not_Used_And_Can_Be_Deleted_Directly"),
    //vc資料
    VC_API_Data_Source_Type_Not_Set("S5001", "未設定VC介接資料來源API類型", "VC_API_Data_Source_Type_Not_Set"),
    VC_Data_Source_Not_Allowed("S5002", "VC介接資料不允許此種介接方式", "VC_Data_Source_Not_Allowed"),
    VC_Data_Regex_Validation_Failed("S5003", "VC資料正規表達式檢核失敗", "Regex_Validation_Failed"),
    Missing_Argument("S5004", "遺漏參數", "Missing_Argument"),
    VC_Data_Not_Found("S5005", "無資料", "VC_Data_Not_Found"),
    VC_Data_ExpiredDate_Not_Allowed("S5006", "到期日不可以早於今日或建立 VC 模板日期", "VC_Data_ExpiredDate_Not_Allowed"),
    VC_Data_IssusnceDate_Not_Allowed("S5007", "發行日不可以是未來時間", "VC_Data_IssusnceDate_Not_Allowed");

    @Getter
    private final String code;

    @Getter
    private final String msg;

    @Getter
    private final String errorKey;

    public static StatusCodeSandbox toStatusCode(String code) {
        for (StatusCodeSandbox tmp : StatusCodeSandbox.values()) {
            if (tmp.getCode().equals(code)) {
                return tmp;
            }
        }
        return null;
    }
}
