package gov.moda.dw.verifier.oidvp.model.oid4vp;

import com.nimbusds.oauth2.sdk.id.ClientID;
import gov.moda.dw.verifier.oidvp.model.errors.BadOidvpParamException;
import gov.moda.dw.verifier.oidvp.service.oidvp.clientID.OidvpClientIdProvider;
import gov.moda.dw.verifier.oidvp.util.URIUtils;
import java.util.Objects;

/**
 * The Client Identifier for oidvp client_id={@code <client_id_scheme>:<orig_client_id>}
 */
public class OidvpClientIdentifier {

    /**
     * the {@code <client_id_scheme>} of oidvp client_id
     */
    private final ClientIdScheme clientIdScheme;

    /**
     * the {@code <orig_client_id>} of oidvp client_id
     */
    private final ClientID originalClientID;


    public OidvpClientIdentifier(OidvpClientIdProvider oidvpClientIdProvider) {
        this(oidvpClientIdProvider.getClientIdScheme(), oidvpClientIdProvider.getOriginalClientID());
    }

    public OidvpClientIdentifier(ClientIdScheme clientIdScheme, ClientID originalClientID) {
        this.clientIdScheme = Objects.requireNonNull(clientIdScheme, "clientIdScheme must be specified");
        this.originalClientID = setAndCheckOriginalClientID(clientIdScheme, originalClientID);
    }

    public static OidvpClientIdentifier parse(String fullClientID) {
        ClientIdScheme clientIdScheme = ClientIdScheme.parseByClientID(fullClientID);
        ClientID originalClientID;
        if (clientIdScheme == ClientIdScheme.DID || clientIdScheme == ClientIdScheme.PRE_REGISTERED) {
            originalClientID = new ClientID(fullClientID);
        } else {
            String[] split = fullClientID.split(":", 2);
            if (split.length != 2) {
                throw new IllegalArgumentException("The format of the fullClientID is invalid.");
            }
            originalClientID = new ClientID(split[1]);
        }
        return new OidvpClientIdentifier(clientIdScheme, originalClientID);
    }

    public ClientID getOriginalClientID() {
        return originalClientID;
    }

    public ClientIdScheme getClientIdScheme() {
        return clientIdScheme;
    }

    public ClientID toOidvpClientID() {
        if (clientIdScheme == ClientIdScheme.DID || clientIdScheme == ClientIdScheme.PRE_REGISTERED) {
            return originalClientID;
        } else {
            return new ClientID(clientIdScheme.getValue() + ":" + originalClientID);
        }
    }

    // not support all client_id_scheme yet
    private ClientID setAndCheckOriginalClientID(ClientIdScheme clientIdScheme, ClientID originalClientID) {
        if (originalClientID == null) {
            throw new IllegalArgumentException("originalClientID must be specified");
        }

        String clientIDValue = originalClientID.getValue();
        try {
            switch (clientIdScheme) {
                case DID -> {
                    if (!isValidDidIdFormat(clientIDValue)) {
                        throw new IllegalArgumentException();
                    }
                }
                case REDIRECT_URI -> {
                    if (!isValidRedirectIDFormat(clientIDValue)) {
                        throw new IllegalArgumentException();
                    }
                }
                case PRE_REGISTERED -> {
                    if (!isValidPreRegisteredIDFormat(clientIDValue)) {
                        throw new IllegalArgumentException();
                    }
                }
                case VERIFIER_ATTESTATION -> {
                    // the originalClientID MUST equal the 'sub' claim value in the Verifier attestation JWT
                }
                default ->
                    throw new UnsupportedOperationException("client_id_scheme: " + clientIdScheme + " is unsupported.");
            }
        } catch (IllegalArgumentException e) {
            throw new BadOidvpParamException("invalid orig_client_id format for client_id_scheme=" + clientIdScheme);
        }
        return originalClientID;
    }

    private boolean isValidRedirectIDFormat(String id) {
        return isValidHttpsIDFormat(id);
    }

    private boolean isValidPreRegisteredIDFormat(String id) {
        return !id.contains(":");
    }

    private boolean isValidHttpsIDFormat(String id) {
        return URIUtils.isValidURI(id);
    }

    private boolean isValidDidIdFormat(String id) {
        String[] split = id.split(":");
        if (split.length != 3) {
            return false;
        } else {
            return "did".equals(split[0]);
        }
    }
}
