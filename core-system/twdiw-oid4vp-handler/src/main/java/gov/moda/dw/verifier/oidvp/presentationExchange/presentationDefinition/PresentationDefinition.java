package gov.moda.dw.verifier.oidvp.presentationExchange.presentationDefinition;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import gov.moda.dw.verifier.oidvp.presentationExchange.inputDescriptor.FormatDescription;
import gov.moda.dw.verifier.oidvp.presentationExchange.inputDescriptor.FormatRegistry;
import gov.moda.dw.verifier.oidvp.presentationExchange.inputDescriptor.InputDescriptor;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@JsonAutoDetect(
    fieldVisibility = Visibility.ANY,
    getterVisibility = Visibility.NONE,
    setterVisibility = Visibility.NONE)
@JsonInclude(value = Include.NON_NULL)
public class PresentationDefinition {

    /**
     * required
     */
    private String id;

    private String name;

    private String purpose;

    @JsonProperty("submission_requirements")
    @JsonInclude(value = Include.NON_EMPTY)
    private List<SubmissionRequirement> submissionRequirements;

    /**
     * required
     */
    @JsonProperty("input_descriptors")
    private List<InputDescriptor> inputDescriptors;

    @JsonInclude(value = Include.NON_EMPTY)
    private Map<FormatRegistry, FormatDescription> format;


    private PresentationDefinition() {
    }

    private PresentationDefinition(Builder builder) {
        id = builder.id;
        name = builder.name;
        purpose = builder.purpose;
        format = builder.format;
        submissionRequirements = builder.submissionRequirements;
        inputDescriptors = builder.inputDescriptors;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPurpose() {
        return purpose;
    }

    public Map<FormatRegistry, FormatDescription> getFormat() {
        return format;
    }

    public List<SubmissionRequirement> getSubmissionRequirements() {
        return submissionRequirements;
    }

    public List<InputDescriptor> getInputDescriptors() {
        return inputDescriptors;
    }

    public static final class Builder {

        private String id;
        private String name;
        private String purpose;
        private Map<FormatRegistry, FormatDescription> format = Collections.emptyMap();
        private List<SubmissionRequirement> submissionRequirements = Collections.emptyList();
        private List<InputDescriptor> inputDescriptors;

        public Builder(String id, List<InputDescriptor> inputDescriptors) {
            if (id == null || id.isEmpty()) {
                throw new IllegalArgumentException("'id' cannot be null or empty");
            }
            if (inputDescriptors == null || inputDescriptors.isEmpty()) {
                throw new IllegalArgumentException("'inputDescriptors' cannot be null or empty");
            }
            this.id = id;
            this.inputDescriptors = inputDescriptors;
        }

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setPurpose(String purpose) {
            this.purpose = purpose;
            return this;
        }

        public Builder setFormat(Map<FormatRegistry, FormatDescription> format) {
            this.format = format;
            return this;
        }

        public Builder setSubmissionRequirements(
            List<SubmissionRequirement> submissionRequirements
        ) {
            this.submissionRequirements = submissionRequirements;
            return this;
        }

        public Builder setInputDescriptors(List<InputDescriptor> inputDescriptors) {
            this.inputDescriptors = inputDescriptors;
            return this;
        }

        public PresentationDefinition build() {
            return new PresentationDefinition(this);
        }
    }

    public void setId(String id) {
        this.id = id;
    }
}
