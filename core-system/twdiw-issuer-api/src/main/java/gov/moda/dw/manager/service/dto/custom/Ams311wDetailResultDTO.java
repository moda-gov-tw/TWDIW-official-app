package gov.moda.dw.manager.service.dto.custom;

import lombok.Data;
import gov.moda.dw.manager.type.StatusCode;

@Data
public class Ams311wDetailResultDTO {

    private Ams311wAccountResDTO ams311wAccountResDTO;

    private StatusCode statusCode;
}
