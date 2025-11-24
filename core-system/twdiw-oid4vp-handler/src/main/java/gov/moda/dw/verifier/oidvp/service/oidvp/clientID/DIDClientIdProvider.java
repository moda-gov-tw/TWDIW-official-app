package gov.moda.dw.verifier.oidvp.service.oidvp.clientID;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.oauth2.sdk.id.ClientID;
import gov.moda.dw.verifier.oidvp.common.OidvpErrorCode.OidvpError;
import gov.moda.dw.verifier.oidvp.model.errors.OidvpException;
import gov.moda.dw.verifier.oidvp.model.errors.OidvpRuntimeException;
import gov.moda.dw.verifier.oidvp.model.oid4vp.ClientIdScheme;
import gov.moda.dw.verifier.oidvp.service.did.DIDService;

public class DIDClientIdProvider implements OidvpClientIdProvider {

    private final DIDService didService;

    public DIDClientIdProvider(DIDService didService) {
        this.didService = didService;
    }

    @Override
    public ClientIdScheme getClientIdScheme() {
        return ClientIdScheme.DID;
    }

    @Override
    public ClientID getOriginalClientID() {
        return new ClientID(getDID());
    }

    @Override
    public JWK getSigningJWK() {
        try {
            return didService.getDIDKey();
        } catch (OidvpException e) {
            throw new OidvpRuntimeException(e.getOidvpError(), e);
        }
    }

    private String getDID() {
        String did;
        // check if DID is existed
        try {
            did = didService.getDID();
        } catch (Exception e) {
            throw new OidvpRuntimeException(OidvpError.GET_DID_ERROR, e.getMessage(), e);
        }

        // check if DID Key is existed
        try {
            didService.getDIDKey();
        } catch (Exception e) {
            throw new OidvpRuntimeException(OidvpError.GET_DID_ERROR, "DID key is not valid. The DID registration might not have been successfully completed.", e);
        }
        return did;
    }
}
