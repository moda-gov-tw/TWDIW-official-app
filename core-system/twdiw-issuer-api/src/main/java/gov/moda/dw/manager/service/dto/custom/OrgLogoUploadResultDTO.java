package gov.moda.dw.manager.service.dto.custom;

import gov.moda.dw.manager.type.StatusCode;
import lombok.Data;

@Data
public class OrgLogoUploadResultDTO {

    private OrgLogoResDTO orgLogoResDTO;

    private StatusCode statusCode;
}
