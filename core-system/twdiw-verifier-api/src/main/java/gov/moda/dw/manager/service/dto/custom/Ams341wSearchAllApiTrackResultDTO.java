package gov.moda.dw.manager.service.dto.custom;

import java.io.Serializable;
import lombok.Data;
import gov.moda.dw.manager.type.StatusCode;
import org.springframework.data.domain.Page;

@Data
public class Ams341wSearchAllApiTrackResultDTO implements Serializable {

    private StatusCode statusCode;

    Page<Ams341wApiTrackResDTO> ams341wApiTrackResDTOPage;

    public Ams341wSearchAllApiTrackResultDTO(StatusCode statusCode) {
        this.setStatusCode(statusCode);
    }

    public Ams341wSearchAllApiTrackResultDTO(Page<Ams341wApiTrackResDTO> ams341wApiTrackResDTOPage) {
        this.ams341wApiTrackResDTOPage = ams341wApiTrackResDTOPage;
        this.setStatusCode(StatusCode.SUCCESS);
    }

    public void setStatusCode(StatusCode statusCode) {
        if (statusCode != null) {
            this.statusCode = statusCode;
        }
    }
}
