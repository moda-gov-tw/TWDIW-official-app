package gov.moda.dw.manager.service.dto.custom;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrgLogoResDTO {

    private String orgId; // 組織代碼
    private Boolean issuerDID; // 組織是否已註冊 DID
    private String orgTwName; // 組織中文名
    private String orgEnName; // 組織英文名
    private String logoSquare; // 組織 Logo (正方形 base64)
    private String logoRectangle; // 組織 Logo (長方形 base64)
}
