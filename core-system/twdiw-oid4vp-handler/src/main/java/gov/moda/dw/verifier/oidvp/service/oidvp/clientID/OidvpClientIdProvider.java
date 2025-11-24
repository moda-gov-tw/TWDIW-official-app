package gov.moda.dw.verifier.oidvp.service.oidvp.clientID;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.oauth2.sdk.id.ClientID;
import gov.moda.dw.verifier.oidvp.model.oid4vp.ClientIdScheme;

public interface OidvpClientIdProvider {

    /**
     * oidvp client_id={@code <client_id_scheme>:<orig_client_id>}
     * <br>
     * get the {@code client_id_scheme} of oidvp client_id
     *
     * @return ClientIdScheme ({@code client_id_scheme})
     */
    ClientIdScheme getClientIdScheme();

    /**
     * oidvp client_id={@code <client_id_scheme>:<orig_client_id>}
     * <br>
     * get the {@code orig_client_id} of oidvp client_id
     *
     * @return Original ClientID ({@code orig_client_id})
     */
    ClientID getOriginalClientID();

    /**
     * get the jwk for signing the Authorization Request correspond to this client_id, if the client_id_scheme were able
     * to sign the Authorization Request
     *
     * @return JWK
     */
    JWK getSigningJWK();
}
