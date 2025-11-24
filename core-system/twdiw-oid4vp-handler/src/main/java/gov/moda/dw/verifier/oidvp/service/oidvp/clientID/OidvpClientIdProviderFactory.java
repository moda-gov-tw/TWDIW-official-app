package gov.moda.dw.verifier.oidvp.service.oidvp.clientID;

import com.nimbusds.oauth2.sdk.ResponseMode;
import gov.moda.dw.verifier.oidvp.model.oid4vp.ClientIdScheme;
import jakarta.annotation.Nullable;

public interface OidvpClientIdProviderFactory {

    /**
     * create OidvpClientIdProvider for specified ClientIdScheme.
     *
     * @param clientIdScheme client_id_scheme
     * @param responseMode   response_mode. If client_id_scheme is 'redirect_uri' this parameter must be specified,
     *                       otherwise it can be null.
     * @return OidvpClientIdProvider for specified ClientIdScheme
     */
    OidvpClientIdProvider createClientIdProvider(ClientIdScheme clientIdScheme, @Nullable ResponseMode responseMode);
}
