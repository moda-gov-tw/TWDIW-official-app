package gov.moda.dw.manager.service.dto.custom.ext.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nimbusds.jose.shaded.gson.JsonArray;
import com.nimbusds.jose.shaded.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifierOid4vp301iRespDTO {
    @JsonProperty("verify_result")
    private Boolean verifyResult;

    @JsonProperty("result_description")
    private String resultDescription;

    @JsonProperty("transaction_id")
    private String transactionId;
    private JsonArray data;

    // private String code;

    // private String message;
}
