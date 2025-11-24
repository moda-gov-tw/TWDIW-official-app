package gov.moda.dw.verifier.oidvp.presentationExchange.presentationDefinition;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonAutoDetect(
    fieldVisibility = Visibility.ANY,
    getterVisibility = Visibility.NONE,
    setterVisibility = Visibility.NONE)
@JsonInclude(value = Include.NON_NULL)
public class PresentationSubmission {

    private String id;

    @JsonProperty("definition_id")
    private String definitionId;

    @JsonProperty("descriptor_map")
    private List<DescriptorMap> descriptorMap;

    
    private PresentationSubmission() {
    }

    public PresentationSubmission(String definitionId, List<DescriptorMap> descriptorMap, String id) {
        this.definitionId = definitionId;
        this.descriptorMap = descriptorMap;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getDefinitionId() {
        return definitionId;
    }

    public List<DescriptorMap> getDescriptorMap() {
        return descriptorMap;
    }
}
