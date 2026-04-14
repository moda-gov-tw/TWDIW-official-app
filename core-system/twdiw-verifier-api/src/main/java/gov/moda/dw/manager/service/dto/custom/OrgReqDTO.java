package gov.moda.dw.manager.service.dto.custom;

import lombok.Data;

import java.time.LocalDate;

@Data
public class OrgReqDTO {

    private Long id; // 組織 table id
    private String orgId; // 組織代碼
    private String orgTwName; // 中文名稱
    private String orgEnName; // 英文名稱
    private String metadata; // 資料
    private String logoSquare; // 組織 Logo (正方形 base64)
    private String logoRectangle; // 組織 Logo (長方形 base64)

    private LocalDate createTimeFrom; // 建立時間(起)
    private LocalDate createTimeTo; // 建立時間(訖)
}
