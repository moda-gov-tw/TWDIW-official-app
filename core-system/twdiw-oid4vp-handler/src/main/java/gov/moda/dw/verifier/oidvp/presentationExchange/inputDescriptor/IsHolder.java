package gov.moda.dw.verifier.oidvp.presentationExchange.inputDescriptor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(value = Include.NON_NULL)
public class IsHolder {

    @JsonProperty("field_id")
    @JsonInclude(value = Include.NON_EMPTY)
    private String[] fieldId;

    private Directive directive;

    
    private IsHolder() {
    }

    public IsHolder(String[] fieldId, Directive directive) {
        this.fieldId = fieldId;
        this.directive = directive;
    }

    public Directive getDirective() {
        return directive;
    }

    public String[] getFieldId() {
        return fieldId;
    }
}
