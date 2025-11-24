package gov.moda.dw.verifier.oidvp.model.errors;

import gov.moda.dw.verifier.oidvp.common.OidvpErrorCode.OidvpError;

public class JWKStoreOperationException extends OidvpException {

    public JWKStoreOperationException(String detailMessage, Throwable cause) {
        super(OidvpError.JWK_STORE_OPERATION_ERROR, detailMessage, cause);
    }

    public JWKStoreOperationException(String detailMessage) {
        super(OidvpError.JWK_STORE_OPERATION_ERROR, detailMessage);
    }
}
