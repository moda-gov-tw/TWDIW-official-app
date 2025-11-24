package gov.moda.dw.verifier.oidvp.presentationExchange.inputDescriptor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;

@JsonInclude(value = Include.NON_NULL)
public class Statuses {

    private StatusesObject active;
    private StatusesObject suspended;
    private StatusesObject revoked;


    public StatusesObject getActive() {
        return active;
    }

    public void setActive(StatusesObject active) {
        this.active = active;
    }

    public StatusesObject getRevoked() {
        return revoked;
    }

    public void setRevoked(StatusesObject revoked) {
        this.revoked = revoked;
    }

    public StatusesObject getSuspended() {
        return suspended;
    }

    public void setSuspended(StatusesObject suspended) {
        this.suspended = suspended;
    }

    public static class StatusesObject {

        protected Directive directive;

        @JsonInclude(value = Include.NON_EMPTY)
        protected List<String> type;

        public Directive getDirective() {
            return directive;
        }

        public void setDirective(Directive directive) {
            this.directive = directive;
        }

        public List<String> getType() {
            return type;
        }

        public void setType(List<String> type) {
            this.type = type;
        }
    }

    public enum Directive {
        required,
        allowed,
        disallowed
    }
}
