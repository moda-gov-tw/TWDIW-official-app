package gov.moda.dw.verifier.oidvp.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import gov.moda.dw.verifier.oidvp.service.oidvp.customData.CustomData;

@JsonInclude(Include.NON_NULL)
public class VerifyResponse extends Response {

    @JsonProperty("verify_result")
    private Boolean verifyResult;

    @JsonProperty("result_description")
    private String resultDescription;

    @JsonProperty("transaction_id")
    private String transactionId;

    @JsonProperty("holder_did")
    private String holderDid;

    @JsonProperty("data")
    private Object data;

    @JsonProperty("custom_data")
    private CustomData customData;

    public VerifyResponse(String transactionId, VerifyResult verifyResult) {
        this.verifyResult = verifyResult.getVerifyResult();
        this.transactionId = transactionId;
        code = verifyResult.getErrorCode().getCode();
        resultDescription = verifyResult.getResultDescription();
        holderDid = verifyResult.getHolderDid();
        if (verifyResult.getVerifyResult()) {
            data = verifyResult.getVcClaims();
            customData = verifyResult.getCustomData();
        }
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getResultDescription() {
        return resultDescription;
    }

    public void setResultDescription(String resultDescription) {
        this.resultDescription = resultDescription;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Boolean getVerifyResult() {
        return verifyResult;
    }

    public void setVerifyResult(Boolean verifyResult) {
        this.verifyResult = verifyResult;
    }

    public String getHolderDid() {
        return holderDid;
    }

    public void setHolderDid(String holderDid) {
        this.holderDid = holderDid;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        if (code != null) {
            sb.append("\"code\":")
              .append(code);
        }
        if (customData != null) {
            sb.append(",\"customData\":")
              .append(customData);
        }
        if (data != null) {
            sb.append(",\"data\":")
              .append(data);
        }
        if (holderDid != null) {
            sb.append(",\"holderDid\":\"")
              .append(holderDid).append('\"');
        }
        if (resultDescription != null) {
            sb.append(",\"resultDescription\":\"")
              .append(resultDescription).append('\"');
        }
        if (transactionId != null) {
            sb.append(",\"transactionId\":\"")
              .append(transactionId).append('\"');
        }
        if (verifyResult != null) {
            sb.append(",\"verifyResult\":")
              .append(verifyResult);
        }
        if (message != null) {
            sb.append(",\"message\":\"")
              .append(message).append('\"');
        }
        sb.append("}");
        String s = sb.toString();
        return (s.startsWith("{,")) ? s.replace("{,", "{") : s;
    }
}
