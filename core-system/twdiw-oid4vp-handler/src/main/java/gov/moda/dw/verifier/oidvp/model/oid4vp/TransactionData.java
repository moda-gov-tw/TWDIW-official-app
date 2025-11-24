package gov.moda.dw.verifier.oidvp.model.oid4vp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonInclude(Include.NON_NULL)
public class TransactionData {

    private String type;

    @JsonProperty("credential_ids")
    @JsonInclude(Include.NON_EMPTY)
    private List<String> credentialIds;

    @JsonProperty("transaction_data_hashes_alg")
    @JsonInclude(Include.NON_EMPTY)
    private List<String> transactionDataHashesAlg;


    public TransactionData(String type, List<String> credentialIds) {
        this.type = type;
        this.credentialIds = credentialIds;
    }

    public TransactionData(String type, List<String> credentialIds, List<String> transactionDataHashesAlg) {
        this.type = type;
        this.credentialIds = credentialIds;
        this.transactionDataHashesAlg = transactionDataHashesAlg;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getCredentialIds() {
        return credentialIds;
    }

    public void setCredentialIds(List<String> credentialIds) {
        this.credentialIds = credentialIds;
    }

    public List<String> getTransactionDataHashesAlg() {
        return transactionDataHashesAlg;
    }

    public void setTransactionDataHashesAlg(List<String> transactionDataHashesAlg) {
        this.transactionDataHashesAlg = transactionDataHashesAlg;
    }
}
