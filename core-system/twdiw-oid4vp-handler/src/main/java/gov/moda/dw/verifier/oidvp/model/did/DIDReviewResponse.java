package gov.moda.dw.verifier.oidvp.model.did;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.Map;

@JsonInclude(Include.NON_NULL)
public class DIDReviewResponse extends DIDBaseResponse {

    private DIDResponseData data;

    @JsonAnySetter
    @JsonInclude(value = Include.NON_EMPTY, content = Include.NON_NULL)
    private Map<String, Object> customParams;

    public DIDResponseData getData() {
        return data;
    }

    public void setData(DIDResponseData data) {
        this.data = data;
    }

    public Map<String, Object> getCustomParams() {
        return customParams;
    }

    public void setCustomParams(Map<String, Object> customParams) {
        this.customParams = customParams;
    }
}
