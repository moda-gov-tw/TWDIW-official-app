package gov.moda.dw.manager.service.dto.custom;

import lombok.Data;

@Data
public class Ams322wReqDTO {

  private String logType; // 異動類型

  private String roleId; // 角色代號

  private String beginDate; // 開始時間

  private String endDate; // 結束時間
}
