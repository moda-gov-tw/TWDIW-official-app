package gov.moda.dw.manager.service.dto.custom;

import gov.moda.dw.manager.type.StatusCode;
import lombok.Data;

@Data
public class Ams311wValidateIdentifierResultDTO {

    private Ams311wValidateIdentifierResDTO ams311wValidateIdentifierResDTO;
    
    private StatusCode statusCode;
}
