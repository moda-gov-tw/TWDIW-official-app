
package gov.moda.dw.manager.service.dto.custom.ext.api.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Constraints {

    private List<Field> fields;
    @JsonProperty("limit_disclosure")
    private String limitDisclosure;

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public String getLimitDisclosure() {
        return limitDisclosure;
    }

    public void setLimitDisclosure(String limitDisclosure) {
        this.limitDisclosure = limitDisclosure;
    }

}
