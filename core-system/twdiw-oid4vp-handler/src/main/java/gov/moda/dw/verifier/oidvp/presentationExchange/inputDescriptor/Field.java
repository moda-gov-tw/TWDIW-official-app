package gov.moda.dw.verifier.oidvp.presentationExchange.inputDescriptor;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@JsonAutoDetect(
    fieldVisibility = Visibility.ANY,
    getterVisibility = Visibility.NONE,
    setterVisibility = Visibility.NONE)
@JsonInclude(value = Include.NON_NULL)
public class Field {

    @JsonInclude(value = Include.NON_EMPTY)
    private List<String> path;

    private String id;

    private String purpose;

    private String name;

    /**
     * filter's value is a JSON Schema descriptor
     *
     * <p>see specification <a href="https://json-schema.org/specification">JSON Schema</a>
     */
    @JsonInclude(value = Include.NON_EMPTY)
    private Map<String, Object> filter;

    private Boolean optional = null;

    @JsonProperty("intent_to_retain")
    private Boolean intentToRetain = null;

    private Predicate predicate;

    private Field() {
    }

    private Field(Builder builder) {
        path = builder.path;
        id = builder.id;
        purpose = builder.purpose;
        name = builder.name;
        filter = builder.filter;
        optional = builder.optional;
        intentToRetain = builder.intentToRetain;
        predicate = builder.predicate;
    }

    public List<String> getPath() {
        return path;
    }

    public String getId() {
        return id;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getName() {
        return name;
    }

    public Map<String, Object> getFilter() {
        return filter;
    }

    public Boolean getIntentToRetain() {
        return intentToRetain;
    }

    public Predicate getPredicate() {
        return predicate;
    }

    public Boolean getOptional() {
        return optional;
    }

    public static final class Builder {

        private List<String> path = Collections.emptyList();
        private String id;
        private String purpose;
        private String name;
        private Map<String, Object> filter = Collections.emptyMap();
        private Boolean optional = null;
        private Boolean intentToRetain = null;
        private Predicate predicate;


        public Builder(List<String> path) {
            if (path == null || path.equals(Collections.emptyList())) {
                throw new IllegalArgumentException("'path' must not be null or empty");
            }
            this.path = path;
        }

        public Builder(List<String> path, Map<String, Object> filter, Predicate predicate) {
            if (path == null || path.equals(Collections.emptyList())) {
                throw new IllegalArgumentException("'path' must not be null or empty");
            }
            if (filter == null || filter.isEmpty()) {
                throw new IllegalArgumentException("'filter' must not be null or empty");
            }
            if (predicate == null) {
                throw new IllegalArgumentException("'predicate' must not be null or empty");
            }
            this.path = path;
            this.filter = filter;
            this.predicate = predicate;
        }

        public Builder setPath(List<String> path) {
            this.path = path;
            return this;
        }

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setPurpose(String purpose) {
            this.purpose = purpose;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setFilter(Map<String, Object> filter) {
            this.filter = filter;
            return this;
        }

        public Builder setOptional(Boolean optional) {
            this.optional = optional;
            return this;
        }

        public Builder setIntentToRetain(Boolean intentToRetain) {
            this.intentToRetain = intentToRetain;
            return this;
        }

        public Builder setPredicate(Predicate predicate) {
            this.predicate = predicate;
            return this;
        }

        public Field build() {
            return new Field(this);
        }
    }

    public enum Predicate {
        required,
        preferred
    }
}
