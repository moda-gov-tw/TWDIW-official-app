package gov.moda.dw.manager.service.dto.custom;

import java.util.List;

import lombok.Data;

@Data
public class Dwvp301iReqDTO {
    
    private Boolean verifyResult;

    private Integer code;
    
    private String resultDescription;
    
    private String transactionId;
    
    private String vpUid;
    
    private List<Dwvp301iReqDataDTO> data;
}
