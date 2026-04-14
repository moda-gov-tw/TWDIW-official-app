package gov.moda.dw.manager.service.dto.custom;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ams341wReqDTO implements Serializable {

    private String source;

    private String serviceId;

    private String uri;

    private String statusCode;

    private String jhiFrom;

    private String jhiTo;

    private String beginDate;

    private String endDate;
}
