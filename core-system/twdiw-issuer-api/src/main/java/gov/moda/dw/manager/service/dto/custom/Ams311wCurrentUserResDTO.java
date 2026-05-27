package gov.moda.dw.manager.service.dto.custom;

import lombok.Data;

@Data
public class Ams311wCurrentUserResDTO {

  private String UserId; // 帳號
  private String UserName; // 姓名
  private String OrgId; // 組織
  private String OrgTwName;
  private String Tel; // 連絡電話
  private String UserTypeId; // 帳號類型
  private String UserTypeName; // 帳號類型中文名稱
  private String ApplyUserTypeName; // 申請帳號類型中文名稱
  private String State; // 啟用狀態
}
