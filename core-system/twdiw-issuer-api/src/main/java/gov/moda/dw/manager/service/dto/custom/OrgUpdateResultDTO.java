package gov.moda.dw.manager.service.dto.custom;

import gov.moda.dw.manager.type.StatusCode;
import lombok.Data;

@Data
public class OrgUpdateResultDTO {

    private OrgResDTO orgResDTO;

    private StatusCode statusCode;
}
