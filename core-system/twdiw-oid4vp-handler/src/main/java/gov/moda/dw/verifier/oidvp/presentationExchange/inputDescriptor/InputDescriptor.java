package gov.moda.dw.verifier.oidvp.presentationExchange.inputDescriptor;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@JsonAutoDetect(
    fieldVisibility = Visibility.ANY,
    getterVisibility = Visibility.NONE,
    setterVisibility = Visibility.NONE)
@JsonInclude(value = Include.NON_NULL)
public class InputDescriptor {

    /**
     * required
     */
    private String id;

    private String name;

    private String purpose;

    @JsonInclude(value = Include.NON_EMPTY)
    private Map<FormatRegistry, FormatDescription> format;

    /**
     * required
     */
    private Constraints constraints;

    @JsonInclude(value = Include.NON_EMPTY)
    private List<String> group;


    private InputDescriptor() {
    }

    private InputDescriptor(Builder builder) {
        id = builder.id;
        name = builder.name;
        purpose = builder.purpose;
        format = builder.format;
        constraints = builder.constraints;
        group = builder.group;
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

    public Constraints getConstraints() {
        return constraints;
    }

    public List<String> getGroup() {
        return group;
    }


    public static final class Builder {

        private String id;
        private String name;
        private String purpose;
        private Map<FormatRegistry, FormatDescription> format = Collections.emptyMap();
        private Constraints constraints;
        private List<String> group;

        public Builder(String id, Constraints constraints) {
            if (id == null || id.isEmpty()) {
                throw new IllegalArgumentException("'id' cannot be null or empty");
            }
            if (constraints == null) {
                throw new IllegalArgumentException("'constraints' cannot be null");
            }
            this.id = id;
            this.constraints = constraints;
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

        public Builder setConstraints(Constraints constraints) {
            this.constraints = constraints;
            return this;
        }

        public Builder setGroup(List<String> group) {
            this.group = group;
            return this;
        }

        public InputDescriptor build() {
            return new InputDescriptor(this);
        }
    }
}
