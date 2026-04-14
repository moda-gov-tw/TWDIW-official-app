package gov.moda.dw.manager.service.dto.custom;

import java.util.List;
import lombok.Data;
import gov.moda.dw.manager.type.StatusCode;

@Data
public class Ams321wRoleConferRultDTO {

    private List<Ams321wRoleConferResDTO> ams321wRoleResResponseDTOList;

    private StatusCode statusCode;
}
