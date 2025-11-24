package gov.moda.dw.verifier.oidvp.model.oid4vp;

import com.fasterxml.jackson.annotation.JsonValue;
import com.nimbusds.oauth2.sdk.id.ClientID;

public enum ClientIdScheme {

    PRE_REGISTERED("pre-registered"),

    REDIRECT_URI("redirect_uri"),

    DID("did"),

    VERIFIER_ATTESTATION("verifier_attestation");

    private final String value;

    ClientIdScheme(String value) {
        this.value = value;
    }

    public static ClientIdScheme parse(String clientIdSchemeValue) {
        if (clientIdSchemeValue == null) {
            throw new IllegalArgumentException("clientIdSchemeValue must not be null");
        }
        for (ClientIdScheme clientIdScheme : values()) {
            if (clientIdScheme.value.equals(clientIdSchemeValue)) {
                return clientIdScheme;
            }
        }
        throw new IllegalArgumentException("unknown client_id_scheme for clientIdSchemeValue: " + clientIdSchemeValue);
    }

    public static ClientIdScheme parseByClientID(ClientID clientID) {
        if (clientID == null) {
            throw new IllegalArgumentException("clientID must not be null");
        }
        return parseByClientID(clientID.getValue());
    }

    public static ClientIdScheme parseByClientID(String clientID) {
        if (clientID == null || clientID.isEmpty()) {
            throw new IllegalArgumentException("client_id value must not be null or empty");
        }
        String[] clientIdSplits = clientID.split(":", 2);
        return clientIdSplits.length == 1 ? PRE_REGISTERED : parse(clientIdSplits[0]);
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getValue();
    }
}
