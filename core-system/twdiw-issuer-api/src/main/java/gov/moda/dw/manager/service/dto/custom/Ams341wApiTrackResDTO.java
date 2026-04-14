package gov.moda.dw.manager.service.dto.custom;

import gov.moda.dw.manager.service.dto.ApiTrackDTO;
import gov.moda.dw.manager.type.StatusCode;
import java.util.List;
import lombok.Data;
import org.springframework.http.HttpHeaders;

@Data
public class Ams341wApiTrackResDTO {

    private List<ApiTrackDTO> apiTrackDTOList;

    private HttpHeaders headers;

    private StatusCode statusCode;
}
