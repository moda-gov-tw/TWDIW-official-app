package gov.moda.dw.verifier.oidvp.presentationExchange.inputDescriptor;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;

@JsonAutoDetect(
    fieldVisibility = Visibility.ANY,
    getterVisibility = Visibility.NONE,
    setterVisibility = Visibility.NONE)
@JsonInclude(value = Include.NON_NULL)
public class Constraints {

    Statuses statuses;

    @JsonInclude(value = Include.NON_EMPTY)
    private List<Field> fields;

    @JsonProperty("limit_disclosure")
    private DisclosureLimitation limitDisclosure;

    @JsonProperty("subject_is_issuer")
    SubjectIsIssuer subjectIsIssuer;

    @JsonProperty("is_holder")
    @JsonInclude(value = Include.NON_EMPTY)
    List<IsHolder> isHolder;

    @JsonProperty("same_subject")
    @JsonInclude(value = Include.NON_EMPTY)
    List<SameSubject> sameSubject;


    private Constraints() {
    }

    private Constraints(Builder builder) {
        fields = builder.fields;
        statuses = builder.statuses;
        limitDisclosure = builder.limitDisclosure;
        subjectIsIssuer = builder.subjectIsIssuer;
        isHolder = builder.isHolder;
        sameSubject = builder.sameSubject;
    }

    public List<Field> getFields() {
        return fields;
    }

    public List<IsHolder> getIsHolder() {
        return isHolder;
    }

    public DisclosureLimitation getLimitDisclosure() {
        return limitDisclosure;
    }

    public List<SameSubject> getSameSubject() {
        return sameSubject;
    }

    public Statuses getStatuses() {
        return statuses;
    }

    public SubjectIsIssuer getSubjectIsIssuer() {
        return subjectIsIssuer;
    }

    public enum DisclosureLimitation {
        REQUIRED("required"),

        PREFERRED("preferred");

        private final String value;

        DisclosureLimitation(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }
    }

    public static final class Builder {

        private List<Field> fields;
        private Statuses statuses;
        private DisclosureLimitation limitDisclosure;
        private SubjectIsIssuer subjectIsIssuer;
        private List<IsHolder> isHolder;
        private List<SameSubject> sameSubject;

        public Builder() {
        }

        public Builder setFields(List<Field> fields) {
            this.fields = fields;
            return this;
        }

        public Builder setStatuses(Statuses statuses) {
            this.statuses = statuses;
            return this;
        }

        public Builder setLimitDisclosure(DisclosureLimitation limitDisclosure) {
            this.limitDisclosure = limitDisclosure;
            return this;
        }

        public Builder setSubjectIsIssuer(SubjectIsIssuer subjectIsIssuer) {
            this.subjectIsIssuer = subjectIsIssuer;
            return this;
        }

        public Builder setIsHolder(List<IsHolder> isHolder) {
            this.isHolder = isHolder;
            return this;
        }

        public Builder setSameSubject(List<SameSubject> sameSubject) {
            this.sameSubject = sameSubject;
            return this;
        }

        public Constraints build() {
            return new Constraints(this);
        }
    }
}
