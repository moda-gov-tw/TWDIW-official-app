package gov.moda.dw.verifier.oidvp.presentationExchange.presentationDefinition;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Collections;
import java.util.List;

@JsonAutoDetect(
    fieldVisibility = Visibility.ANY,
    getterVisibility = Visibility.NONE,
    setterVisibility = Visibility.NONE)
@JsonInclude(value = Include.NON_NULL)
public class SubmissionRequirement {

    private String name;

    private String purpose;

    private SubmissionRequirementRule rule;

    private Integer count;

    private Integer min;

    private Integer max;

    private String from;

    @JsonProperty("from_nested")
    @JsonInclude(value = Include.NON_EMPTY)
    private List<SubmissionRequirement> fromNested;


    private SubmissionRequirement() {
    }

    private SubmissionRequirement(Builder builder) {
        rule = builder.rule;
        from = builder.from;
        fromNested = builder.fromNested;
        name = builder.name;
        purpose = builder.purpose;
        count = builder.count;
        min = builder.min;
        max = builder.max;
    }

    public SubmissionRequirementRule getRule() {
        return rule;
    }

    public String getFrom() {
        return from;
    }

    public List<SubmissionRequirement> getFromNested() {
        return fromNested;
    }

    public String getName() {
        return name;
    }

    public String getPurpose() {
        return purpose;
    }

    public Integer getCount() {
        return count;
    }

    public Integer getMin() {
        return min;
    }

    public Integer getMax() {
        return max;
    }

    public static final class Builder {

        private SubmissionRequirementRule rule;
        private String from;
        private List<SubmissionRequirement> fromNested = Collections.emptyList();
        private String name;
        private String purpose;
        private Integer count;
        private Integer min;
        private Integer max;

        public Builder(SubmissionRequirementRule rule, String from) {
            if (rule == null) {
                throw new IllegalArgumentException("'rule' cannot be null");
            }
            if (from == null || from.isEmpty()) {
                throw new IllegalArgumentException("'from' cannot be null or empty");
            }
            this.rule = rule;
            this.from = from;
        }

        public Builder(SubmissionRequirementRule rule, List<SubmissionRequirement> fromNested) {
            if (rule == null) {
                throw new IllegalArgumentException("'rule' cannot be null");
            }
            if (fromNested == null || fromNested.isEmpty()) {
                throw new IllegalArgumentException("'fromNested' cannot be null or empty");
            }
            this.rule = rule;
            this.fromNested = fromNested;
        }

        public Builder setRule(SubmissionRequirementRule rule) {
            this.rule = rule;
            return this;
        }

        public Builder setFrom(String from) {
            this.from = from;
            return this;
        }

        public Builder setFromNested(List<SubmissionRequirement> fromNested) {
            this.fromNested = fromNested;
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

        public Builder setCount(Integer count) {
            this.count = count;
            return this;
        }

        public Builder setMin(Integer min) {
            this.min = min;
            return this;
        }

        public Builder setMax(Integer max) {
            this.max = max;
            return this;
        }

        public SubmissionRequirement build() {
            return new SubmissionRequirement(this);
        }
    }

    public enum SubmissionRequirementRule {
        ALL("all"),

        PICK("pick");

        private final String value;

        private SubmissionRequirementRule(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }
    }
}
