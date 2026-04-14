package gov.moda.dw.manager.service.dto.custom;

import lombok.Data;
import gov.moda.dw.manager.type.StatusCode;

@Data
public class Ams321wUpdateResultDTO {

    private Ams321wRoleResDTO ams321wRoleResDTO;

    private StatusCode statusCode;
}
