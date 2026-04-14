package gov.moda.dw.manager.service.dto.custom;

import lombok.Data;

@Data
public class Ams311wCurrentUserReqDTO {

  private String userId; // 帳號

  private String userName; // 姓名

  //  private String phone; // 手機

  private String tel; // 市話
}
