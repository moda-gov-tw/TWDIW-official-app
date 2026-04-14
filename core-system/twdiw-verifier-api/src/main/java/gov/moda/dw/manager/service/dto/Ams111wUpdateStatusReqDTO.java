package gov.moda.dw.manager.service.dto;

import lombok.Data;

@Data
public class Ams111wUpdateStatusReqDTO {

    private String orderType; // 訂單類型代碼
    private String status; // 狀態
}
