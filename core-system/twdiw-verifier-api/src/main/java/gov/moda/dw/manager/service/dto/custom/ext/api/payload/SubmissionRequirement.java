
package gov.moda.dw.manager.service.dto.custom.ext.api.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmissionRequirement {

    @JsonProperty("name")
    private String name;

    @JsonProperty("rule")
    private String rule;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("max")
    private Long max;

    @JsonProperty("from")
    private String from;

}
