package gov.moda.dw.manager.service.dto.custom;

import lombok.Data;

@Data
public class Ams332wReqDTO {

  private String logType; // 異動類型

  private String resId; // 功能代號

  private String beginDate; // 開始時間

  private String endDate; // 結束時間
}
