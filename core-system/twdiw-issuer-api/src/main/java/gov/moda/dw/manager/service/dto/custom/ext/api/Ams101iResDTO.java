package gov.moda.dw.manager.service.dto.custom.ext.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ams101iResDTO {

    //帳務系統訂單編號
    private String orderCaseId;
    //帳務系統月結訂單總訂單編號
    private String settlementOrderCaseId;
    //繳費開始時間
    private String payStartDate;
    //繳費結束時間
    private String payEndDate;
}
