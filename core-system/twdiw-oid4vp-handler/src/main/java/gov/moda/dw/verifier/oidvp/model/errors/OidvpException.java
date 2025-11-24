package gov.moda.dw.verifier.oidvp.model.errors;

import gov.moda.dw.verifier.oidvp.common.OidvpErrorCode.OidvpError;

public class OidvpException extends BaseOidvpException {

    public OidvpException(OidvpError oidvpError, Throwable cause) {
        super(oidvpError, cause);
    }

    public OidvpException(OidvpError oidvpError, String detailMessage, Throwable cause) {
        super(oidvpError, detailMessage, cause);
    }

    public OidvpException(OidvpError oidvpError, String detailMessage) {
        super(oidvpError, detailMessage);
    }

    public OidvpException(OidvpError oidvpError) {
        super(oidvpError);
    }
}
