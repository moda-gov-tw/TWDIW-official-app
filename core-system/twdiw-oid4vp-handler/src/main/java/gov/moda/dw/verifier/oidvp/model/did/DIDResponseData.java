package gov.moda.dw.verifier.oidvp.model.did;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.Map;

@JsonInclude(Include.NON_NULL)
public class DIDResponseData {

    /**
     * did document
     */
    private Map<String, Object> did;

    /**
     * did
     */
    private String id;

    /**
     * did status.
     * <li> {@code 0}-待審核
     * <li> {@code 1}-有效
     * <li> {@code 2}-已註銷
     * <li> {@code 3}-審核未通過
     * <li> {@code 4}-已審核上鏈中
     * <li> {@code 5}-註銷中
     */
    private Integer status;

    private String txHash;

    @JsonAnySetter
    @JsonInclude(value = Include.NON_EMPTY, content = Include.NON_NULL)
    private Map<String, Object> customParams;

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Object> getDid() {
        return did;
    }

    public void setDid(Map<String, Object> did) {
        this.did = did;
    }

    public Map<String, Object> getCustomParams() {
        return customParams;
    }

    public void setCustomParams(Map<String, Object> customParams) {
        this.customParams = customParams;
    }
}
