package gov.moda.dw.manager.service.dto.custom;

import lombok.Data;

@Data
public class OrgLogoReqDTO {

    private String logoSquare; // 組織 Logo (正方形 base64)

    private String logoRectangle; // 組織 Logo (長方形 base64)

}
