package gov.moda.dw.verifier.oidvp.model.oid4vp;

import java.util.Arrays;
import java.util.HashSet;

public class IdTokenType extends HashSet<IdTokenType.Value> {

    public static final IdTokenType SUBJECT = new IdTokenType(Value.SUBJECT);

    public static final IdTokenType ATTESTER = new IdTokenType(Value.ATTESTER);

    public static final IdTokenType SUBJECT_ATTESTER = new IdTokenType(Value.SUBJECT, Value.ATTESTER);


    public IdTokenType() {
        super();
    }

    public IdTokenType(Value... values) {
        super(Arrays.asList(values));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Value value : this) {
            if (!sb.isEmpty()) {
                sb.append(" ");
            }
            sb.append(value.toString());
        }
        return sb.toString();
    }

    public static class Value {

        public static final Value SUBJECT = new Value("subject_signed_id_token");

        public static final Value ATTESTER = new Value("attester_signed_id_token");

        private final String value;


        public Value(String value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object object) {
            return object instanceof Value && toString().equals(object.toString());
        }

        @Override
        public String toString() {
            return value;
        }
    }
}
