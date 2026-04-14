
package gov.moda.dw.manager.service.dto.custom.ext.api.payload;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InputDescriptor {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("group")
    private List<String> group;

    @JsonProperty("constraints")
    private Constraints constraints;

}
