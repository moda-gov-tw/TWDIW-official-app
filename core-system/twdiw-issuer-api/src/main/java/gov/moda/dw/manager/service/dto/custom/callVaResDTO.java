package gov.moda.dw.manager.service.dto.custom;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class callVaResDTO {
    private String transactionValue;
    private String resultMSG;
    private String resultException;
    private int resultErrorCode;
}
