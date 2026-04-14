package gov.moda.dw.manager.service.dto.custom;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DwSandBoxVP401WStaticQRCodeResDTO {

    private String base64;
    
    private String url;
}
