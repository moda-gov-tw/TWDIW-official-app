package gov.moda.dw.manager.service.dto.custom.ext.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ams105iResDTO {

    private String orderType;
    private String orderPlatform;
    private String userType;
    private String payPlatform;
    private String checkoutFunc;
    private String invOrRecType;
}
