
package gov.moda.dw.manager.service.dto.custom.ext.api.payload;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PresentationDefinitionDTO {

    @JsonProperty("id")
    private String id;

    @JsonProperty("purpose")
    private String purpose; // 授權單位名稱、授權條款 URL、驗證情境主題、授權目的

    @JsonProperty("submission_requirements")
    private List<SubmissionRequirement> submissionRequirements;

    @JsonProperty("input_descriptors")
    private List<InputDescriptor> inputDescriptors;

}
