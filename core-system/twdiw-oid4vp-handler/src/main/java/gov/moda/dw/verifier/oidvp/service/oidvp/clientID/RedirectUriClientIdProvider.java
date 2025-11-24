package gov.moda.dw.verifier.oidvp.service.oidvp.clientID;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.oauth2.sdk.id.ClientID;
import gov.moda.dw.verifier.oidvp.model.oid4vp.ClientIdScheme;
import java.net.URI;

public class RedirectUriClientIdProvider implements OidvpClientIdProvider {

    private final URI redirectOrResponseURI;

    public RedirectUriClientIdProvider(URI redirectOrResponseURI) {
        this.redirectOrResponseURI = redirectOrResponseURI;
    }

    @Override
    public ClientIdScheme getClientIdScheme() {
        return ClientIdScheme.REDIRECT_URI;
    }

    @Override
    public ClientID getOriginalClientID() {
        return new ClientID(redirectOrResponseURI.toString());
    }

    @Override
    public JWK getSigningJWK() {
        // client_id_scheme=redirect_uri can not sign request
        return null;
    }
}
