package gov.moda.dw.manager.service.dto.custom;

import java.time.Instant;
import lombok.Data;

@Data
public class Ams312wReqDTO {

  private String logType; // 異動類型

  private String userId; // 帳號

  private String beginDate; // 開始時間

  private String endDate; // 結束時間
}
