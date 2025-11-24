package gov.moda.dw.verifier.oidvp.service.oidvp.clientID;

import com.nimbusds.oauth2.sdk.ResponseMode;
import gov.moda.dw.verifier.oidvp.config.OidvpConfig;
import gov.moda.dw.verifier.oidvp.model.oid4vp.ClientIdScheme;
import gov.moda.dw.verifier.oidvp.service.did.DIDService;
import gov.moda.dw.verifier.oidvp.service.oidvp.OID4VP;
import jakarta.annotation.Nullable;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class DefaultOidvpClientIdProviderFactory implements OidvpClientIdProviderFactory {

    private static DIDClientIdProvider didClientIdProvider;
    private static RedirectUriClientIdProvider redirectUriClientIdProvider;
    private static RedirectUriClientIdProvider responseUriClientIdProvider;

    public DefaultOidvpClientIdProviderFactory(DIDService didService, OidvpConfig oidvpConfig) {
        didClientIdProvider = new DIDClientIdProvider(didService);
        redirectUriClientIdProvider = new RedirectUriClientIdProvider(Objects.requireNonNull(oidvpConfig.getRedirectUri()));
        responseUriClientIdProvider = new RedirectUriClientIdProvider(Objects.requireNonNull(oidvpConfig.getResponseUri()));
    }

    @Override
    public OidvpClientIdProvider createClientIdProvider(ClientIdScheme clientIdScheme, @Nullable ResponseMode responseMode) {
        if (clientIdScheme == null) {
            throw new IllegalArgumentException("clientIdScheme must not be null");
        }

        return switch (clientIdScheme) {
            case DID -> didClientIdProvider;
            case REDIRECT_URI -> {
                if (responseMode == null) {
                    throw new IllegalArgumentException("when client_id_scheme is 'redirect_uri' responseMode must not be null.");
                }
                yield OID4VP.isUsingResponseURI(responseMode) ? responseUriClientIdProvider : redirectUriClientIdProvider;
            }
            default ->
                throw new UnsupportedOperationException("unsupported ClientIdProvider for clientIdScheme: " + clientIdScheme);
        };
    }
}
