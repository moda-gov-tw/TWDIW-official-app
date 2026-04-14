package gov.moda.dw.manager.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VcItemData305iRequestDTO {

    @JsonProperty("credential_type")
    private String credentialType;

    private String nonce;
    private String sub;

    private String transactionId;

    public String getCredentialType() {
        return credentialType;
    }

    public void setCredentialType(String credentialType) {
        this.credentialType = credentialType;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
        this.transactionId = nonce;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public String toString() {
        return "VcItemData305iRequestDTO{" +
            "credentialType='" + credentialType + '\'' +
            ", nonce='" + nonce + '\'' +
            ", sub='" + sub + '\'' +
            ", transactionId='" + transactionId + '\'' +
            '}';
    }
}
