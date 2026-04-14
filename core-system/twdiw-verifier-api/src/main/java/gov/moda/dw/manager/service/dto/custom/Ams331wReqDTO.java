package gov.moda.dw.manager.service.dto.custom;

import lombok.Data;

@Data
public class Ams331wReqDTO {

  // 功能類型
  private String typeId;

  // 功能代碼
  private String resId;

  // 功能名稱
  private String resName;

  // 是否啟用
  private String state;
}
