package gov.moda.dw.verifier.oidvp.model.errors;

import gov.moda.dw.verifier.oidvp.common.OidvpErrorCode.OidvpError;

public class OidvpRuntimeException extends BaseOidvpRuntimeException {

    public OidvpRuntimeException(OidvpError oidvpError, Throwable cause) {
        super(oidvpError, cause);
    }

    public OidvpRuntimeException(OidvpError oidvpError, String detailMessage, Throwable cause) {
        super(oidvpError, detailMessage, cause);
    }

    public OidvpRuntimeException(OidvpError oidvpError, String detailMessage) {
        super(oidvpError, detailMessage);
    }

    public OidvpRuntimeException(OidvpError oidvpError) {
        super(oidvpError);
    }
}
