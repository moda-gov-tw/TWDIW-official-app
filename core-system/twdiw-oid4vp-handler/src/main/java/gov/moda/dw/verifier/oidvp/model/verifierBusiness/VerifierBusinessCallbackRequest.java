package gov.moda.dw.verifier.oidvp.model.verifierBusiness;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import gov.moda.dw.verifier.oidvp.model.VerifyResult;
import gov.moda.dw.verifier.oidvp.service.oidvp.customData.CustomData;

@JsonInclude(Include.NON_NULL)
public class VerifierBusinessCallbackRequest {

    private final boolean verifyResult;
    private final Integer code;
    private final String resultDescription;
    private final String transactionId;
    private final String vpUid;
    @JsonProperty("holder_did")
    private final String holderDid;
    private final Object data;
    private final CustomData customData;

    public VerifierBusinessCallbackRequest(Integer code, boolean verifyResult, String resultDescription, String transactionId, String vpUid, Object data, String holderDid, CustomData customData) {
        this.code = code;
        this.verifyResult = verifyResult;
        this.resultDescription = resultDescription;
        this.transactionId = transactionId;
        this.vpUid = vpUid;
        this.data = data;
        this.holderDid = holderDid;
        this.customData = customData;
    }

    public VerifierBusinessCallbackRequest(String transactionId, VerifyResult verifyResult, String vpUid) {
        if (verifyResult == null) {
            throw new IllegalArgumentException("verifyResult must not be null");
        }
        this.verifyResult = verifyResult.getVerifyResult();
        this.code = verifyResult.getErrorCode().getCode();
        this.resultDescription = verifyResult.getResultDescription();
        this.transactionId = transactionId;
        this.vpUid = vpUid;
        this.data = verifyResult.getVerifyResult() ? verifyResult.getVcClaims() : null;
        this.holderDid = verifyResult.getHolderDid() == null ? "" : verifyResult.getHolderDid();
        this.customData = verifyResult.getCustomData();
    }

    public Integer getCode() {
        return code;
    }

    public Object getData() {
        return data;
    }

    public String getHolderDid() {
        return holderDid;
    }

    public String getResultDescription() {
        return resultDescription;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public boolean isVerifyResult() {
        return verifyResult;
    }

    public String getVpUid() {
        return vpUid;
    }

    public CustomData getCustomData() {
        return customData;
    }

    public String toJson() {
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"verifyResult\":").append(verifyResult);
        sb.append(",\"code\":").append(code);
        sb.append(",\"resultDescription\":\"").append(resultDescription).append('\"');
        sb.append(",\"transactionId\":\"").append(transactionId).append('\"');
        sb.append(",\"vpUid\":\"").append(vpUid).append('\"');
        sb.append(",\"holder_did\":\"").append(holderDid).append('\"');
        sb.append(",\"data\":").append(data);
        sb.append(",\"customData\":").append(customData.getJsonValue());
        sb.append("}");
        return sb.toString();
    }

    @Override
    public String toString() {
        return toJson();
    }
}
