package gov.moda.dw.manager.service.dto.custom.ext.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifierOid4vp201iReqDTO {

    private String transactionId;
    private String responseCode;
}
