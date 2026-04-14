
package gov.moda.dw.manager.domain.outside.vdr.category;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Data {

    @JsonProperty
    private Long count;

    @JsonProperty
    private List<DataDid> dids;

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public List<DataDid> getDids() {
        return dids;
    }

    public void setDids(List<DataDid> dids) {
        this.dids = dids;
    }

}
